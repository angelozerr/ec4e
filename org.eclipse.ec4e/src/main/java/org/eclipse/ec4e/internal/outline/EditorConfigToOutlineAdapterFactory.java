package org.eclipse.ec4e.internal.outline;

import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.ec4e.utils.EditorUtils;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.IContentOutlinePage;

public class EditorConfigToOutlineAdapterFactory implements IAdapterFactory {

	@Override
	public <T> T getAdapter(Object adaptableObject, Class<T> adapterType) {
		if (adapterType == IContentOutlinePage.class && adaptableObject instanceof ITextEditor
				&& EditorUtils.isEditorConfigFile((ITextEditor) adaptableObject)) {
			return (T) new EditorConfigOutlinePage((ITextEditor) adaptableObject);
		}
		return null;
	}

	@Override
	public Class<?>[] getAdapterList() {
		return new Class<?>[] { IContentOutlinePage.class };
	}

}
