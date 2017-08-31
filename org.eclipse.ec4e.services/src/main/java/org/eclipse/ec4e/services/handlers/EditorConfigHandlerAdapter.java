package org.eclipse.ec4e.services.handlers;

import org.eclipse.ec4e.services.parser.ParseException;

public class EditorConfigHandlerAdapter<S, O> extends AbstractEditorConfigHandler<S, O> {

	@Override
	public S startSection() {
		return null;
	}

	@Override
	public void endSection(S section) {

	}

	@Override
	public void startMultiPatternSection(S section) {

	}

	@Override
	public void endMultiPatternSection(S section) {

	}

	@Override
	public void startPattern(S section, int i) {

	}

	@Override
	public void endPattern(S section, String pattern, int i) {

	}

	@Override
	public O startOption() {
		return null;
	}

	@Override
	public void endOption(O option) {

	}

	@Override
	public void startOptionName(O option) {

	}

	@Override
	public void endOptionName(O option, String name) {

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
