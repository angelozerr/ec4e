package org.eclipse.ec4e.internal;

import java.io.File;
import java.util.Collection;

import org.eclipse.ec4e.services.EditorConfigException;
import org.eclipse.ec4e.services.model.Option;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorConfigPreferenceStore implements IPreferenceStore {

	private static final String EDITOR_SPACES_FOR_TABS = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;
	private static final String EDITOR_TAB_WIDTH = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;

	private final ITextEditor textEditor;
	private IPreferenceStore editorStore;

	private boolean spacesForTabs;
	private int tabWidth;

	public EditorConfigPreferenceStore(ITextEditor textEditor) throws Exception {
		this.textEditor = textEditor;
		this.editorStore = PreferenceStoreHelper.contributeToPreferenceStore(textEditor, this);
	}

	public void setEditorStore(IPreferenceStore editorStore) {
		this.editorStore = editorStore;
	}

	public void applyConfig() {
		File file = getFile(textEditor);
		if (file != null) {
			try {
				Collection<Option> options = IDEEditorConfigManager.getInstance().getOptions(file, null);
				for (Option option : options) {

					if ("indent_style".equals(option.getName())) {
						boolean oldSpacesForTabs = spacesForTabs;
						spacesForTabs = "space".equals(option.getValue());
						if (oldSpacesForTabs != spacesForTabs) {
							editorStore.firePropertyChangeEvent(EDITOR_SPACES_FOR_TABS, oldSpacesForTabs,
									spacesForTabs);
						}
					} else if ("indent_size".equals(option.getName())) {
						int oldTabWidth = tabWidth;
						tabWidth = Integer.parseInt(option.getValue());
						if (oldTabWidth != tabWidth) {
							editorStore.firePropertyChangeEvent(EDITOR_TAB_WIDTH, oldTabWidth, tabWidth);
						}
					}
				}

				// newStore.firePropertyChangeEvent(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH,
				// 0,
				// 50);
				// newStore.firePropertyChangeEvent(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS,
				// false, true);
			} catch (EditorConfigException e) {
				e.printStackTrace();
			}

		}
	}

	private static File getFile(ITextEditor textEditor) {
		IEditorInput input = textEditor.getEditorInput();
		if (input instanceof IFileEditorInput) {
			return ((IFileEditorInput) input).getFile().getLocation().toFile();
		}
		return null;
	}

	@Override
	public void addPropertyChangeListener(IPropertyChangeListener listener) {

	}

	@Override
	public boolean contains(String name) {
		if (EDITOR_SPACES_FOR_TABS.equals(name)) {
			return true;
		} else if (EDITOR_TAB_WIDTH.equals(name)) {
			return true;
		}
		return false;
	}

	@Override
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {

	}

	@Override
	public boolean getBoolean(String name) {
		if (EDITOR_SPACES_FOR_TABS.equals(name)) {
			return spacesForTabs;
		}
		return false;
	}

	@Override
	public boolean getDefaultBoolean(String name) {
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
		if (EDITOR_TAB_WIDTH.equals(name)) {
			return tabWidth;
		}
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
