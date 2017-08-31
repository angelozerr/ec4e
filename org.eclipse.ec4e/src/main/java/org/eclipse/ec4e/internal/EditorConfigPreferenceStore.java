package org.eclipse.ec4e.internal;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.AbstractTextEditor;
import org.eclipse.ui.texteditor.ChainedPreferenceStore;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorConfigPreferenceStore implements IPreferenceStore {

	private final ITextEditor textEditor;
	private ChainedPreferenceStore newStore;

	public EditorConfigPreferenceStore(ITextEditor textEditor) throws Exception {
		this.textEditor = textEditor;

		Field field = AbstractTextEditor.class.getDeclaredField("fPreferenceStore");
		field.setAccessible(true);
		IPreferenceStore oldStore = (IPreferenceStore) field.get(textEditor);
		newStore = new ChainedPreferenceStore(new IPreferenceStore[] { this, oldStore });

		// field.set(textEditor, newStore);

		Method m = AbstractTextEditor.class.getDeclaredMethod("setPreferenceStore",
				new Class[] { IPreferenceStore.class });
		m.setAccessible(true);
		m.invoke(textEditor, newStore);

		Field f = AbstractTextEditor.class.getDeclaredField("fConfiguration");
		f.setAccessible(true);
		SourceViewerConfiguration oldConfig = (SourceViewerConfiguration) f.get(textEditor);
		if (oldConfig instanceof TextSourceViewerConfiguration) {
			Field f2 = TextSourceViewerConfiguration.class.getDeclaredField("fPreferenceStore");
			f2.setAccessible(true);
			f2.set(oldConfig, newStore);
		}
	}

	public void applyConfig() {
		newStore.firePropertyChangeEvent(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH, 0, 50);
		newStore.firePropertyChangeEvent(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS, false, true);
	}

	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(String name) {
		if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS.equals(name)) {
			return true;
		}
		return false;
	}

	@Override
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean getBoolean(String name) {
		if (AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS.equals(name)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean getDefaultBoolean(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public double getDefaultDouble(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getDefaultFloat(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getDefaultInt(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getDefaultLong(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getDefaultString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getDouble(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getFloat(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getInt(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public long getLong(String name) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public String getString(String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDefault(String name) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean needsSaving() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void putValue(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePropertyChangeListener(IPropertyChangeListener listener) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefault(String name, double value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefault(String name, float value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefault(String name, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefault(String name, long value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefault(String name, String defaultObject) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefault(String name, boolean value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setToDefault(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(String name, double value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(String name, float value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(String name, int value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(String name, long value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(String name, String value) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setValue(String name, boolean value) {
		// TODO Auto-generated method stub

	}

}
