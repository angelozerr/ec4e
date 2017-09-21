package org.eclipse.ec4e.services;

import org.eclipse.ec4e.services.completion.CharProvider;
import org.eclipse.ec4e.services.completion.CharProvider.StringCharProvider;
import org.eclipse.ec4e.services.completion.CompletionContext;
import org.eclipse.ec4e.services.completion.CompletionContextType;
import org.eclipse.ec4e.services.completion.ICompletionEntry;
import org.eclipse.ec4e.services.model.options.ConfigPropertyException;
import org.eclipse.ec4e.services.model.options.ConfigPropertyType;
import org.eclipse.ec4e.services.parser.EditorConfigParser;
import org.eclipse.ec4e.services.validation.IReporter;
import org.eclipse.ec4e.services.validation.ISeverityProvider;
import org.eclipse.ec4e.services.validation.ValidationEditorConfigHandler;

/**
 * EditorConfig service helpful for IDE:
 * 
 * <ul>
 * <li>validation</li>
 * <li>completion</li>
 * </ul>
 *
 */
public class EditorConfigService {

	// ------------- Validation service

	public static void validate(String content, IReporter reporter) {
		validate(content, reporter, null);
	}

	/**
	 * Validate the given content of an .editorconfig and report errors in the given
	 * reporter. This validator is able to validate:
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

	// ------------- Completion service

	public static CompletionContext getCompletionContext(int offset, String document) throws Exception {
		return getCompletionContext(offset, document, StringCharProvider.INSTANCE);
	}

	public static <T> CompletionContext getCompletionContext(int offset, T document, CharProvider<T> provider)
			throws Exception {
		char c;
		CompletionContextType type = CompletionContextType.OPTION_NAME;
		StringBuilder prefix = new StringBuilder();
		StringBuilder name = null;
		int i = offset - 1;
		// Collect prefix
		while (i >= 0) {
			c = provider.getChar(document, i);
			if (Character.isJavaIdentifierPart(c)) {
				prefix.insert(0, c);
				i--;
			} else {
				break;
			}
		}
		// Collect context type
		boolean stop = false;
		while (i >= 0 && !stop) {
			c = provider.getChar(document, i--);
			switch (c) {
			case '[':
				type = CompletionContextType.SECTION;
				stop = true;
				break;
			case '#':
				type = CompletionContextType.COMMENTS;
				stop = true;
				break;
			case ' ':
			case '\t':
				continue;
			case '\r':
			case '\n':
				stop = true;
				break;
			case '=':
				name = new StringBuilder();
				type = CompletionContextType.OPTION_VALUE;
				break;
			default:
				if (name != null && Character.isJavaIdentifierPart(c)) {
					name.insert(0, c);
				}
			}
		}
		return new CompletionContext(prefix.toString(), name != null ? name.toString() : null, type);
	}

	public static ICompletionEntry getCompletionEntries() {
		return null;
	}
}
