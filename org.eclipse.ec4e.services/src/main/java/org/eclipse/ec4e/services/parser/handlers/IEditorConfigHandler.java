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
package org.eclipse.ec4e.services.parser.handlers;

import org.eclipse.ec4e.services.parser.EditorConfigParser;
import org.eclipse.ec4e.services.parser.ParseException;

public interface IEditorConfigHandler<Section, Option> {

	public void setParser(EditorConfigParser<Section, Option> parser);
	
	public Section startSection();

	public void endSection(Section section);

	public void startMultiPatternSection(Section section);

	public void endMultiPatternSection(Section section);

	public void startPattern(Section section, int i);

	public void endPattern(Section section, String pattern, int i);

	public Option startOption();

	public void endOption(Option option, Section section);

	public void startOptionName(Option option);

	public void endOptionName(Option option, String name);

	public void startOptionValue(Option option, String name);

	public void endOptionValue(Option option, String value, String name);

	public void error(ParseException e);

}