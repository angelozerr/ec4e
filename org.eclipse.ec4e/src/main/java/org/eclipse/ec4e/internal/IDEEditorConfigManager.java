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
import java.util.HashMap;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Path;
import org.eclipse.ec4e.EditorConfigPlugin;
import org.eclipse.ec4j.AbstractEditorConfigManager;
import org.eclipse.ec4j.EditorConfigConstants;
import org.eclipse.ec4j.ResourceProvider;
import org.eclipse.ec4j.model.EditorConfig;

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

	/**
	 * {@link EditorConfig} instance cache.
	 *
	 */
	private static class EditorConfigCache extends HashMap<IResource, EditorConfig>
			implements IResourceChangeListener, IResourceDeltaVisitor {

		private static final long serialVersionUID = 1L;

		@Override
		public void resourceChanged(IResourceChangeEvent event) {
			if (event.getType() == IResourceChangeEvent.POST_CHANGE) {
				IResourceDelta delta = event.getDelta();
				if (delta != null) {
					try {
						delta.accept(this);
					} catch (CoreException e) {
						EditorConfigPlugin.logError("Error while .editorconfig resource changed", e);
					}
				}
			}
		}

		@Override
		public boolean visit(IResourceDelta delta) throws CoreException {
			IResource resource = delta.getResource();
			if (resource == null) {
				return false;
			}
			switch (resource.getType()) {
			case IResource.ROOT:
			case IResource.PROJECT:
			case IResource.FOLDER:
				return true;
			case IResource.FILE:
				IFile file = (IFile) resource;
				if (EditorConfigConstants.EDITORCONFIG.equals(file.getName())
						&& delta.getKind() == IResourceDelta.CHANGED) {
					super.remove(file);
				}
			}
			return false;
		}
	}

	public static final IDEEditorConfigManager INSTANCE = new IDEEditorConfigManager();

	private final EditorConfigCache caches;

	public IDEEditorConfigManager() {
		super(ECLIPSE_RESOURCE_PROVIDER);
		this.caches = new EditorConfigCache();
	}

	public static IDEEditorConfigManager getInstance() {
		return INSTANCE;
	}

	public void init() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(caches);
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(caches);
		caches.clear();
	}

	@Override
	protected EditorConfig getEditorConfig(IResource configFile) throws IOException {
		EditorConfig config = caches.get(configFile);
		if (config == null) {
			config = super.getEditorConfig(configFile);
			caches.put(configFile, config);
		}
		return config;
	}
}
