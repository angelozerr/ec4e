package org.eclipse.ec4e.internal.validation;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ec4e.services.EditorConfigService;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;

public class EditorConfigReconcilingStrategy
		implements IReconcilingStrategy, IReconcilingStrategyExtension, IReconciler {

	private ITextViewer textViewer;

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
		IDocument document = textViewer.getDocument();
		EditorConfigService.validate(document.get(), (message, start, end, type, severity) -> {
			
		});
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

}
