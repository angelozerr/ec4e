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

import org.eclipse.ec4e.services.EditorConfigService;
import org.eclipse.ec4e.services.completion.CharProvider;
import org.eclipse.ec4e.services.completion.CompletionContext;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
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

	private static final CharProvider<IDocument> DOCUMENT_CHAR_PROVIDER = new CharProvider<IDocument>() {
		@Override
		public char getChar(IDocument document, int offset) throws Exception {
			return document.getChar(offset);
		}

		@Override
		public String get(IDocument document, int start, int end) throws Exception {
			return document.get(start, end - start);
		}
	};

	@Override
	public ICompletionProposal[] computeCompletionProposals(ITextViewer viewer, int offset) {
		IDocument document = viewer.getDocument();
		try {
			CompletionContext context = EditorConfigService.getCompletionContext(offset,
					document, DOCUMENT_CHAR_PROVIDER);
			switch (context.type) {
			case OPTION_NAME:
				return computeOptionNameCompletionProposals(viewer, offset, context.prefix);
			case OPTION_VALUE:
				return computeOptionValueCompletionProposals(viewer, offset, context.prefix);
			default:
				return null;
			}
		} catch (Exception e) {
		}
		return null;
	}

	private ICompletionProposal[] computeOptionNameCompletionProposals(ITextViewer viewer, int offset, String prefix) {
		System.err.println("name=" + prefix);
		// Do nothing
		//Stream.of(ConfigPropertyType.ALL_TYPES).filter(t -> t.getName().)
		return null;
	}

	private ICompletionProposal[] computeOptionValueCompletionProposals(ITextViewer viewer, int offset, String prefix) {
		System.err.println("value=" + prefix);
		// Do nothing
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
