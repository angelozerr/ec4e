package org.eclipse.ec4e.internal.resource;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ec4j.core.ResourcePaths.ResourcePath;
import org.eclipse.ec4j.core.Resources.RandomReader;
import org.eclipse.ec4j.core.Resources.Resource;

/**
 * A {@link Resource} implementation that uses an underlying {@link IFile}.
 *
 * @author <a href="https://github.com/ppalaga">Peter Palaga</a>
 */
public class FileResource implements Resource {
	private final IFile file;

	public FileResource(IFile file) {
		super();
		this.file = file;
	}

	@Override
	public boolean exists() {
		return file.exists();
	}

	@Override
	public ResourcePath getParent() {
		IContainer parent = file.getParent();
		return parent == null ? null : new ContainerResourcePath(parent);
	}

	@Override
	public String getPath() {
		return file.getLocation().toString().replaceAll("[\\\\]", "/");
	}

	@Override
	public RandomReader openRandomReader() throws IOException {
		try (Reader reader = openReader()) {
			return org.eclipse.ec4j.core.Resources.StringRandomReader.ofReader(reader);
		}
	}

	@Override
	public Reader openReader() throws IOException {
		try {
			return new InputStreamReader(file.getContents(), Charset.forName(file.getCharset()));
		} catch (CoreException e) {
			throw new IOException(e);
		}
	}

}