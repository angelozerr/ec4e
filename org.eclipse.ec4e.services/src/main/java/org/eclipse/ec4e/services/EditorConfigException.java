package org.eclipse.ec4e.services;

import java.io.IOException;

public class EditorConfigException extends Exception {

	public EditorConfigException(String message, IOException e) {
		super(message, e);
	}

}
