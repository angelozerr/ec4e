package org.eclipse.ec4e.services.validator;

import org.eclipse.ec4e.services.parser.ErrorType;

public interface ISeverityProvider {

	public static final ISeverityProvider DEFAULT = new ISeverityProvider() {

		@Override
		public Severity getSeverity(ErrorType errorType) {
			return errorType.isSyntaxError() ? Severity.error : Severity.warning;
		}
	};

	Severity getSeverity(ErrorType errorType);
}
