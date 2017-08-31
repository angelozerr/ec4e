package org.eclipse.ec4e.services.parser;

public class OptionAssignementMissingException extends ParseException {

	public OptionAssignementMissingException(String name, Location location) {
		super("Assignement misses for the option '" + name + "'. Expected '='", location,
				ErrorType.OptionAssignementMissing);
	}
}
