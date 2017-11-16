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

import org.ec4j.core.EditorConfigConstants;
import org.eclipse.core.resources.IResource;
import org.eclipse.ec4e.internal.folding.EditorConfigFoldingStrategy;
import org.eclipse.ec4e.internal.validation.ValidateAppliedOptionsStrategy;
import org.eclipse.ec4e.internal.validation.ValidateEditorConfigStrategy;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.MonoReconciler;

public class EditorConfigReconciler extends MonoReconciler {

	public EditorConfigReconciler(IPreferenceStore preferenceStore, IResource resource) {
		super(create(preferenceStore, resource), true);
	}

	private static IReconcilingStrategy create(IPreferenceStore preferenceStore, IResource resource) {
		if (EditorConfigConstants.EDITORCONFIG.equals(resource.getName())) {
			// it's an .editorconfig file, add validation
			CompositeReconcilingStrategy strategy = new CompositeReconcilingStrategy();
			strategy.setReconcilingStrategies(new IReconcilingStrategy[] { new ValidateEditorConfigStrategy(resource),
					new ValidateAppliedOptionsStrategy(preferenceStore, resource), new EditorConfigFoldingStrategy() });
			return strategy;
		}
		return new ValidateAppliedOptionsStrategy(preferenceStore, resource);
	}

	@Override
	public void install(ITextViewer textViewer) {
		super.install(textViewer);
		((IReconciler) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE)).install(textViewer);
	}

	@Override
	public void uninstall() {
		super.uninstall();
		((IReconciler) getReconcilingStrategy(IDocument.DEFAULT_CONTENT_TYPE)).uninstall();
	}

}
