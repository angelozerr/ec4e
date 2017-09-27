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
package org.eclipse.ec4e.internal.validation;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.MonoReconciler;

public class EditorConfigReconciler extends MonoReconciler {

	public EditorConfigReconciler(IPreferenceStore preferenceStore, IResource resource) {
		super(new EditorConfigReconcilingStrategy(preferenceStore, resource), true);
	}

	@Override
	public void install(ITextViewer textViewer) {
		super.install(textViewer);
		((EditorConfigReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE)).install(textViewer);
	}

	@Override
	public void uninstall() {
		super.uninstall();
		((EditorConfigReconcilingStrategy) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE)).uninstall();
	}

}
