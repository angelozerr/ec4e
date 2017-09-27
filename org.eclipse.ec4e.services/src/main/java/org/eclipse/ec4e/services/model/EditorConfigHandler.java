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
package org.eclipse.ec4e.services.model;

import org.eclipse.ec4e.services.parser.ParseException;
import org.eclipse.ec4e.services.parser.handlers.AbstractEditorConfigHandler;

class EditorConfigHandler extends AbstractEditorConfigHandler<Section, Option> {

	private final EditorConfig editorConfig;

	public EditorConfigHandler() {
		this.editorConfig = new EditorConfig();
	}

	@Override
	public Section startSection() {
		return new Section(editorConfig);
	}

	@Override
	public void endSection(Section section) {
		editorConfig.addSection(section);
	}

	@Override
	public void startMultiPatternSection(Section section) {

	}

	@Override
	public void endMultiPatternSection(Section section) {

	}

	@Override
	public void startPattern(Section section, int i) {

	}

	@Override
	public void endPattern(Section section, String pattern, int i) {
		section.addPattern(pattern);
	}

	@Override
	public Option startOption() {
		return new Option();
	}

	@Override
	public void endOption(Option option, Section section) {
		if (section != null) {
			section.addOption(option);
		} else if ("root".equals(option.getName())) {
			editorConfig.setRoot("true".equals(option.getValue()));
		}
	}

	@Override
	public void startOptionName(Option option) {

	}

	@Override
	public void endOptionName(Option option, String name) {
		option.setName(name);
	}

	@Override
	public void startOptionValue(Option option, String name) {

	}

	@Override
	public void endOptionValue(Option option, String value, String name) {
		option.setValue(value);
	}

	@Override
	public void error(ParseException e) {
		e.printStackTrace();
	}

	public EditorConfig getEditorConfig() {
		return editorConfig;
	}

}