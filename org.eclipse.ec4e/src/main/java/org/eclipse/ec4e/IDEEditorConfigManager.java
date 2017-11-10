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
package org.eclipse.ec4e;

import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ec4e.internal.resource.FileResource;
import org.eclipse.ec4j.core.Cache;
import org.eclipse.ec4j.core.EditorConfigConstants;
import org.eclipse.ec4j.core.EditorConfigLoader;
import org.eclipse.ec4j.core.ResourcePropertiesService;
import org.eclipse.ec4j.core.PropertyTypeRegistry;
import org.eclipse.ec4j.core.Resource;
import org.eclipse.ec4j.core.ResourceProperties;
import org.eclipse.ec4j.core.model.EditorConfig;
import org.eclipse.ec4j.core.model.Property;
import org.eclipse.ec4j.core.model.Version;

/**
 * IDE editorconfig manager.
 *
 */
public class IDEEditorConfigManager {

	/**
	 * An unchecked wrapper around {@link EditorConfigException} to be able to trow
	 * properly from lambda expressions.
	 */
	private static class WrappedEditorConfigException extends RuntimeException {
		private static final long serialVersionUID = 1L;
		private final IOException cause;

		WrappedEditorConfigException(IOException cause) {
			this.cause = cause;
		}
	}

	/**
	 * {@link EditorConfig} instance cache. Uses {@link ConcurrentHashMap}
	 * internally and can thus be accessed from concurrent threads.
	 */
	private static class EditorConfigCache implements IResourceChangeListener, IResourceDeltaVisitor, Cache {

		private static final long serialVersionUID = 1L;

		private final ConcurrentHashMap<Resource, EditorConfig> entries = new ConcurrentHashMap<>();

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
					entries.remove(new FileResource(file));
				}
			}
			return false;
		}

		@Override
		public EditorConfig get(Resource editorConfigFile, EditorConfigLoader loader) throws IOException {
			try {
				return entries.computeIfAbsent(editorConfigFile, k -> {
					try {
						return loader.load(k);
					} catch (IOException e) {
						throw new WrappedEditorConfigException(e);
					}
				});
			} catch (WrappedEditorConfigException e) {
				throw e.cause;
			}
		}

		public void clear() {
			entries.clear();
		}
	}

	public static final IDEEditorConfigManager INSTANCE = new IDEEditorConfigManager();

	private final ResourcePropertiesService session;

	private final EditorConfigCache cache;

	private final PropertyTypeRegistry registry;

	private final EditorConfigLoader loader;

	private final Version version;

	public IDEEditorConfigManager() {
		this.cache = new EditorConfigCache();
		this.registry = PropertyTypeRegistry.getDefault();
		this.version = Version.CURRENT;
		this.loader = EditorConfigLoader.of(version, registry);

		session = ResourcePropertiesService.builder()//
				.cache(cache) //
				.loader(loader) //
				.build();
	}

	public static IDEEditorConfigManager getInstance() {
		return INSTANCE;
	}

	public void init() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(cache);
	}

	public ResourcePropertiesService getSession() {
		return session;
	}

	public PropertyTypeRegistry getRegistry() {
		return registry;
	}

	public EditorConfigLoader getLoader() {
		return loader;
	}

	public Version getVersion() {
		return version;
	}

	public void dispose() {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(cache);
		cache.clear();
	}

	public ResourceProperties queryOptions(IFile file) throws IOException  {
		return session.queryProperties(new FileResource(file));
	}

}
