/*
 * Copyright 2014 Nathan Jones
 *
 * This file is part of "EditorConfig Eclipse".
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.eclipse.ec4e.services.model.options;

import java.util.HashMap;
import java.util.Map;

public abstract class ConfigPropertyType<T> implements Comparable<ConfigPropertyType<T>> {

	private static final String[] BOOLEAN_POSSIBLE_VALUES = new String[] { "true", "false" };

	public static class IndentStyle extends ConfigPropertyType<IndentStyleOption> {

		private static final String[] POSSIBLE_VALUES = new String[] { "tab", "space" };

		@Override
		public String getName() {
			return "indent_style";
		}

		@Override
		public String getDescription() {
			return "set to tab or space to use hard tabs or soft tabs respectively.";
		}

		@Override
		public ValueParser<IndentStyleOption> getValueParser() {
			return new EnumValueParser<IndentStyleOption>(IndentStyleOption.class);
		}

		@Override
		public ValueValidator<IndentStyleOption> getValueValidator() {
			return new EnumValueValidator<IndentStyleOption>(IndentStyleOption.class);
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}

	}

	public static class IndentSize extends ConfigPropertyType<Integer> {

		private static final String[] POSSIBLE_VALUES = new String[] { "1", "2", "3", "4", "5", "6", "7", "8", "tab" };

		@Override
		public String getName() {
			return "indent_size";
		}

		@Override
		public String getDescription() {
			return "a whole number defining the number of columns used for each indentation level and the width of soft tabs (when supported). When set to tab, the value of tab_width (if specified) will be used.";
		}

		@Override
		public ValueParser<Integer> getValueParser() {
			return ValueParser.POSITIVE_INT_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Integer> getValueValidator() {
			return ValueValidator.POSITIVE_INT_VALUE_VALIDATOR;
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}

	}

	public static class TabWidth extends ConfigPropertyType<Integer> {

		private static final String[] POSSIBLE_VALUES = new String[] { "1", "2", "3", "4", "5", "6", "7", "8" };

		@Override
		public String getName() {
			return "tab_width";
		}

		@Override
		public String getDescription() {
			return "a whole number defining the number of columns used to represent a tab character. This defaults to the value of indent_size and doesn't usually need to be specified.";
		}

		@Override
		public ValueParser<Integer> getValueParser() {
			return ValueParser.POSITIVE_INT_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Integer> getValueValidator() {
			return ValueValidator.POSITIVE_INT_VALUE_VALIDATOR;
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}
	}

	public static class EndOfLine extends ConfigPropertyType<EndOfLineOption> {

		private static final String[] POSSIBLE_VALUES = new String[] { "lf", "crlf", "cr" };

		@Override
		public String getName() {
			return "end_of_line";
		}

		@Override
		public String getDescription() {
			return "set to lf, cr, or crlf to control how line breaks are represented.";
		}

		@Override
		public ValueParser<EndOfLineOption> getValueParser() {
			return new EnumValueParser<EndOfLineOption>(EndOfLineOption.class);
		}

		@Override
		public ValueValidator<EndOfLineOption> getValueValidator() {
			return new EnumValueValidator<EndOfLineOption>(EndOfLineOption.class);
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}
	}

	public static class Charset extends ConfigPropertyType<String> {

		private static final String[] POSSIBLE_VALUES = new String[] { "utf-8", "utf-8-bom", "utf-16be", "utf-16le",
				"latin1", "tab" };

		@Override
		public String getName() {
			return "charset";
		}

		@Override
		public String getDescription() {
			return "set to latin1, utf-8, utf-8-bom, utf-16be or utf-16le to control the character set. Use of utf-8-bom is discouraged.";
		}

		@Override
		public ValueParser<String> getValueParser() {
			return ValueParser.IDENTITY_VALUE_PARSER;
		}

		@Override
		public ValueValidator<String> getValueValidator() {
			return ValueValidator.IDENTITY_VALUE_VALIDATOR;
		}

		@Override
		public String[] getPossibleValues() {
			return POSSIBLE_VALUES;
		}

	}

	public static class TrimTrailingWhitespace extends ConfigPropertyType<Boolean> {

		@Override
		public String getName() {
			return "trim_trailing_whitespace";
		}

		@Override
		public String getDescription() {
			return "set to true to remove any whitespace characters preceding newline characters and false to ensure it doesn't.";
		}

		@Override
		public ValueParser<Boolean> getValueParser() {
			return ValueParser.BOOLEAN_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Boolean> getValueValidator() {
			return ValueValidator.BOOLEAN_VALUE_VALIDATOR;
		}

		@Override
		public String[] getPossibleValues() {
			return BOOLEAN_POSSIBLE_VALUES;
		}
	}

	public static class InsertFinalNewline extends ConfigPropertyType<Boolean> {

		@Override
		public String getName() {
			return "insert_final_newline";
		}

		@Override
		public String getDescription() {
			return "set to true to ensure file ends with a newline when saving and false to ensure it doesn't.";
		}

		@Override
		public ValueParser<Boolean> getValueParser() {
			return ValueParser.BOOLEAN_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Boolean> getValueValidator() {
			return ValueValidator.BOOLEAN_VALUE_VALIDATOR;
		}

		@Override
		public String[] getPossibleValues() {
			return BOOLEAN_POSSIBLE_VALUES;
		}
	}

	public static class Root extends ConfigPropertyType<Boolean> {

		@Override
		public String getName() {
			return "root";
		}

		@Override
		public String getDescription() {
			return "special property that should be specified at the top of the file outside of any sections. Set to true to stop .editorconfig files search on current file.";
		}

		@Override
		public ValueParser<Boolean> getValueParser() {
			return ValueParser.BOOLEAN_VALUE_PARSER;
		}

		@Override
		public ValueValidator<Boolean> getValueValidator() {
			return ValueValidator.BOOLEAN_VALUE_VALIDATOR;
		}

		@Override
		public String[] getPossibleValues() {
			return BOOLEAN_POSSIBLE_VALUES;
		}
	}

	public static final Root ROOT = new Root();
	public static final IndentStyle INDENT_STYLE = new IndentStyle();
	public static final IndentSize INDENT_SIZE = new IndentSize();
	public static final TabWidth TAB_WIDTH = new TabWidth();
	public static final EndOfLine END_OF_LINE = new EndOfLine();
	public static final Charset CHARSET = new Charset();
	public static final TrimTrailingWhitespace TRIM_TRAILING_WHITESPACE = new TrimTrailingWhitespace();
	public static final InsertFinalNewline INSERT_FINAL_NEWLINE = new InsertFinalNewline();

	public static final ConfigPropertyType<?>[] ALL_TYPES = { ROOT, INDENT_STYLE, INDENT_SIZE, TAB_WIDTH, END_OF_LINE,
			CHARSET, TRIM_TRAILING_WHITESPACE, INSERT_FINAL_NEWLINE };
	private static final Map<String, ConfigPropertyType<?>> ALL_TYPES_MAP = new HashMap<String, ConfigPropertyType<?>>();
	private static final Map<String, Integer> ALL_TYPES_INDICES = new HashMap<String, Integer>();
	static {
		int index = 0;
		for (final ConfigPropertyType<?> type : ALL_TYPES) {
			ALL_TYPES_MAP.put(type.getName().toUpperCase(), type);
			ALL_TYPES_INDICES.put(type.getName().toUpperCase(), index);
			index += 1;
		}
	}

	public static ConfigPropertyType<?> valueOf(final String name) {
		if (name == null) {
			return null;
		}
		return ALL_TYPES_MAP.get(name.toUpperCase());
	}

	public abstract String getName();

	public abstract String getDescription();

	public abstract ValueParser<T> getValueParser();

	public abstract ValueValidator<T> getValueValidator();

	public void validate(String value) throws ConfigPropertyException {
		getValueValidator().validate(getName(), value);
	}

	public abstract String[] getPossibleValues();

	private Integer getIndex() {
		return ALL_TYPES_INDICES.get(getName());
	}

	@Override
	public int compareTo(final ConfigPropertyType<T> o) {
		return getIndex().compareTo(o.getIndex());
	}

	@Override
	public String toString() {
		return getName();
	}

}
