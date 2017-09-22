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
		public int getLength(String document) {
			return document.length();
		}

	}

	char getChar(T document, int offset) throws Exception;
	
	int getLength(T document);
	

}