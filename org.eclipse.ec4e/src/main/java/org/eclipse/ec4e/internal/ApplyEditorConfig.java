package org.eclipse.ec4e.internal;

import org.eclipse.ec4e.internal.validation.EditorConfigReconciler;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public class ApplyEditorConfig {

	private final AbstractTextEditor textEditor;
	private final EditorConfigPreferenceStore store;
	private final EditorConfigReconciler reconciler;

	/**
	 * @param store
	 * @param reconciler
	 * @throws Exception
	 */
	public ApplyEditorConfig(AbstractTextEditor textEditor) throws Exception {
		this.textEditor = textEditor;
		this.store = new EditorConfigPreferenceStore(textEditor);
		this.reconciler = new EditorConfigReconciler(store.getEditorStore());
	}

	public void applyConfig() {
		store.applyConfig();
	}

	public void install() {
		reconciler.install((ITextViewer) textEditor.getAdapter(ITextOperationTarget.class));
	}

	public void uninstall() {
		reconciler.uninstall();
	}

}
