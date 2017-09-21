package org.eclipse.ec4e.services.parser.handlers;

import org.eclipse.ec4e.services.parser.EditorConfigParser;
import org.eclipse.ec4e.services.parser.Location;

public abstract class AbstractEditorConfigHandler<Section, Option> implements IEditorConfigHandler<Section, Option> {

	private EditorConfigParser<Section, Option> parser;

	@Override
	public void setParser(EditorConfigParser<Section, Option> parser) {
		this.parser = parser;
	}

	/**
	 * Returns the current parser location.
	 *
	 * @return the current parser location
	 */
	protected Location getLocation() {
		return parser.getLocation();
	}

}
