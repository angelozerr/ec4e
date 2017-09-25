package org.eclipse.ec4e.internal.validation;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.MonoReconciler;

public class EditorConfigReconciler extends MonoReconciler {

	public EditorConfigReconciler(IPreferenceStore preferenceStore) {
		super(new EditorConfigReconcilingStrategy(preferenceStore), false);
	}

	@Override
	public void install(ITextViewer textViewer) {
		super.install(textViewer);
		((EditorConfigReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE)).install(textViewer);
	}

	@Override
	public void uninstall() {
		super.uninstall();
		((EditorConfigReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE)).uninstall();
	}

}
