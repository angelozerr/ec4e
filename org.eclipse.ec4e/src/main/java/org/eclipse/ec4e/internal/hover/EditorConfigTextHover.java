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
package org.eclipse.ec4e.internal.hover;

import org.eclipse.ec4e.internal.DocumentContentProvider;
import org.eclipse.ec4e.services.EditorConfigService;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

/**
 * Hover to display .editorconfig option name.
 *
 */
public class EditorConfigTextHover implements ITextHover {

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		try {
			return EditorConfigService.getHover(hoverRegion.getOffset(), textViewer.getDocument(),
					DocumentContentProvider.INSTANCE);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 1);
	}

}
