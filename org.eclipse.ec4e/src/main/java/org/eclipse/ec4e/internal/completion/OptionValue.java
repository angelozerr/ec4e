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

import java.util.ArrayList;
import java.util.List;

import org.ec4j.core.model.PropertyType;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.swt.graphics.Image;

/**
 * Position for option value.
 *
 */
public class OptionValue extends Position {

	private static final ICompletionProposal[] EMPTY_PROPOSALS = new ICompletionProposal[0];

	private final String name;
	private List<ICompletionProposal> proposals;

	public OptionValue(int offset, int length) {
		this(offset, length, null);
	}

	public OptionValue(int offset, int length, String name) {
		super(offset, length);
		this.name = name;
	}

	public OptionValue(int offset, PropertyType<?> optionType) {
		this(offset, optionType.getPossibleValues().iterator().next().length());
		for (String value : optionType.getPossibleValues()) {
			addProposal(value, value, null, null);
		}
	}

	public String getName() {
		return name;
	}

	public ICompletionProposal[] getProposals() {
		if (proposals == null) {
			return null;
		}
		return proposals.toArray(EMPTY_PROPOSALS);
	}

	public void addProposal(String name, String displayName, Image image, String doc) {
		if (proposals == null) {
			proposals = new ArrayList<ICompletionProposal>();
		}

		proposals.add(new PositionBasedCompletionProposal(name, this, getLength(), image, displayName, null, doc));
	}

	public void updateOffset(int baseOffset) {
		this.offset = baseOffset + offset;
	}
}
