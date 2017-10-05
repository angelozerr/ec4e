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
package org.eclipse.ec4e.internal;

import org.eclipse.ec4j.ContentProvider;
import org.eclipse.jface.text.IDocument;

/**
 * {@link ContentProvider} implementation for Eclipse {@link IDocument}.
 *
 */
public class DocumentContentProvider implements ContentProvider<IDocument> {

	public static final DocumentContentProvider INSTANCE = new DocumentContentProvider();

	private DocumentContentProvider() {
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
