package org.eclipse.ec4e.codelens;

import org.eclipse.ec4j.model.EditorConfig;
import org.eclipse.ec4j.model.Section;
import org.eclipse.ec4j.parser.Location;

public class SectionWithLoc extends Section {

	private final Location start;

	public SectionWithLoc(EditorConfig editorConfig, Location start) {
		super(editorConfig);
		this.start = start;
	}

	public Location getStart() {
		return start;
	}
}
