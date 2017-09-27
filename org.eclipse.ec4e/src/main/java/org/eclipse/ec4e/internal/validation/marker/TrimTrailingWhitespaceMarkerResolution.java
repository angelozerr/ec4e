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
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.texteditor.AbstractTextEditor;

final class TrimTrailingWhitespaceMarkerResolution extends AbstractMarkerResolution {

	public static final IMarkerResolution INSTANCE = new TrimTrailingWhitespaceMarkerResolution();

	@Override
	public String getLabel() {
		return "Fix trim trailing whitespace";
	}

	@Override
	protected boolean run(IMarker marker, AbstractTextEditor editor, IDocument doc) {
		int start = marker.getAttribute(IMarker.CHAR_START, -1);
		int end = marker.getAttribute(IMarker.CHAR_END, -1);
		StyledText text = (StyledText) editor.getAdapter(Control.class);
		text.getDisplay().syncExec(() -> {
			text.replaceTextRange(start, end - start, "");
			text.setCaretOffset(start);
		});
		return true;
	}

}