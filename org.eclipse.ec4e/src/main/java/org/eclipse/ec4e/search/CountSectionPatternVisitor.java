package org.eclipse.ec4e.search;

import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.ec4j.model.Section;

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
