/**
 *  Copyright (c) 2015-2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.ec4e.internal;

import org.eclipse.tm4e.ui.text.TMPresentationReconciler;
import org.eclipse.ui.editors.text.TextEditor;

/**
 * Basic TextMate TextEditor to consume the {@link TMPresentationReconciler}.
 *
 */
public class TextMateEditor extends TextEditor {

	public TextMateEditor() {
		setSourceViewerConfiguration(new TextMateSourceViewerConfiguration(super.getPreferenceStore()));
	}
}
