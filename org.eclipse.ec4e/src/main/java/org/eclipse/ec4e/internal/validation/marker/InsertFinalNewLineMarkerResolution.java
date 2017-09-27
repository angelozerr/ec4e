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
package org.eclipse.ec4e.internal.validation.marker;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IDocumentExtension4;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.texteditor.AbstractTextEditor;

final class InsertFinalNewLineMarkerResolution extends AbstractMarkerResolution {

	public static final IMarkerResolution INSTANCE = new InsertFinalNewLineMarkerResolution();

	@Override
	public String getLabel() {
		return "Fix insert final new line";
	}

	@Override
	protected boolean run(IMarker marker, AbstractTextEditor editor, IDocument doc) {
		String delimiter = ((IDocumentExtension4) doc).getDefaultLineDelimiter();
		int offset = marker.getAttribute(IMarker.CHAR_END, -1);
		// doc.replace(offset, 0, delimiter);
		// Update StyledText instead of updating IDocument because on Windows
		// the render of StyledText crashes if we add '\r\n' at the end of the
		// IDocument.
		StyledText text = (StyledText) editor.getAdapter(Control.class);
		text.getDisplay().syncExec(() -> {
			text.replaceTextRange(offset, 0, delimiter);
			text.setCaretOffset(offset + delimiter.length());
		});
		return true;
	}

}