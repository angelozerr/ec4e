package org.eclipse.ec4e.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

public class PreferenceStoreHelper {

	public static IPreferenceStore contributeToPreferenceStore(ITextEditor textEditor,
			IPreferenceStore preferenceStore) {
		try {
			// Get old store
			IPreferenceStore oldStore = getPreferenceStore(textEditor);
			// Create chained store with preference store to add
			IPreferenceStore newStore = new ChainedPreferenceStore(
					new IPreferenceStore[] { preferenceStore, oldStore });
			// Update the text editor with new preference store
			setPreferenceStoreOfTextEditor(textEditor, newStore);
			// Update the source viewer configuration with new preference store
			setPreferenceStoreOfSourceViewerConfiguration(textEditor, newStore);
			return newStore;

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	public static IPreferenceStore getPreferenceStore(ITextEditor textEditor) throws Exception {
		Field field = AbstractTextEditor.class.getDeclaredField("fPreferenceStore");
		field.setAccessible(true);
		return (IPreferenceStore) field.get(textEditor);
	}

	private static void setPreferenceStoreOfTextEditor(ITextEditor textEditor, IPreferenceStore preferenceStore)
			throws Exception {
		Method m = AbstractTextEditor.class.getDeclaredMethod("setPreferenceStore",
				new Class[] { IPreferenceStore.class });
		m.setAccessible(true);
		m.invoke(textEditor, preferenceStore);
	}

	private static void setPreferenceStoreOfSourceViewerConfiguration(ITextEditor textEditor,
			IPreferenceStore preferenceStore) throws Exception {
		Field f = AbstractTextEditor.class.getDeclaredField("fConfiguration");
		f.setAccessible(true);
		SourceViewerConfiguration oldConfig = (SourceViewerConfiguration) f.get(textEditor);
		if (oldConfig instanceof TextSourceViewerConfiguration) {
			Field f2 = TextSourceViewerConfiguration.class.getDeclaredField("fPreferenceStore");
			f2.setAccessible(true);
			f2.set(oldConfig, preferenceStore);
		}
	}

}
