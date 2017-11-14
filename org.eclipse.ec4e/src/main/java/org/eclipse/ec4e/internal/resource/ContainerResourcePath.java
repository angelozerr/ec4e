package org.eclipse.ec4e.internal.resource;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;
import org.eclipse.ec4j.core.Resource;
import org.eclipse.ec4j.core.ResourcePath;

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
		if (container.getType() == IResource.PROJECT) {
			// Stop the search of  '.editorconfig' files in the parent container
			return null;
		}
		IContainer parent = container.getParent();
		return parent == null ? null : new ContainerResourcePath(parent);
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