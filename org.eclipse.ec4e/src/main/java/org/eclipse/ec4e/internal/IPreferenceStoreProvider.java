package org.eclipse.ec4e.internal;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

public interface IPreferenceStoreProvider {

	IPreferenceStore getPreferenceStore(ITextEditor textEditor);
}
