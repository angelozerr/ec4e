package org.eclipse.ec4e.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.ec4e.internal.validation.EditorConfigReconciler;
import org.eclipse.jface.text.ITextOperationTarget;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ITextEditor;

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
		this.reconciler = new EditorConfigReconciler(store.getEditorStore(), getFile(textEditor));
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

	private static IFile getFile(ITextEditor textEditor) {
		IEditorInput input = textEditor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			return ((IFileEditorInput) input).getFile();
		}
		return null;
	}

}
