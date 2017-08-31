package org.eclipse.ec4e.services.parser;

public class SectionNotClosedException extends ParseException {

	public SectionNotClosedException(Location location) {
		super("Section not closed. Expected ']'", location, ErrorType.SectionNotClosed);
	}
}
