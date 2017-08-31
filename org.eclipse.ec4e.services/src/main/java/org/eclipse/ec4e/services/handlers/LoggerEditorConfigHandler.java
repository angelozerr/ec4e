package org.eclipse.ec4e.services.handlers;

import org.eclipse.ec4e.services.parser.ParseException;

public class LoggerEditorConfigHandler<S, O> extends AbstractEditorConfigHandler<S, O> {

	@Override
	public S startSection() {
		System.err.println("Start section at " + getLocation());
		return null;
	}

	@Override
	public void endSection(S section) {
		System.err.println("End section at " + getLocation());
	}

	@Override
	public void startMultiPatternSection(S section) {
		System.err.println("Start multi pattern at " + getLocation());
	}

	@Override
	public void endMultiPatternSection(S section) {
		System.err.println("End multi pattern at " + getLocation());
	}

	@Override
	public void startPattern(S section, int i) {
		System.err.println("Start pattern [" + i + "] at " + getLocation());
	}

	@Override
	public void endPattern(S section, String pattern, int i) {
		System.err.println("End pattern [" + i + "] at " + getLocation() + ", pattern=" + pattern);
	}

	@Override
	public O startOption() {
		System.err.println("Start option at " + getLocation());
		return null;
	}

	@Override
	public void endOption(O option) {
		System.err.println("End option at " + getLocation());
	}

	@Override
	public void startOptionName(O option) {
		System.err.println("Start option name at " + getLocation());
	}

	@Override
	public void endOptionName(O option, String name) {
		System.err.println("End option name at " + getLocation() + ", name=" + name);
	}

	@Override
	public void startOptionValue(Object option, String name) {
		System.err.println("Start option value of '" + name + "' at " + getLocation());
	}

	@Override
	public void endOptionValue(Object option, String value, String name) {
		System.err.println("End option value of '" + name + "', value=" + value + " at " + getLocation());
	}

	@Override
	public void error(ParseException e) {
		e.printStackTrace();
	}
}
