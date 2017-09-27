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
package org.eclipse.ec4e.internal.validation.marker;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.AbstractTextEditor;

public abstract class AbstractMarkerResolution implements IMarkerResolution {

	@Override
	public final void run(IMarker marker) {
		// Se if there is an open editor on the file containing the marker
		IWorkbenchWindow w = PlatformUI.getWorkbench().getWorkbenchWindows()[0];
		// .getActiveWorkbenchWindow();
		if (w == null)
			return;
		IWorkbenchPage page = w.getActivePage();
		if (page == null)
			return;
		IFileEditorInput input = new FileEditorInput((IFile) marker.getResource());
		IEditorPart editorPart = page.findEditor(input);

		if (editorPart == null) {
			// open an editor
			try {
				editorPart = IDE.openEditor(page, (IFile) marker.getResource(), true);
			} catch (PartInitException e) {
				// MessageDialog.openError(w.getShell(), MessageUtil
				// .getString("Resolution_Error"), //$NON-NLS-1$
				// MessageUtil.getString("Unable_to_open_file_editor")); //$NON-NLS-1$
			}
		}
		if (editorPart == null || !(editorPart instanceof AbstractTextEditor))
			return;
		// insert the sentence
		AbstractTextEditor editor = (AbstractTextEditor) editorPart;
		IDocument doc = editor.getDocumentProvider().getDocument(editor.getEditorInput());
		if (!run(marker, editor, doc)) {
			return;
		}
		// delete the marker
		try {
			marker.delete();
		} catch (CoreException e) {
			e.printStackTrace();
			// ignore
		}
	}

	protected abstract boolean run(IMarker marker, AbstractTextEditor editor, IDocument doc);
}