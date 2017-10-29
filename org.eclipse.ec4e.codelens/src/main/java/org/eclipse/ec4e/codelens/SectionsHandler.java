package org.eclipse.ec4e.codelens;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.ec4j.core.model.Version;
import org.eclipse.ec4j.core.model.propertytype.PropertyTypeRegistry;
import org.eclipse.ec4j.core.parser.EditorConfigModelHandler;
import org.eclipse.ec4j.core.parser.Location;
import org.eclipse.ec4j.core.parser.ParseContext;

public class SectionsHandler extends EditorConfigModelHandler {

	public SectionsHandler(PropertyTypeRegistry registry, Version version) {
		super(registry, version);
	}

	private final List<Location> sectionLocations = new ArrayList<>();

	public List<Location> getSectionLocations() {
		return sectionLocations;
	}

	@Override
	public void startSection(ParseContext context) {
		super.startSection(context);
		sectionLocations.add(context.getLocation());
	}

}
