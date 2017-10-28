package org.eclipse.ec4e.internal.resource;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.ec4j.core.ResourcePaths.ResourcePath;
import org.eclipse.ec4j.core.Resources.Resource;

/**
 * A {@link ResourcePath} implementation based on {@link IContainer}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class ContainerResourcePath implements ResourcePath {

	private final IContainer container;

	ContainerResourcePath(IContainer container) {
		this.container = container;
	}

	@Override
	public ResourcePath getParent() {
		IContainer parent = container.getParent();
		if (parent == null || parent.getType() == IResource.ROOT) {
			return null;
		}
		// Search '.editorconfig' files only in project and folders container.
		return new ContainerResourcePath(parent);
	}

	@Override
	public String getPath() {
		return container.getLocation().toString().replaceAll("[\\\\]", "/");
	}

	@Override
	public boolean hasParent() {
		IContainer parent = container.getParent();
		return parent != null;
	}

	@Override
	public Resource resolve(String name) {
		IFile child = container.getFile(new Path(name));
		return new FileResource(child);
	}

}