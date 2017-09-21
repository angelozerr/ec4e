package org.eclipse.ec4e.internal.completion;

import org.eclipse.ec4e.services.completion.CompletionEntry;
import org.eclipse.ec4e.services.model.options.ConfigPropertyType;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.BoldStylerProvider;
import org.eclipse.jface.text.contentassist.CompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.ICompletionProposalExtension7;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class EditorConfigCompletionProposal extends CompletionEntry
		implements ICompletionProposal, ICompletionProposalExtension7 {

	private StyledString fDisplayString;
	private String fReplacementString;
	private int cursorPosition;
	private int replacementOffset;
	private int replacementlength;

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
	public Point getSelection(IDocument document) {
		return null;
	}

	@Override
	public String getAdditionalProposalInfo() {
		initIfNeeded();
		ConfigPropertyType<?> optionType = getOptionType();
		return optionType != null ? optionType.getDisplayLabel() : null;
	}

	@Override
	public String getDisplayString() {
		initIfNeeded();
		return super.getName();
	}

	@Override
	public Image getImage() {
		return null;
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
}
