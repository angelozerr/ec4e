package org.eclipse.ec4e.codelens;

import org.eclipse.codelens.editors.DefaultCodeLensController;
import org.eclipse.codelens.editors.ICodeLensController;
import org.eclipse.codelens.editors.ICodeLensControllerFactory;
import org.eclipse.ec4e.utils.EditorUtils;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorConfigCodeLensControllerProvider implements ICodeLensControllerFactory {

	@Override
	public boolean isRelevant(ITextEditor textEditor) {
		return EditorUtils.isEditorConfigFile(textEditor);
	}

	@Override
	public ICodeLensController create(ITextEditor textEditor) {
		DefaultCodeLensController controller = new DefaultCodeLensController(textEditor);
		controller.addTarget("ec4e.codelens");
		return controller;
	}

}
