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

public class Option {

	private final String name;

	private final EditorConfig editorConfig;
	
	private String value;

	public Option(String name, EditorConfig editorConfig) {
		this.name = name;
		this.editorConfig = editorConfig;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setValue(String value) {
		this.value = value;
	}

	@Override
	public String toString() {
		return new StringBuilder(name).append(" = ").append(value).toString();
	}

	public int getIntValue() {
		return Integer.parseInt(getValue());
	}

	public boolean isValid() {
		return true;
	}
}
