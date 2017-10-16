/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.ec4e.internal.validation;

import static org.eclipse.ec4e.internal.EditorConfigPreferenceStore.EDITOR_INSERT_FINAL_NEWLINE;
import static org.eclipse.ec4e.internal.EditorConfigPreferenceStore.EDITOR_TRIM_TRAILING_WHITESPACE;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ec4e.internal.IDEEditorConfigManager;
import org.eclipse.ec4e.internal.validation.marker.MarkerUtils;
import org.eclipse.ec4j.model.optiontypes.OptionNames;
import org.eclipse.ec4j.model.optiontypes.OptionType;
import org.eclipse.ec4j.model.optiontypes.OptionTypeRegistry;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.MarkerUtilities;

/**
 * Validate content of any editor where .editorconfig options
 * 'trim_trailing_whitespace' and/or 'insert_final_newline' must be applied.
 *
 */
public class ValidateAppliedOptionsStrategy
		implements IReconcilingStrategy, IReconcilingStrategyExtension, IPropertyChangeListener, IReconciler {

	// preference store & file resource of the editor to validate
	private final IPreferenceStore preferenceStore;
	private final IResource resource;

	// Text viewer of the editor to validate
	private ITextViewer textViewer;
	private OptionType<?> insertFinalNewlineType;
	private OptionType<?> trimTrailingWhitespaceType;

	public ValidateAppliedOptionsStrategy(IPreferenceStore preferenceStore, IResource resource) {
		this.preferenceStore = preferenceStore;
		this.resource = resource;
		OptionTypeRegistry registry = IDEEditorConfigManager.getInstance().getRegistry();
		insertFinalNewlineType = registry.getType(OptionNames.insert_final_newline.name());
		trimTrailingWhitespaceType = registry.getType(OptionNames.trim_trailing_whitespace.name());
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		if (textViewer == null) {
			return;
		}
		try {
			// Collect ec4e markers which have option type (coming from this validator)
			Set<IMarker> remainingMarkers = MarkerUtils.findEditorConfigMarkers(resource).stream().filter(marker -> {
				try {
					return MarkerUtils.getOptionType(marker) != null;
				} catch (CoreException e) {
					return false;
				}
			}).collect(Collectors.toSet());
			IDocument document = textViewer.getDocument();
			// Validate 'trim_trailing_whitespace' if needed
			IRegion region = DirtyRegion.REMOVE.equals(dirtyRegion.getType()) ? new Region(subRegion.getOffset(), 0)
					: subRegion;
			validateTrimTrailingWhiteSpace(document, region, remainingMarkers);
			// Validate 'insert_final_newline' if needed
			validateInsertFinalNewline(document, remainingMarkers);
			for (IMarker marker : remainingMarkers) {
				marker.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (EDITOR_TRIM_TRAILING_WHITESPACE.equals(event.getProperty())) {
			// apply of .editorconfig 'trim_trailing_whitespace' option to the editor
			validateOnlyTrimTrailingWhiteSpace();
		} else if (EDITOR_INSERT_FINAL_NEWLINE.equals(event.getProperty())) {
			// apply of .editorconfig 'insert_final_newline' option to the editor
			validateOnlyInsertFinalNewline();
		}
	}

	@Override
	public void install(ITextViewer textViewer) {
		this.textViewer = textViewer;
		preferenceStore.addPropertyChangeListener(this);
	}

	@Override
	public void uninstall() {
		this.textViewer = null;
		preferenceStore.removePropertyChangeListener(this);
	}

	/**
	 * Validate only 'trim_trailing_whitespace'.
	 */
	private void validateOnlyTrimTrailingWhiteSpace() {
		if (textViewer == null) {
			return;
		}
		try {
			Set<IMarker> remainingMarkers = MarkerUtils.findEditorConfigMarkers(resource).stream().filter(marker -> {
				try {
					return MarkerUtils.getOptionType(marker) == trimTrailingWhitespaceType;
				} catch (CoreException e) {
					return false;
				}
			}).collect(Collectors.toSet());
			// Validate the whole lines of the document.
			IDocument document = textViewer.getDocument();
			validateTrimTrailingWhiteSpace(document, new Region(0, document.getLength()), remainingMarkers);
			for (IMarker marker : remainingMarkers) {
				marker.delete();
			}
		} catch (CoreException | BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Validate 'trim_trailing_whitespace' of the lines where region changed if
	 * needed and update the given set of marker.
	 * 
	 * @param partition
	 *            the region which changed
	 * @param remainingMarkers
	 *            set of markers to update.
	 * @throws BadLocationException
	 */
	private void validateTrimTrailingWhiteSpace(IDocument document, IRegion partition, Set<IMarker> remainingMarkers)
			throws BadLocationException, CoreException {
		boolean trimTrailingWhiteSpace = preferenceStore.getBoolean(EDITOR_TRIM_TRAILING_WHITESPACE);
		if (!trimTrailingWhiteSpace) {
			return;
		}
		int startLine = document.getLineOfOffset(partition.getOffset());
		int endLine = document.getLineOfOffset(partition.getOffset() + partition.getLength());

		IRegion region = null;
		for (int i = startLine; i < endLine + 1; i++) {
			region = document.getLineInformation(i);
			if (region.getLength() == 0)
				continue;
			int lineStart = region.getOffset();
			int lineExclusiveEnd = lineStart + region.getLength();
			int j = lineExclusiveEnd - 1;
			while (j >= lineStart && Character.isWhitespace(document.getChar(j)))
				--j;
			++j;
			if (j < lineExclusiveEnd) {
				addOrUpdateMarker(j, lineExclusiveEnd, trimTrailingWhitespaceType, document, remainingMarkers);
			}
		}
		if (region != null) {
			for (IMarker marker : new HashSet<>(remainingMarkers)) {
				if (MarkerUtils.getOptionType(marker) == trimTrailingWhitespaceType) {
					int line = MarkerUtilities.getLineNumber(marker) + 1;
					if (line < startLine || line > endLine) {
						remainingMarkers.remove(marker);
					}
				}
			}
		}
	}

	/**
	 * Validate only 'insert_final_newline'
	 */
	private void validateOnlyInsertFinalNewline() {
		if (textViewer == null) {
			return;
		}
		try {
			Set<IMarker> remainingMarkers = MarkerUtils.findEditorConfigMarkers(resource).stream().filter(marker -> {
				try {
					return MarkerUtils.getOptionType(marker) == insertFinalNewlineType;
				} catch (CoreException e) {
					return false;
				}
			}).collect(Collectors.toSet());
			IDocument document = textViewer.getDocument();
			validateInsertFinalNewline(document, remainingMarkers);
			for (IMarker marker : remainingMarkers) {
				marker.delete();
			}
		} catch (CoreException | BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Validate 'insert_final_newline' if needed and update the given set of marker.
	 * 
	 * @param document
	 *            the document to validate
	 * @param remainingMarkers
	 *            set of markers to update.
	 * @throws BadLocationException
	 */
	private void validateInsertFinalNewline(IDocument document, Set<IMarker> remainingMarkers)
			throws BadLocationException {
		boolean insertFinalNewline = preferenceStore.getBoolean(EDITOR_INSERT_FINAL_NEWLINE);
		if (!insertFinalNewline) {
			return;
		}
		// Check if there are an empty line at the end of the document.
		if (document.getLength() == 0) {
			return;
		}
		int line = document.getNumberOfLines() - 1;
		IRegion region = document.getLineInformation(line);
		if (region.getLength() > 0) {
			int end = region.getOffset() + region.getLength();
			int start = end - 1;
			addOrUpdateMarker(start, end, insertFinalNewlineType, document, remainingMarkers);
		}
	}

	/**
	 * Add or update marker error.
	 * 
	 * @param start
	 *            the start of the error.
	 * @param end
	 *            the end of the error.
	 * @param type
	 *            the option type where there are an error.
	 * @param document
	 *            the document to validate.
	 * @param remainingMarkers
	 *            set of markers to update.
	 */
	private void addOrUpdateMarker(int start, int end, OptionType<?> type, IDocument document,
			Set<IMarker> remainingMarkers) {
		try {
			IMarker associatedMarker = getExistingMarkerFor(start, end, type, remainingMarkers);
			if (associatedMarker == null) {
				associatedMarker = MarkerUtils.createEditorConfigMarker(resource);
			} else {
				remainingMarkers.remove(associatedMarker);
			}
			updateMarker(start, end, type, document, associatedMarker);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Update marker.
	 * 
	 * @param start
	 *            the start of the error.
	 * @param end
	 *            the end of the error.
	 * @param type
	 *            the option type where there are an error.
	 * @param document
	 *            the document to validate.
	 * @param marker
	 *            the marker to update.
	 */
	private void updateMarker(int start, int end, OptionType<?> type, IDocument document, IMarker marker) {
		try {
			MarkerUtils.setOptionType(marker, type);
			marker.setAttribute(IMarker.MESSAGE, getMessage(type));
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			if (resource.getType() != IResource.FILE) {
				return;
			}
			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, end);
			marker.setAttribute(IMarker.LINE_NUMBER, document.getLineOfOffset(start) + 1);
		} catch (CoreException | BadLocationException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the error message according the option type.
	 * 
	 * @param type
	 *            the option type.
	 * @return the error message according the option type.
	 */
	private String getMessage(OptionType<?> type) {
		if (insertFinalNewlineType == type) {
			return "Insert final newline";
		} else if (trimTrailingWhitespaceType == type) {
			return "Trim traling whitespace";
		}
		return null;
	}

	/**
	 * Return the existing marker and null otherwise.
	 * 
	 * @param start
	 *            the start of the error.
	 * @param end
	 *            the end of the error.
	 * @param type
	 *            the option type where there are an error.
	 * @param remainingMarkers
	 *            set of markers to update.
	 * @return the existing marker and null otherwise.
	 */
	private IMarker getExistingMarkerFor(int start, int end, OptionType<?> type, Set<IMarker> remainingMarkers) {
		try {
			if (insertFinalNewlineType == type) {
				for (IMarker marker : remainingMarkers) {
					if (type == MarkerUtils.getOptionType(marker)) {
						return marker;
					}
				}
			} else if (trimTrailingWhitespaceType == type) {
				for (IMarker marker : remainingMarkers) {
					int startOffset = MarkerUtilities.getCharStart(marker);
					int endOffset = MarkerUtilities.getCharEnd(marker);
					if (type == MarkerUtils.getOptionType(marker)) {
						if (start <= startOffset && end >= endOffset) {
							return marker;
						}
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {
		// Do nothing
	}

	@Override
	public void initialReconcile() {
		// Do nothing
	}

	@Override
	public void setDocument(IDocument document) {
		// Do nothing
	}

	@Override
	public IReconcilingStrategy getReconcilingStrategy(String contentType) {
		return null;
	}

	@Override
	public void reconcile(IRegion partition) {
		// Do nothing
	}

}
