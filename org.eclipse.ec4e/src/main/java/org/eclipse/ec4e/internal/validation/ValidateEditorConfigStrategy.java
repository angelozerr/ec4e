package org.eclipse.ec4e.internal.validation;

import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.ec4j.core.parser.EditorConfigHandler;
import org.ec4j.core.parser.EditorConfigParser;
import org.ec4j.core.parser.ErrorEvent;
import org.ec4j.core.parser.ErrorHandler;
import org.ec4j.core.parser.ParseContext;
import org.ec4j.core.services.EditorConfigService;
import org.ec4j.core.parser.ValidatingHandler;
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
import org.eclipse.ec4e.internal.validation.marker.MarkerUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.ui.texteditor.MarkerUtilities;

public class ValidateEditorConfigStrategy
		implements IReconcilingStrategy, IReconcilingStrategyExtension, IReconciler {

	private ITextViewer textViewer;

	private IResource resource;

	public ValidateEditorConfigStrategy(IResource resource) {
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
			IDocument document = textViewer.getDocument();
			Set<IMarker> remainingMarkers = MarkerUtils.findEditorConfigMarkers(resource).stream().filter(marker -> {
				try {
					return MarkerUtils.getOptionType(marker) == null;
				} catch (CoreException e) {
					return false;
				}
			}).collect(Collectors.toSet());

			org.ec4j.core.Resource ec4jResource = org.ec4j.core.Resource.Resources.ofString(resource.getFullPath().toString(), document.get());
			EditorConfigParser parser = EditorConfigParser.default_();
			ValidatingHandler handler = new ValidatingHandler(org.ec4j.core.PropertyTypeRegistry.builder().defaults().build());
			parser.parse(ec4jResource, handler, new ErrorHandler() {
		        @Override
		        public void error(ParseContext context, ErrorEvent errorEvent) {
					int startOffset = errorEvent.getStart().offset;
					int endOffset = startOffset;
					if (errorEvent.getEnd() == null) {
						startOffset--;
					} else {
						endOffset = errorEvent.getEnd().offset;
					}
					addError(errorEvent.getMessage(), startOffset, endOffset, MarkerUtils.getSeverity(errorEvent.getErrorType()), remainingMarkers);
		        }
		    });

			for (IMarker marker : remainingMarkers) {
				marker.delete();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void reconcile(IRegion partition) {

	}

	@Override
	public void install(ITextViewer textViewer) {
		this.textViewer = textViewer;
	}

	@Override
	public void uninstall() {
		this.textViewer = null;
	}

	@Override
	public IReconcilingStrategy getReconcilingStrategy(String contentType) {
		return null;
	}

	private void addError(String message, int start, int end, int severity,
			Set<IMarker> remainingMarkers) {
		try {
			IMarker associatedMarker = getExistingMarkerFor(resource, message, start, end, remainingMarkers);
			if (associatedMarker == null) {
				associatedMarker = MarkerUtils.createEditorConfigMarker(resource);
			} else {
				remainingMarkers.remove(associatedMarker);
			}
			updateMarker(resource, message, start, end, severity, associatedMarker);
		} catch (CoreException e) {
			e.printStackTrace();
		}

	}

	private void updateMarker(IResource resource, String message, int start, int end, int severity,
			IMarker marker) {
		try {
			marker.setAttribute(IMarker.MESSAGE, message);
			marker.setAttribute(IMarker.SEVERITY, severity);
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

	private IMarker getExistingMarkerFor(IResource resource, String message, int start, int end,
			Set<IMarker> remainingMarkers) {
		ITextFileBuffer textFileBuffer = FileBuffers.getTextFileBufferManager()
				.getTextFileBuffer(resource.getFullPath(), LocationKind.IFILE);
		if (textFileBuffer == null) {
			return null;
		}
		try {
			for (IMarker marker : remainingMarkers) {
				int startOffset = MarkerUtilities.getCharStart(marker);
				int endOffset = MarkerUtilities.getCharEnd(marker);
				if (MarkerUtils.getOptionType(marker) == null && message.equals(MarkerUtilities.getMessage(marker))) {
					if (start <= startOffset && end >= endOffset) {
						return marker;
					}
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return null;
	}

}
