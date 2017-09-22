package org.eclipse.ec4e.internal;

import org.eclipse.ec4e.services.completion.CharProvider;
import org.eclipse.jface.text.IDocument;

public class DocumentCharProvider implements CharProvider<IDocument> {

	public static final DocumentCharProvider INSTANCE = new DocumentCharProvider();

	private DocumentCharProvider() {
	}

	@Override
	public char getChar(IDocument document, int offset) throws Exception {
		return document.getChar(offset);
	}

	@Override
	public int getLength(IDocument document) {
		return document.getLength();
	}

}
