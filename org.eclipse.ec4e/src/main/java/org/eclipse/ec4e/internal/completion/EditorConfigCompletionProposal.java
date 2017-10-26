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
package org.eclipse.ec4e.internal.completion;

import org.eclipse.ec4e.internal.EditorConfigImages;
import org.eclipse.ec4j.services.completion.CompletionContextType;
import org.eclipse.ec4j.services.completion.CompletionEntry;
import org.eclipse.ec4j.core.model.optiontypes.OptionType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.BadPositionCategoryException;
import org.eclipse.jface.text.DocumentEvent;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IPositionUpdater;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.BoldStylerProvider;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension2;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension7;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.link.ILinkedModeListener;
import org.eclipse.jface.text.link.InclusivePositionUpdater;
import org.eclipse.jface.text.link.LinkedModeModel;
import org.eclipse.jface.text.link.LinkedModeUI;
import org.eclipse.jface.text.link.LinkedModeUI.ExitFlags;
import org.eclipse.jface.text.link.LinkedModeUI.IExitPolicy;
import org.eclipse.jface.text.link.LinkedPositionGroup;
import org.eclipse.jface.text.link.ProposalPosition;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.ui.texteditor.link.EditorLinkedModeUI;

/**
 * Completion proposal for .editorconfig option names, values.
 *
 */
public class EditorConfigCompletionProposal extends CompletionEntry implements ICompletionProposal,
		ICompletionProposalExtension, ICompletionProposalExtension2, ICompletionProposalExtension7 {

	private StyledString fDisplayString;
	private String fReplacementString;
	private int cursorPosition;
	private int replacementOffset;
	private int replacementlength;
	private ITextViewer fTextViewer;
	private boolean fToggleEating;

	private IRegion fSelectedRegion;
	private IPositionUpdater fUpdater;

	public EditorConfigCompletionProposal(String name) {
		super(name);
	}

	@Override
	public void apply(IDocument document) {
		initIfNeeded();
		CompletionProposal proposal = new CompletionProposal(getReplacementString(), getReplacementOffset(),
				getReplacementLength(), getCursorPosition(), getImage(), getDisplayString(), getContextInformation(),
				getAdditionalProposalInfo());
		proposal.apply(document);
	}

	@Override
	public void apply(ITextViewer viewer, char trigger, int stateMask, int offset) {
		initIfNeeded();
		IDocument document = viewer.getDocument();
		if (fTextViewer == null) {
			fTextViewer = viewer;
		}
		// don't eat if not in preferences, XOR with modifier key 1 (Ctrl)
		// but: if there is a selection, replace it!
		Point selection = viewer.getSelectedRange();
		fToggleEating = (stateMask & SWT.MOD1) != 0;
		int newLength = selection.x + selection.y - getReplacementOffset();
		if ((insertCompletion() ^ fToggleEating) && newLength >= 0) {
			setReplacementLength(newLength);
		}
		apply(document, trigger, offset);
		fToggleEating = false;
	}

	@Override
	public void apply(IDocument document, char trigger, int offset) {
		initIfNeeded();
		// compute replacement string
		String replacement = computeReplacementString(document, offset);
		setReplacementString(replacement);

		// apply the replacement.
		CompletionProposal proposal = new CompletionProposal(getReplacementString(), getReplacementOffset(),
				getReplacementLength(), getCursorPosition(), getImage(), getDisplayString(), getContextInformation(),
				getAdditionalProposalInfo());
		// we currently don't do anything special for which character
		// selected the proposal, and where the cursor offset is
		// but we might in the future...
		proposal.apply(document);
		int baseOffset = getReplacementOffset();

		String[] possibleValues = getOptionType().getPossibleValues();
		if (getContextType() == CompletionContextType.OPTION_NAME && possibleValues != null
				&& getTextViewer() != null) {

			try {
				LinkedModeModel model = new LinkedModeModel();
				LinkedPositionGroup group = new LinkedPositionGroup();

				OptionType<?> optionType = getOptionType();
				OptionValue value = new OptionValue(replacement.length() - optionType.getPossibleValues()[0].length(),
						optionType);
				value.updateOffset(baseOffset);

				ensurePositionCategoryInstalled(document, model);
				document.addPosition(getCategory(), value);
				group.addPosition(new ProposalPosition(document, value.getOffset(), value.getLength(),
						LinkedPositionGroup.NO_STOP, value.getProposals()));
				model.addGroup(group);

				model.forceInstall();

				LinkedModeUI ui = new EditorLinkedModeUI(model, getTextViewer());
				ui.setExitPosition(getTextViewer(), baseOffset + replacement.length(), 0, Integer.MAX_VALUE);
				ui.setExitPolicy(new ExitPolicy(')', document));
				ui.setDoContextInfo(true);
				ui.setCyclingMode(LinkedModeUI.CYCLE_WHEN_NO_PARENT);
				ui.enter();

				fSelectedRegion = ui.getSelectedRegion();

			} catch (BadLocationException e) {
				ensurePositionCategoryRemoved(document);
				// JavaScriptPlugin.log(e);
				// openErrorDialog(e);
			} catch (BadPositionCategoryException e) {
				ensurePositionCategoryRemoved(document);
				// JavaScriptPlugin.log(e);
				// openErrorDialog(e);
			}

		} else {
			int newOffset = baseOffset + replacement.length();
			fSelectedRegion = new Region(newOffset, 0);
		}
	}

	private ITextViewer getTextViewer() {
		return fTextViewer;
	}

	private String computeReplacementString(IDocument document, int offset) {
		if (getContextType() == CompletionContextType.OPTION_NAME) {
			String first = getOptionType().getPossibleValues()[0];
			return new StringBuilder(getReplacementString()).append(" = ").append(first).toString();
		}
		return getReplacementString();
	}

	private boolean insertCompletion() {
		return true;
	}

	@Override
	public Point getSelection(IDocument document) {
		initIfNeeded();
		if (fSelectedRegion == null) {
			return new Point(getReplacementOffset(), 0);
		}
		return new Point(fSelectedRegion.getOffset(), fSelectedRegion.getLength());
	}

	@Override
	public String getAdditionalProposalInfo() {
		initIfNeeded();
		OptionType<?> optionType = getOptionType();
		return optionType != null ? optionType.getDescription() : null;
	}

	@Override
	public String getDisplayString() {
		initIfNeeded();
		return super.getName();
	}

	@Override
	public Image getImage() {
		switch (getContextType()) {
		case OPTION_NAME:
			return EditorConfigImages.getImage(EditorConfigImages.IMG_PROPERTY);
		case OPTION_VALUE:
			return EditorConfigImages.getImage(EditorConfigImages.IMG_VALUE);
		default:
			return null;
		}
	}

	@Override
	public IContextInformation getContextInformation() {
		return null;
	}

	@Override
	public StyledString getStyledDisplayString(IDocument document, int offset, BoldStylerProvider boldStylerProvider) {
		// Highlight matched prefix
		StyledString styledDisplayString = new StyledString();
		styledDisplayString.append(getStyledDisplayString());

		String pattern = getPatternToEmphasizeMatch(document, offset);
		if (pattern != null && pattern.length() > 0) {
			String displayString = styledDisplayString.getString();
			int[] bestSequence = super.getMatcher().bestSubsequence(displayString, pattern);
			int highlightAdjustment = 0;
			for (int index : bestSequence) {
				styledDisplayString.setStyle(index + highlightAdjustment, 1, boldStylerProvider.getBoldStyler());
			}
		}
		return styledDisplayString;
	}

	public StyledString getStyledDisplayString() {
		initIfNeeded();
		return fDisplayString;
	}

	private void initIfNeeded() {
		if (getReplacementString() != null) {
			return;
		}
		String name = super.getName();
		String prefix = super.getPrefix();
		setReplacementString(name);
		setCursorPosition(name.length());
		setReplacementOffset(getInitialOffset() - prefix.length());
		setReplacementLength(prefix.length());
		this.fDisplayString = new StyledString(name);
	}

	private void setReplacementLength(int replacementlength) {
		this.replacementlength = replacementlength;
	}

	private String getReplacementString() {
		return fReplacementString;
	}

	private void setReplacementString(String replacementString) {
		fReplacementString = replacementString;
	}

	private int getReplacementLength() {
		return replacementlength;
	}

	private int getReplacementOffset() {
		return replacementOffset;
	}

	private void setReplacementOffset(int replacementOffset) {
		this.replacementOffset = replacementOffset;
	}

	private int getCursorPosition() {
		return cursorPosition;
	}

	private void setCursorPosition(int cursorPosition) {
		this.cursorPosition = cursorPosition;
	}

	private String getPatternToEmphasizeMatch(IDocument document, int offset) {
		int start = getPrefixCompletionStart(document, offset);
		int patternLength = offset - start;
		String pattern = null;
		try {
			pattern = document.get(start, patternLength);
		} catch (BadLocationException e) {
			// return null
		}
		return pattern;
	}

	private int getPrefixCompletionStart(IDocument document, int completionOffset) {
		initIfNeeded();
		return replacementOffset;
	}

	@Override
	public boolean isValidFor(IDocument document, int offset) {
		return false; // validate(document, offset, null);
	}

	@Override
	public char[] getTriggerCharacters() {
		return null;
	}

	@Override
	public int getContextInformationPosition() {
		initIfNeeded();
		// https://bugs.eclipse.org/bugs/show_bug.cgi?id=110355
		// return getCursorPosition();
		if (getContextInformation() == null)
			return getReplacementOffset() - 1;
		return getReplacementOffset() + getCursorPosition();
	}

	@Override
	public void selected(ITextViewer viewer, boolean smartToggle) {

	}

	@Override
	public void unselected(ITextViewer viewer) {

	}

	@Override
	public boolean validate(IDocument document, int offset, DocumentEvent event) {
		initIfNeeded();
		if (offset < replacementOffset) {
			return false;
		}

		int replacementOffset = getReplacementOffset();
		String word = getReplacementString();
		int wordLength = word == null ? 0 : word.length();
		if (offset > replacementOffset + wordLength) {
			return false;
		}

		try {
			int length = offset - replacementOffset;
			String start = document.get(replacementOffset, length);
			return super.updatePrefix(start);
		} catch (BadLocationException x) {
		}

		return false;
	}

	private void ensurePositionCategoryInstalled(final IDocument document, LinkedModeModel model) {
		if (!document.containsPositionCategory(getCategory())) {
			document.addPositionCategory(getCategory());
			fUpdater = new InclusivePositionUpdater(getCategory());
			document.addPositionUpdater(fUpdater);

			model.addLinkingListener(new ILinkedModeListener() {

				/*
				 * @see org.eclipse.jface.text.link.ILinkedModeListener#left(org.
				 * eclipse.jface.text.link.LinkedModeModel, int)
				 */
				public void left(LinkedModeModel environment, int flags) {
					ensurePositionCategoryRemoved(document);
				}

				public void suspend(LinkedModeModel environment) {
				}

				public void resume(LinkedModeModel environment, int flags) {
				}
			});
		}
	}

	private void ensurePositionCategoryRemoved(IDocument document) {
		if (document.containsPositionCategory(getCategory())) {
			try {
				document.removePositionCategory(getCategory());
			} catch (BadPositionCategoryException e) {
				// ignore
			}
			document.removePositionUpdater(fUpdater);
		}
	}

	protected static final class ExitPolicy implements IExitPolicy {

		final char fExitCharacter;
		private final IDocument fDocument;

		public ExitPolicy(char exitCharacter, IDocument document) {
			fExitCharacter = exitCharacter;
			fDocument = document;
		}

		public ExitFlags doExit(LinkedModeModel environment, VerifyEvent event, int offset, int length) {

			if (event.character == fExitCharacter) {
				if (environment.anyPositionContains(offset))
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, false);
				else
					return new ExitFlags(ILinkedModeListener.UPDATE_CARET, true);
			}

			switch (event.character) {
			case ';':
				return new ExitFlags(ILinkedModeListener.NONE, true);
			case SWT.CR:
				// when entering an anonymous class as a parameter, we don't
				// want
				// to jump after the parenthesis when return is pressed
				if (offset > 0) {
					try {
						if (fDocument.getChar(offset - 1) == '{')
							return new ExitFlags(ILinkedModeListener.EXIT_ALL, true);
					} catch (BadLocationException e) {
					}
				}
				return null;
			default:
				return null;
			}
		}

	}

	private String getCategory() {
		return "EditorConfig_" + toString(); //$NON-NLS-1$
	}

}
