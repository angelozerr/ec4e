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
package org.eclipse.ec4e.internal;

import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.ec4e.IDEEditorConfigManager;
import org.eclipse.ec4j.core.EditorConfigException;
import org.eclipse.ec4j.core.model.Option;
import org.eclipse.ec4j.core.model.optiontypes.EndOfLineOption;
import org.eclipse.ec4j.core.model.optiontypes.IndentStyleOption;
import org.eclipse.ec4j.core.model.optiontypes.OptionNames;
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
		IFile file = ApplyEditorConfig.getFile(textEditor);
		if (file != null) {
			try {
				Boolean oldSpacesForTabs = spacesForTabs;
				spacesForTabs = null;
				Integer oldTabWidth = tabWidth;
				tabWidth = null;
				Collection<Option> options = IDEEditorConfigManager.getInstance().queryOptions(file);
				for (Option option : options) {
					if (!option.isValid()) {
						continue;
					}
					OptionNames names = OptionNames.get(option.getName());
					switch (names) {
					case indent_style:
						IndentStyleOption styleOption = option.getValueAs();
						spacesForTabs = styleOption == IndentStyleOption.SPACE;
						if (oldSpacesForTabs != spacesForTabs) {
							editorStore.firePropertyChangeEvent(EDITOR_SPACES_FOR_TABS, oldSpacesForTabs,
									spacesForTabs);
						}
						break;
					case indent_size:
						tabWidth = option.getValueAs();
						if (oldTabWidth != tabWidth) {
							editorStore.firePropertyChangeEvent(EDITOR_TAB_WIDTH, oldTabWidth, tabWidth);
						}
						break;
					case end_of_line:
						IEditorInput editorInput = textEditor.getEditorInput();
						IDocument document = textEditor.getDocumentProvider().getDocument(editorInput);
						if (document instanceof IDocumentExtension4) {
							EndOfLineOption endOfLineOption = option.getValueAs();
							if (endOfLineOption != null) {
								((IDocumentExtension4) document)
										.setInitialLineDelimiter(endOfLineOption.getEndOfLineString());
							}
						}
						break;
					case charset:
						IEncodingSupport encodingSupport = textEditor.getAdapter(IEncodingSupport.class);
						if (encodingSupport != null) {
							encodingSupport.setEncoding(option.getValue().toUpperCase());
						}
						break;
					case trim_trailing_whitespace:
						boolean oldTrimTrailingWhitespace = trimTrailingWhitespace;
						trimTrailingWhitespace = option.getValueAs();
						if (oldTrimTrailingWhitespace != trimTrailingWhitespace) {
							editorStore.firePropertyChangeEvent(EDITOR_TRIM_TRAILING_WHITESPACE,
									oldTrimTrailingWhitespace, trimTrailingWhitespace);
						}
						break;
					case insert_final_newline:
						boolean oldInsertFinalNewline = insertFinalNewline;
						insertFinalNewline = option.getValueAs();
						if (oldInsertFinalNewline != insertFinalNewline) {
							editorStore.firePropertyChangeEvent(EDITOR_INSERT_FINAL_NEWLINE, oldInsertFinalNewline,
									insertFinalNewline);
						}
						break;
					default:
						// Do nothing
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
