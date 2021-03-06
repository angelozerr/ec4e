package org.eclipse.ec4e.internal.resource;

import org.ec4j.core.Resource;
import org.ec4j.core.ResourcePath;
import org.ec4j.core.model.Ec4jPath;
import org.ec4j.core.model.Ec4jPath.Ec4jPaths;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.Path;

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
	public Ec4jPath getPath() {
		return Ec4jPaths.of(container.getLocation().toString().replaceAll("[\\\\]", "/"));
	}

	@Override
	public boolean hasParent() {
		IContainer parent = container.getParent();
		return parent != null;
	}

	/** {@inheritDoc} */
	@Override
	public Resource relativize(Resource resource) {
		if (resource instanceof FileResource) {
			final FileResource fileResource = (FileResource) resource;
			final IFile relativeFile = container.getFile(fileResource.file.getFullPath().makeRelativeTo(container.getFullPath()));
			return new FileResource(relativeFile);
		} else {
			throw new IllegalArgumentException(this.getClass().getName()
					+ ".relativize(Resource resource) can handle only instances of " + FileResource.class.getName());
		}
	}

	@Override
	public Resource resolve(String name) {
		IFile child = container.getFile(new Path(name));
		return new FileResource(child);
	}

}