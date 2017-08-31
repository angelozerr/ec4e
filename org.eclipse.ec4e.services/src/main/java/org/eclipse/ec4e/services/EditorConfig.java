package org.eclipse.ec4e.services;

import org.eclipse.ec4e.services.model.ConfigPropertyException;
import org.eclipse.ec4e.services.model.ConfigPropertyType;
import org.eclipse.ec4e.services.parser.EditorConfigParser;
import org.eclipse.ec4e.services.validator.IReporter;
import org.eclipse.ec4e.services.validator.ISeverityProvider;
import org.eclipse.ec4e.services.validator.ValidationEditorConfigHandler;

/**
 * EditorConfig utilities useful for IDE.
 *
 */
public class EditorConfig {

	public static void validate(String content, IReporter reporter) {
		validate(content, reporter, null);
	}

	/**
	 * Validate the given content of an .editorconfig and report errors in the
	 * given reporter. This validator is able to validate:
	 * 
	 * <ul>
	 * <li>Syntax error like section which are not closed.</li>
	 * <li>Semantic error like :
	 * <ul>
	 * <li>check option name is an EditorConfig properties
	 * {@link https://github.com/editorconfig/editorconfig/wiki/EditorConfig-Properties}
	 * </li>
	 * <li>check option value according the option name.</li>
	 * </ul>
	 * </li>
	 * </ul>
	 * 
	 * @param content
	 *            of the .editorconfig to validate
	 * @param reporter
	 *            used to report errors.
	 */
	public static void validate(String content, IReporter reporter, ISeverityProvider provider) {
		ValidationEditorConfigHandler handler = new ValidationEditorConfigHandler(reporter, provider);
		EditorConfigParser parser = new EditorConfigParser(handler);
		// Set parser as tolerant to collect the full errors of each line of the
		// editorconfig.
		parser.setTolerant(true);
		parser.parse(content);
	}

	public static boolean validateOptionValue(String name, String value) throws ConfigPropertyException {
		ConfigPropertyType<?> type = getOption(name);
		if (type != null) {
			type.validate(value);
		}
		return true;
	}

	public static boolean isOptionExists(String name) {
		return getOption(name) != null;
	}

	public static ConfigPropertyType<?> getOption(String name) {
		return ConfigPropertyType.valueOf(name.toUpperCase());
	}

}
