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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ec4j.AbstractEditorConfigManager;
import org.eclipse.ec4j.ResourceProvider;

/**
 * IDE editorconfig manager.
 *
 */
public class IDEEditorConfigManager extends AbstractEditorConfigManager<IResource> {

	private static final ResourceProvider<IResource> ECLIPSE_RESOURCE_PROVIDER = new ResourceProvider<IResource>() {

		@Override
		public IResource getParent(IResource file) {
			IContainer parent = file.getParent();
			if (parent != null && parent.getType() != IResource.ROOT) {
				return parent;
			}
			return null;
		}

		@Override
		public IResource getResource(IResource parent, String child) {
			return ((IContainer) parent).getFile(new Path(child));
		}

		@Override
		public boolean exists(IResource file) {
			return file.exists();
		}

		@Override
		public String getPath(IResource file) {
			return file.getLocation().toString().replaceAll("[\\\\]", "/");
		}

		@Override
		public Reader getContent(IResource configFile) throws IOException {
			try {
				return new InputStreamReader(((IFile) configFile).getContents(), StandardCharsets.UTF_8);
			} catch (CoreException e) {
				throw new IOException(e);
			}
		}
	};

	public static final IDEEditorConfigManager INSTANCE = new IDEEditorConfigManager();

	public IDEEditorConfigManager() {
		super(ECLIPSE_RESOURCE_PROVIDER);
	}

	public static IDEEditorConfigManager getInstance() {
		return INSTANCE;
	}

}
