package org.eclipse.ec4e.services.parser;

public enum ErrorType {

	ParsingError(true), SectionNotClosed(true), MultiPatternNotClosed(true), OptionAssignementMissing(
			true), OptionValueMissing(true), OptionNameNotExists(false), OptionValueType(false);

	private final boolean syntaxError;

	private ErrorType(boolean syntaxError) {
		this.syntaxError = syntaxError;
	}

	public boolean isSyntaxError() {
		return syntaxError;
	}
}
