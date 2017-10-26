package org.eclipse.ec4e.codelens;

import org.eclipse.ec4j.core.model.EditorConfigHandler;
import org.eclipse.ec4j.core.model.Option;
import org.eclipse.ec4j.core.model.Section;
import org.eclipse.ec4j.core.model.optiontypes.OptionTypeRegistry;

public class SectionsHandler extends EditorConfigHandler {

	public SectionsHandler(String dirPath, OptionTypeRegistry registry, String version) {
		super(registry, version);
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
