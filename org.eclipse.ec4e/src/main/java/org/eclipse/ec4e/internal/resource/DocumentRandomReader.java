package org.eclipse.ec4e.internal.resource;

import java.io.IOException;

import org.ec4j.core.Resource.RandomReader;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;

/**
 * A {@link RandomReader} implementation that uses an underlying {@link IDocument}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class DocumentRandomReader implements RandomReader {
	private final IDocument document;

	public DocumentRandomReader(IDocument document) {
		super();
		this.document = document;
	}

	@Override
	public void close() throws IOException {
		/* nothing to do */
	}

	@Override
	public long getLength() {
		return document.getLength();
	}

	@Override
	public char read(long offset) throws IndexOutOfBoundsException {
		try {
			return document.getChar((int)offset);
		} catch (BadLocationException e) {
			throw new IndexOutOfBoundsException(e.getMessage());
		}
	}

}