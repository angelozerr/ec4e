package org.eclipse.ec4e.search;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.resources.IResourceProxyVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ec4j.model.Section;

public abstract class AbstractSectionPatternVisitor implements IResourceProxyVisitor {

	private final Section section;

	public AbstractSectionPatternVisitor(Section section) {
		this.section = section;
	}

	@Override
	public boolean visit(IResourceProxy proxy) throws CoreException {
		IPath path = proxy.requestFullPath();
		if (proxy.getType() == IResource.FILE && section.match(path.toString())) {
			collect(proxy);
		}
		return true;
	}

	protected abstract void collect(IResourceProxy proxy);

}
