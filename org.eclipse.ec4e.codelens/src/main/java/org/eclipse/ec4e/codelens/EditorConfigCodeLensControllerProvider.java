package org.eclipse.ec4e.codelens;

import org.eclipse.codelens.editors.DefaultCodeLensController;
import org.eclipse.codelens.editors.ICodeLensController;
import org.eclipse.codelens.editors.ICodeLensControllerFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorConfigCodeLensControllerProvider implements ICodeLensControllerFactory {

	@Override
	public boolean isRelevant(ITextEditor textEditor) {
		IFile configFile = getFile(textEditor);
		return (configFile != null && ".editorconfig".equals(configFile.getName()));
	}

	@Override
	public ICodeLensController create(ITextEditor textEditor) {
		DefaultCodeLensController controller = new DefaultCodeLensController(textEditor);
		controller.addTarget("ec4e.codelens");
		return controller;
	}

	public static IFile getFile(ITextEditor textEditor) {
		IEditorInput input = textEditor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			return ((IFileEditorInput) input).getFile();
		}
		return null;
	}

}
