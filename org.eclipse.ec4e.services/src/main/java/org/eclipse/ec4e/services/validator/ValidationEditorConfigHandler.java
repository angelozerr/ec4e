package org.eclipse.ec4e.services.validator;

import java.text.MessageFormat;

import org.eclipse.ec4e.services.EditorConfigService;
import org.eclipse.ec4e.services.handlers.EditorConfigHandlerAdapter;
import org.eclipse.ec4e.services.model.ConfigPropertyException;
import org.eclipse.ec4e.services.parser.ErrorType;
import org.eclipse.ec4e.services.parser.Location;
import org.eclipse.ec4e.services.parser.ParseException;

public class ValidationEditorConfigHandler extends EditorConfigHandlerAdapter<Object, Object> {

	private static final String OPTION_NAME_NOT_EXISTS_MESSAGE = "The option ''{0}'' is not supported by .editorconfig";
	private static final String OPTION_VALUE_TYPE_MESSAGE = "The option ''{0}'' doesn't support the value ''{1}''";

	private final IReporter reporter;
	private final ISeverityProvider provider;

	public ValidationEditorConfigHandler(IReporter reporter, ISeverityProvider provider) {
		this.reporter = reporter;
		this.provider = provider != null ? provider : ISeverityProvider.DEFAULT;
	}

	@Override
	public void endPattern(Object section, String pattern, int i) {
		// TODO: validate pattern
	}

	@Override
	public void endOptionName(Object option, String name) {
		// Validate option name
		if (!EditorConfigService.isOptionExists(name)) {
			Location location = getLocation();
			ErrorType errorType = ErrorType.OptionNameNotExists;
			reporter.addError(MessageFormat.format(OPTION_NAME_NOT_EXISTS_MESSAGE, name), location, null, errorType,
					provider.getSeverity(errorType));
		}
	}

	@Override
	public void endOptionValue(Object option, String value, String name) {
		// Validate value of the option name
		try {
			EditorConfigService.validateOptionValue(name, value);
		} catch (ConfigPropertyException e) {
			Location location = getLocation();
			ErrorType errorType = ErrorType.OptionValueType;
			reporter.addError(e.getMessage(), location, null, errorType, provider.getSeverity(errorType));
		}
	}

	@Override
	public void error(ParseException e) {
		reporter.addError(e.getMessage(), e.getLocation(), null, e.getErrorType(), getSeverity(e));
	}

	protected Severity getSeverity(ParseException e) {
		return provider.getSeverity(e.getErrorType());
	}

}
