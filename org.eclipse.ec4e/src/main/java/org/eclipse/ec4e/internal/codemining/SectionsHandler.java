package org.eclipse.ec4e.internal.codemining;

import java.util.ArrayList;
import java.util.List;

import org.ec4j.core.PropertyTypeRegistry;
import org.ec4j.core.model.Version;
import org.ec4j.core.parser.EditorConfigModelHandler;
import org.ec4j.core.parser.ErrorHandler;
import org.ec4j.core.parser.Location;
import org.ec4j.core.parser.ParseContext;

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
