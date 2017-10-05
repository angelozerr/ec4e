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

import java.util.Iterator;

import org.eclipse.ec4e.internal.DocumentContentProvider;
import org.eclipse.ec4j.EditorConfigService;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.IAnnotationModel;
import org.eclipse.jface.text.source.IAnnotationModelExtension2;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.ui.texteditor.MarkerAnnotation;

/**
 * Hover to display .editorconfig option name.
 *
 */
public class EditorConfigTextHover implements ITextHover {

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		if (hasProblem(textViewer, hoverRegion.getOffset())) {
			// There are marker annotation, don't return the editorconfig hover.
			return null;
		}
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

	/***
	 * Returns true if it exists a marker annotation in the given offset and false
	 * otherwise.
	 * 
	 * @param textViewer
	 * @param offset
	 * @return true if it exists a marker annotation in the given offset and false
	 *         otherwise.
	 */
	private static boolean hasProblem(ITextViewer textViewer, int offset) {
		if (!(textViewer instanceof ISourceViewer)) {
			return false;
		}

		IAnnotationModel annotationModel = ((ISourceViewer) textViewer).getAnnotationModel();
		Iterator<Annotation> iter = (annotationModel instanceof IAnnotationModelExtension2)
				? ((IAnnotationModelExtension2) annotationModel).getAnnotationIterator(offset, 1, true, true)
				: annotationModel.getAnnotationIterator();
		while (iter.hasNext()) {
			Annotation ann = iter.next();
			if (ann instanceof MarkerAnnotation) {
				return true;
			}
		}
		return false;
	}
}
