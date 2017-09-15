package org.eclipse.ec4e.internal;

import org.eclipse.ec4e.services.EditorConfigManager;

public class IDEEditorConfigManager extends EditorConfigManager {

	public static final IDEEditorConfigManager INSTANCE = new IDEEditorConfigManager();
	
	public static IDEEditorConfigManager getInstance() {
		return INSTANCE;
	}
	
}
