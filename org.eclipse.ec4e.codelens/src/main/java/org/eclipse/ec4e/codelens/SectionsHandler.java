package org.eclipse.ec4e.codelens;

import org.eclipse.ec4e.IDEEditorConfigManager;
import org.eclipse.ec4j.model.EditorConfigHandler;
import org.eclipse.ec4j.model.Option;
import org.eclipse.ec4j.model.Section;

public class SectionsHandler extends EditorConfigHandler {

	public SectionsHandler(String dirPath) {
		super(IDEEditorConfigManager.getInstance().getRegistry(), IDEEditorConfigManager.getInstance().getVersion());
		getEditorConfig().setDirPath(dirPath);
	}

	@Override
	public Section startSection() {
		return new SectionWithLoc(getEditorConfig(), getLocation());
	}

	@Override
	public Option endOptionName(String name) {
		// No need to parse options.
		return null;
	}

}
