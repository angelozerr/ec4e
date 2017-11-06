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
package org.eclipse.ec4e.internal;

import org.eclipse.core.resources.IFile;
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

	public static IFile getFile(ITextEditor textEditor) {
		IEditorInput input = textEditor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			return ((IFileEditorInput) input).getFile();
		}
		return null;
	}

}
