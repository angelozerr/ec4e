package org.eclipse.ec4e.services.parser;

/**
 * An unchecked exception to indicate that an input does not qualify as valid
 * .editorconfig.
 */
@SuppressWarnings("serial") // use default serial UID
public class ParseException extends RuntimeException {

	private final Location location;
	private final ErrorType errorType;

	ParseException(String message, Location location) {
		this(message, location, ErrorType.ParsingError);
	}

	ParseException(String message, Location location, ErrorType errorType) {
		super(message + " at " + location);
		this.location = location;
		this.errorType = errorType;
	}

	/**
	 * Returns the location at which the error occurred.
	 *
	 * @return the error location
	 */
	public Location getLocation() {
		return location;
	}

	public ErrorType getErrorType() {
		return errorType;
	}
}