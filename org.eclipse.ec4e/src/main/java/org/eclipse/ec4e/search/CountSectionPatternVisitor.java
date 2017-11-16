package org.eclipse.ec4e.search;

import org.ec4j.core.model.Section;
import org.eclipse.core.resources.IResourceProxy;

public class CountSectionPatternVisitor extends AbstractSectionPatternVisitor {

	private int nbFiles;

	public CountSectionPatternVisitor(Section section) {
		super(section);
	}

	@Override
	protected void collect(IResourceProxy proxy) {
		nbFiles++;
	}

	public int getNbFiles() {
		return nbFiles;
	}

}
