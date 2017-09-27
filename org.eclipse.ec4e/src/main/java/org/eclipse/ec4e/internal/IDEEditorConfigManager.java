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
package org.eclipse.ec4e.internal;

import org.eclipse.ec4e.services.EditorConfigManager;

/**
 * IDE editorconfig manager.
 *
 */
public class IDEEditorConfigManager extends EditorConfigManager {

	public static final IDEEditorConfigManager INSTANCE = new IDEEditorConfigManager();

	public static IDEEditorConfigManager getInstance() {
		return INSTANCE;
	}

}
