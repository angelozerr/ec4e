/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.ec4e.services;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.ec4e.services.completion.CompletionContextType;
import org.eclipse.ec4e.services.completion.CompletionEntry;
import org.eclipse.ec4e.services.completion.ICompletionEntry;
import org.eclipse.ec4e.services.completion.ICompletionEntryMatcher;
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

	public static List<ICompletionEntry> getCompletionEntries(int offset, String document,
			ICompletionEntryMatcher matcher) throws Exception {
		return getCompletionEntries(offset, document, matcher, CompletionEntry::new,
				ContentProvider.STRING_CONTENT_PROVIDER);
	}

	public static <T, C extends ICompletionEntry> List<C> getCompletionEntries(int offset, T document,
			ICompletionEntryMatcher matcher, final Function<String, C> factory, ContentProvider<T> provider)
			throws Exception {
		TokenContext context = getTokenContext(offset, document, false, provider);
		switch (context.type) {
		case OPTION_NAME:
			return Stream.of(ConfigPropertyType.ALL_TYPES).map(type -> {
				C entry = factory.apply(type.getName());
				entry.setMatcher(matcher);
				entry.setOptionType(type);
				entry.setContextType(context.type);
				entry.setInitialOffset(offset);
				return entry;
			}).filter(entry -> entry.updatePrefix(context.prefix)).collect(Collectors.toList());
		case OPTION_VALUE:
			ConfigPropertyType<?> optionType = getOption(context.name);
			if (optionType != null) {
				String values[] = optionType.getPossibleValues();
				if (values != null) {
					return Stream.of(values).map(value -> {
						C entry = factory.apply(value);
						entry.setMatcher(matcher);
						entry.setOptionType(optionType);
						entry.setContextType(context.type);
						entry.setInitialOffset(offset);
						return entry;
					}).filter(entry -> entry.updatePrefix(context.prefix)).collect(Collectors.toList());
				}
			}
			break;
		default:
			break;
		}

		return Collections.emptyList();
	}

	// ------------- Hover service

	public static <T> String getHover(int offset, T document, ContentProvider<T> provider) throws Exception {
		TokenContext context = getTokenContext(offset, document, true, provider);
		switch (context.type) {
		case OPTION_NAME: {
			ConfigPropertyType<?> type = ConfigPropertyType.valueOf(context.prefix);
			return type != null ? type.getDescription() : null;
		}
		case OPTION_VALUE: {
			ConfigPropertyType<?> type = ConfigPropertyType.valueOf(context.name);
			return type != null ? type.getDescription() : null;
		}
		default:
			return null;
		}
	}

	private static class TokenContext {
		public final String prefix;
		public final String name; // option name, only available when context type is an option value
		public final CompletionContextType type;

		public TokenContext(String prefix, String name, CompletionContextType type) {
			this.prefix = prefix;
			this.name = name;
			this.type = type;
		}
	}

	private static <T> TokenContext getTokenContext(int offset, T document, boolean collectWord,
			ContentProvider<T> provider) throws Exception {
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
		if (collectWord) {
			int j = offset;
			int length = provider.getLength(document);
			while (j <= length) {
				c = provider.getChar(document, j);
				if (Character.isJavaIdentifierPart(c)) {
					prefix.append(c);
					j++;
				} else {
					break;
				}
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
		return new TokenContext(prefix.toString(), name != null ? name.toString() : null, type);
	}

	public static String getEndOfLine(String lineDelimiter) {
		if ("\n".equals(lineDelimiter)) {
			return "lf";
		} else if ("\r".equals(lineDelimiter)) {
			return "cr";
		} else if ("\r\n".equals(lineDelimiter)) {
			return "crlf";
		}
		return null;
	}
}
