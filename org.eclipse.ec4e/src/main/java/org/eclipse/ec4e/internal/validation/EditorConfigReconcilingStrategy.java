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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.filebuffers.ITextFileBuffer;
import org.eclipse.core.filebuffers.ITextFileBufferManager;
import org.eclipse.core.filebuffers.LocationKind;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ec4e.services.model.options.ConfigPropertyType;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.texteditor.MarkerUtilities;

public class EditorConfigReconcilingStrategy
		implements IReconcilingStrategy, IReconcilingStrategyExtension, IPropertyChangeListener {

	public static final String EC_OPTION_TYPE = "ecOptionType"; //$NON-NLS-1$
	public static final String PROBLEM_MARKER_TYPE = "org.eclipse.ec4e.problem"; //$NON-NLS-1$

	private final IPreferenceStore preferenceStore;
	private boolean trimTrailingWhiteSpace;
	private boolean insertFinalNewline;

	private IResource resource;

	private ITextViewer textViewer;

	public EditorConfigReconcilingStrategy(IPreferenceStore preferenceStore, IResource resource) {
		this.preferenceStore = preferenceStore;
		this.resource = resource;
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {

	}

	@Override
	public void initialReconcile() {

	}

	@Override
	public void setDocument(IDocument document) {

	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		if (textViewer == null) {
			return;
		}
		try {
			Set<IMarker> remainingMarkers = new HashSet<>(
					Arrays.asList(resource.findMarkers(PROBLEM_MARKER_TYPE, false, IResource.DEPTH_ONE)));
			validateTrimTrailingWhiteSpace(remainingMarkers, subRegion);
			validateInsertFinalNewline(remainingMarkers);
			for (IMarker marker : remainingMarkers) {
				marker.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void install(ITextViewer textViewer) {
		this.textViewer = textViewer;
		preferenceStore.addPropertyChangeListener(this);
	}

	public void uninstall() {
		this.textViewer = null;
		preferenceStore.removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (EDITOR_TRIM_TRAILING_WHITESPACE.equals(event.getProperty())) {
			this.trimTrailingWhiteSpace = preferenceStore.getBoolean(EDITOR_TRIM_TRAILING_WHITESPACE);
			validateOnlyTrimTrailingWhiteSpace();
		} else if (EDITOR_INSERT_FINAL_NEWLINE.equals(event.getProperty())) {
			this.insertFinalNewline = preferenceStore.getBoolean(EDITOR_INSERT_FINAL_NEWLINE);
			validateOnlyInsertFinalNewline();
		}
	}

	@Override
	public void reconcile(IRegion partition) {
		System.err.println();
	}

	private void validateOnlyTrimTrailingWhiteSpace() {
		if (textViewer == null) {
			return;
		}
		try {
			Set<IMarker> remainingMarkers = Arrays
					.asList(resource.findMarkers(PROBLEM_MARKER_TYPE, false, IResource.DEPTH_ONE)).stream()
					.filter(marker -> {
						try {
							return marker.getAttribute(EC_OPTION_TYPE) == ConfigPropertyType.TRIM_TRAILING_WHITESPACE;
						} catch (CoreException e) {
							return false;
						}
					}).collect(Collectors.toSet());
			validateTrimTrailingWhiteSpace(remainingMarkers, new Region(0, textViewer.getDocument().getLength()));
			for (IMarker marker : remainingMarkers) {
				marker.delete();
			}
		} catch (CoreException | BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void validateTrimTrailingWhiteSpace(Set<IMarker> remainingMarkers, IRegion partition)
			throws BadLocationException, CoreException {
		if (!trimTrailingWhiteSpace) {
			return;
		}
		IDocument document = textViewer.getDocument();
		int startLine = document.getLineOfOffset(partition.getOffset());
		int endLine = document.getLineOfOffset(partition.getOffset() + partition.getLength());

		IRegion region = null;
		Integer maxStart = null, maxEnd = null;
		for (int i = startLine; i < endLine + 1; i++) {
			region = document.getLineInformation(i);
			if (region.getLength() == 0)
				continue;
			if (maxStart == null) {
				maxStart = region.getOffset();
			}
			int lineStart = region.getOffset();
			int lineExclusiveEnd = lineStart + region.getLength();
			int j = lineExclusiveEnd - 1;
			while (j >= lineStart && Character.isWhitespace(document.getChar(j)))
				--j;
			++j;
			if (j < lineExclusiveEnd) {
				addError(j, lineExclusiveEnd, ConfigPropertyType.TRIM_TRAILING_WHITESPACE, remainingMarkers);
			}
		}
		if (region != null) {
			maxEnd = region.getOffset() + region.getLength();
			for (IMarker marker : new HashSet<>(remainingMarkers)) {
				if (marker.getAttribute(EC_OPTION_TYPE) == ConfigPropertyType.TRIM_TRAILING_WHITESPACE) {
					int line = MarkerUtilities.getLineNumber(marker) + 1;
					if (line < startLine || line > endLine) {
						remainingMarkers.remove(marker);
					}
				}
			}
		}
	}

	private void validateOnlyInsertFinalNewline() {
		if (textViewer == null) {
			return;
		}
		try {
			Set<IMarker> remainingMarkers = Arrays
					.asList(resource.findMarkers(PROBLEM_MARKER_TYPE, false, IResource.DEPTH_ONE)).stream()
					.filter(marker -> {
						try {
							return marker.getAttribute(EC_OPTION_TYPE) == ConfigPropertyType.INSERT_FINAL_NEWLINE;
						} catch (CoreException e) {
							return false;
						}
					}).collect(Collectors.toSet());
			validateInsertFinalNewline(remainingMarkers);
			for (IMarker marker : remainingMarkers) {
				marker.delete();
			}
		} catch (CoreException | BadLocationException e) {
			e.printStackTrace();
		}
	}

	private void validateInsertFinalNewline(Set<IMarker> remainingMarkers) throws BadLocationException {
		if (!insertFinalNewline) {
			return;
		}
		IDocument document = textViewer.getDocument();
		if (document.getLength() == 0) {
			return;
		}
		int line = document.getNumberOfLines() - 1;
		IRegion region = document.getLineInformation(line);
		if (region.getLength() > 0) {
			int end = region.getOffset() + region.getLength();
			int start = end - 1;
			addError(start, end, ConfigPropertyType.INSERT_FINAL_NEWLINE, remainingMarkers);
		}
	}

	private void addError(int start, int end, ConfigPropertyType<?> type, Set<IMarker> remainingMarkers) {
		try {
			IMarker associatedMarker = getExistingMarkerFor(resource, start, end, type, remainingMarkers);
			if (associatedMarker == null) {
				associatedMarker = resource.createMarker(PROBLEM_MARKER_TYPE);
			} else {
				remainingMarkers.remove(associatedMarker);
			}
			updateMarker(resource, start, end, type, associatedMarker);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	protected void updateMarker(IResource resource, int start, int end, ConfigPropertyType<?> type, IMarker marker) {
		try {
			marker.setAttribute(EC_OPTION_TYPE, type);
			marker.setAttribute(IMarker.MESSAGE, getMessage(type));
			marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_ERROR);
			if (resource.getType() != IResource.FILE) {
				return;
			}
			IFile file = (IFile) resource;
			ITextFileBufferManager manager = FileBuffers.getTextFileBufferManager();
			ITextFileBuffer textFileBuffer = manager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);

			if (textFileBuffer == null) {
				manager.connect(file.getFullPath(), LocationKind.IFILE, new NullProgressMonitor());
				textFileBuffer = manager.getTextFileBuffer(file.getFullPath(), LocationKind.IFILE);
			}

			IDocument document = textFileBuffer.getDocument();

			marker.setAttribute(IMarker.CHAR_START, start);
			marker.setAttribute(IMarker.CHAR_END, end);
			marker.setAttribute(IMarker.LINE_NUMBER, document.getLineOfOffset(start) + 1);
		} catch (CoreException | BadLocationException e) {
			e.printStackTrace();
		}
	}

	private String getMessage(ConfigPropertyType<?> type) {
		if (ConfigPropertyType.INSERT_FINAL_NEWLINE == type) {
			return "Insert final newline";
		} else if (ConfigPropertyType.TRIM_TRAILING_WHITESPACE == type) {
			return "Trim traling whitespace";
		}
		return null;
	}

	private IMarker getExistingMarkerFor(IResource resource, int start, int end, ConfigPropertyType<?> type,
			Set<IMarker> remainingMarkers) {
		ITextFileBuffer textFileBuffer = FileBuffers.getTextFileBufferManager()
				.getTextFileBuffer(resource.getFullPath(), LocationKind.IFILE);
		if (textFileBuffer == null) {
			return null;
		}
		try {
			if (ConfigPropertyType.INSERT_FINAL_NEWLINE == type) {
				for (IMarker marker : remainingMarkers) {
					if (type == marker.getAttribute(EC_OPTION_TYPE)) {
						return marker;
					}
				}
			} else if (ConfigPropertyType.TRIM_TRAILING_WHITESPACE == type) {
				for (IMarker marker : remainingMarkers) {
					int startOffset = MarkerUtilities.getCharStart(marker);
					int endOffset = MarkerUtilities.getCharEnd(marker);
					if (type == marker.getAttribute(EC_OPTION_TYPE)) {
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
}
