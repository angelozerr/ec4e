package org.eclipse.ec4e.internal;

import java.io.File;
import java.util.Collection;

import org.eclipse.ec4e.services.EditorConfigException;
import org.eclipse.ec4e.services.model.Option;
import org.eclipse.ec4e.services.model.options.EndOfLineOption;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.editors.text.IEncodingSupport;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorConfigPreferenceStore implements IPreferenceStore {

	public static final String EDITOR_SPACES_FOR_TABS = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS;
	public static final String EDITOR_TAB_WIDTH = AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH;
	public static final String EDITOR_TRIM_TRAILING_WHITESPACE = "trim_trailing_whitespace";
	public static final String EDITOR_INSERT_FINAL_NEWLINE = "insert_final_newline";

	private final ITextEditor textEditor;
	private IPreferenceStore editorStore;

	private Boolean spacesForTabs;
	private Integer tabWidth;
	private String endOfLine;
	private boolean trimTrailingWhitespace;
	private boolean insertFinalNewline;

	public EditorConfigPreferenceStore(ITextEditor textEditor) throws Exception {
		this.textEditor = textEditor;
		this.editorStore = PreferenceStoreHelper.contributeToPreferenceStore(textEditor, this);
	}

	public void applyConfig() {
		File file = getFile(textEditor);
		if (file != null) {
			try {
				Boolean oldSpacesForTabs = spacesForTabs;
				spacesForTabs = null;
				Integer oldTabWidth = tabWidth;
				tabWidth = null;
				Collection<Option> options = IDEEditorConfigManager.getInstance().getOptions(file, null);
				for (Option option : options) {
					if ("indent_style".equals(option.getName())) {
						spacesForTabs = "space".equals(option.getValue());
						if (oldSpacesForTabs != spacesForTabs) {
							editorStore.firePropertyChangeEvent(EDITOR_SPACES_FOR_TABS, oldSpacesForTabs,
									spacesForTabs);
						}
					} else if ("indent_size".equals(option.getName())) {
						tabWidth = Integer.parseInt(option.getValue());
						if (oldTabWidth != tabWidth) {
							editorStore.firePropertyChangeEvent(EDITOR_TAB_WIDTH, oldTabWidth, tabWidth);
						}
					} else if ("end_of_line".equals(option.getName())) {
						IEditorInput editorInput = textEditor.getEditorInput();
						IDocument document = textEditor.getDocumentProvider().getDocument(editorInput);
						if (document instanceof IDocumentExtension4) {
							EndOfLineOption endOfLineOption = EndOfLineOption.valueOf(option.getValue().toUpperCase());
							if (endOfLineOption != null) {
								((IDocumentExtension4) document)
										.setInitialLineDelimiter(endOfLineOption.getEndOfLineString());
							}
						}
					} else if ("charset".equals(option.getName())) {
						IEncodingSupport encodingSupport = textEditor.getAdapter(IEncodingSupport.class);
						if (encodingSupport != null) {
							encodingSupport.setEncoding(option.getValue().toUpperCase());
						}
					} else if ("trim_trailing_whitespace".equals(option.getName())) {
						boolean oldTrimTrailingWhitespace = trimTrailingWhitespace;
						trimTrailingWhitespace = "true".equals(option.getValue());
						if (oldTrimTrailingWhitespace != trimTrailingWhitespace) {
							editorStore.firePropertyChangeEvent(EDITOR_TRIM_TRAILING_WHITESPACE,
									oldTrimTrailingWhitespace, trimTrailingWhitespace);
						}
					} else if ("insert_final_newline".equals(option.getName())) {
						boolean oldInsertFinalNewline = insertFinalNewline;
						insertFinalNewline = "true".equals(option.getValue());
						if (oldInsertFinalNewline != insertFinalNewline) {
							editorStore.firePropertyChangeEvent(EDITOR_INSERT_FINAL_NEWLINE, oldInsertFinalNewline,
									insertFinalNewline);
						}
					}
				}
			} catch (EditorConfigException e) {
				e.printStackTrace();
			} finally {

			}

		}
	}

	public IPreferenceStore getEditorStore() {
		return editorStore;
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
			return spacesForTabs != null;
		} else if (EDITOR_TAB_WIDTH.equals(name)) {
			return tabWidth != null;
		} else if (EDITOR_TRIM_TRAILING_WHITESPACE.equals(name)) {
			return true;
		} else if (EDITOR_INSERT_FINAL_NEWLINE.equals(name)) {
			return true;
		}
		return false;
	}

	@Override
	public boolean getBoolean(String name) {
		if (EDITOR_SPACES_FOR_TABS.equals(name)) {
			return spacesForTabs;
		} else if (EDITOR_TRIM_TRAILING_WHITESPACE.equals(name)) {
			return trimTrailingWhitespace;
		} else if (EDITOR_INSERT_FINAL_NEWLINE.equals(name)) {
			return insertFinalNewline;
		}
		return false;
	}

	@Override
	public int getInt(String name) {
		if (EDITOR_TAB_WIDTH.equals(name)) {
			return tabWidth;
		}
		return 0;
	}

	@Override
	public void firePropertyChangeEvent(String name, Object oldValue, Object newValue) {

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
