package org.eclipse.ec4e.services.handlers;

import org.eclipse.ec4e.services.parser.ParseException;

public class EditorConfigHandlerAdapter<Section, Option> extends AbstractEditorConfigHandler<Section, Option> {

	@Override
	public Section startSection() {
		return null;
	}

	@Override
	public void endSection(Section section) {

	}

	@Override
	public void startMultiPatternSection(Section section) {

	}

	@Override
	public void endMultiPatternSection(Section section) {

	}

	@Override
	public void startPattern(Section section, int i) {

	}

	@Override
	public void endPattern(Section section, String pattern, int i) {

	}

	@Override
	public Option startOption() {
		return null;
	}

	@Override
	public void endOption(Option option, Section section) {

	}

	@Override
	public void startOptionName(Option option) {

	}

	@Override
	public void endOptionName(Option option, String name) {

	}

	@Override
	public void startOptionValue(Object option, String name) {

	}

	@Override
	public void endOptionValue(Object option, String value, String name) {

	}

	@Override
	public void error(ParseException e) {

	}
}
