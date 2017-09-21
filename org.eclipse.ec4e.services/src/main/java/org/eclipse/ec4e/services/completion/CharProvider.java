package org.eclipse.ec4e.services.completion;

public interface CharProvider<T> {

	public static final StringCharProvider INSTANCE = new StringCharProvider();

	public static class StringCharProvider implements CharProvider<String> {

		public static final StringCharProvider INSTANCE = new StringCharProvider();

		@Override
		public char getChar(String document, int index) throws Exception {
			return document.charAt(index);
		}

		@Override
		public String get(String document, int start, int end) throws Exception {
			return document.substring(start, end);
		}
	}

	char getChar(T document, int offset) throws Exception;

	String get(T document, int start, int end) throws Exception;

}