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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.presentation.IPresentationReconciler;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.tm4e.languageconfiguration.LanguageConfigurationAutoEditStrategy;
import org.eclipse.tm4e.ui.text.TMPresentationReconciler;
import org.eclipse.ui.editors.text.TextSourceViewerConfiguration;

/**
 * TextMate source viewer configuration.
 *
 */
public class TextMateSourceViewerConfiguration extends TextSourceViewerConfiguration {

	public TextMateSourceViewerConfiguration(IPreferenceStore preferenceStore) {
		super(preferenceStore);
	}

	@Override
	public IPresentationReconciler getPresentationReconciler(ISourceViewer sourceViewer) {
		TMPresentationReconciler presentationReconciler = new TMPresentationReconciler();
		presentationReconciler.setOpenImportDialogWhenGrammarNotFound(true);
		return presentationReconciler;
	}

	@Override
	public IAutoEditStrategy[] getAutoEditStrategies(ISourceViewer sourceViewer, String contentType) {
		return new IAutoEditStrategy[] { new LanguageConfigurationAutoEditStrategy() };
	}
}
