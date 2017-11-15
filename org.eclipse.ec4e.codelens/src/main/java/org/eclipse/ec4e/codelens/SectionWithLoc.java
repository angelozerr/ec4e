package org.eclipse.ec4e.codelens;

import org.ec4j.core.model.Section;
import org.ec4j.core.parser.Location;

public class SectionWithLoc {

	public SectionWithLoc(Location start, Section section) {
		super();
		this.start = start;
		this.section = section;
	}


	private final Location start;
	private final Section section;


	public Location getStart() {
		return start;
	}


	public Section getSection() {
		return section;
	}
}
