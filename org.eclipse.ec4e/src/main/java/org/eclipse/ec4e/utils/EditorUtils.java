package org.eclipse.ec4e.utils;

import org.eclipse.core.resources.IFile;
import org.eclipse.ec4j.core.EditorConfigConstants;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorUtils {

	public static boolean isEditorConfigFile(ITextEditor textEditor) {
		IFile configFile = EditorUtils.getFile(textEditor);
		return isEditorConfigFile(configFile);
	}

	public static boolean isEditorConfigFile(IFile configFile) {
		return configFile != null && EditorConfigConstants.EDITORCONFIG.equals(configFile.getName());
	}

	public static IFile getFile(ITextEditor textEditor) {
		IEditorInput input = textEditor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			return ((IFileEditorInput) input).getFile();
		}
		return null;
	}
}
