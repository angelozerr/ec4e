package org.eclipse.ec4e.search;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ec4e.EditorConfigPlugin;
import org.eclipse.ec4e.internal.EditorConfigMessages;
import org.eclipse.ec4j.model.Section;
import org.eclipse.search.internal.ui.text.FileSearchQuery;
import org.eclipse.search.ui.text.AbstractTextSearchResult;

public class EditorConfigSearchQuery extends FileSearchQuery {

	private final Section section;
	private final IFile configFile;

	public EditorConfigSearchQuery(Section section, IFile configFile) {
		super(EditorConfigMessages.editorconfigSearchQuery, false, false, null);
		this.section = section;
		this.configFile = configFile;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		AbstractTextSearchResult textResult = (AbstractTextSearchResult) getSearchResult();
		textResult.removeAll();
		try {
			CountSectionPatternVisitor visitor = new CountSectionPatternVisitor(section);
			configFile.accept(visitor, IResource.NONE);
			
			// for (Location loc : references.get(4, TimeUnit.SECONDS)) {
			// Match match = toMatch(loc, monitor);
			// addMatch(match);
			// }
			return Status.OK_STATUS;
		} catch (Exception ex) {
			return new Status(IStatus.ERROR, EditorConfigPlugin.getDefault().getBundle().getSymbolicName(),
					ex.getMessage(), ex);
		}
	}

}
