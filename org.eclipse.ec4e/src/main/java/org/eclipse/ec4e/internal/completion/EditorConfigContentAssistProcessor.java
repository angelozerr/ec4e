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
package org.eclipse.ec4e.internal.completion;

import org.eclipse.ec4e.internal.DocumentCharProvider;
import org.eclipse.ec4e.services.EditorConfigService;
import org.eclipse.ec4e.services.completion.ICompletionEntryMatcher;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;

/**
 * Content assist processor for .editorconfig option names, values.
 *
 */
public class EditorConfigContentAssistProcessor implements IContentAssistProcessor {

	private static final char[] AUTO_ACTIVATION_CHARACTERS = new char[] { '=' };

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		try {
			return EditorConfigService.getCompletionEntries(offset, document, ICompletionEntryMatcher.LCS,
					EditorConfigCompletionProposal::new, DocumentCharProvider.INSTANCE)
					.stream().toArray(ICompletionProposal[]::new);
		} catch (Exception e) {
		}
		return null;
	}

	@Override
	public IContextInformation[] computeContextInformation(ITextViewer viewer, int offset) {
		return null;
	}

	@Override
	public char[] getCompletionProposalAutoActivationCharacters() {
		return AUTO_ACTIVATION_CHARACTERS;
	}

	@Override
	public char[] getContextInformationAutoActivationCharacters() {
		return null;
	}

	@Override
	public String getErrorMessage() {
		return null;
	}

	@Override
	public IContextInformationValidator getContextInformationValidator() {
		return null;
	}

}
