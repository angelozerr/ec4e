package org.eclipse.ec4e.search;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceProxy;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.ec4e.EditorConfigPlugin;
import org.eclipse.ec4e.internal.EditorConfigMessages;
import org.eclipse.ec4j.core.model.Section;
import org.eclipse.osgi.util.NLS;
import org.eclipse.search.internal.ui.text.FileMatch;
import org.eclipse.search.internal.ui.text.FileSearchQuery;
import org.eclipse.search.ui.ISearchQuery;
import org.eclipse.search.ui.ISearchResult;
import org.eclipse.search.ui.text.AbstractTextSearchResult;
import org.eclipse.search.ui.text.Match;

/**
 * {@link ISearchQuery} implementation for EditorConfig section matching files.
 *
 */
public class EditorConfigSearchQuery extends FileSearchQuery {

	private final Section section;
	private final IFile configFile;

	private EditorConfigSearchResult result;
	private long startTime;

	/**
	 * EditorConfig section matching files query to "Find matching files" from the
	 * given section
	 *
	 * @param section
	 * @param configFile
	 */
	public EditorConfigSearchQuery(Section section, IFile configFile) {
		super("", false, false, null); //$NON-NLS-1$
		this.section = section;
		this.configFile = configFile;
	}

	@Override
	public IStatus run(IProgressMonitor monitor) throws OperationCanceledException {
		startTime = System.currentTimeMillis();
		AbstractTextSearchResult textResult = (AbstractTextSearchResult) getSearchResult();
		textResult.removeAll();

		try {

			IContainer dir = configFile.getParent();
			dir.accept(new AbstractSectionPatternVisitor(section) {

				@Override
				protected void collect(IResourceProxy proxy) {
					Match match = new FileMatch((IFile) proxy.requestResource());
					result.addMatch(match);
				}
			}, IResource.NONE);

			return Status.OK_STATUS;
		} catch (Exception ex) {
			return new Status(IStatus.ERROR, EditorConfigPlugin.PLUGIN_ID, ex.getMessage(), ex);
		}
	}

	@Override
	public ISearchResult getSearchResult() {
		if (result == null) {
			result = new EditorConfigSearchResult(this);
		}
		return result;
	}

	@Override
	public String getLabel() {
		return EditorConfigMessages.EditorConfigSearchQuery_label;
	}

	@Override
	public String getResultLabel(int nMatches) {
		long time = 0;
		if (startTime > 0) {
			time = System.currentTimeMillis() - startTime;
		}
		if (nMatches == 1) {
			return NLS.bind(EditorConfigMessages.EditorConfigSearchQuery_singularReference,
					new Object[] { section.getPattern(), time });
		}
		return NLS.bind(EditorConfigMessages.EditorConfigSearchQuery_pluralReferences,
				new Object[] { section.getPattern(), nMatches, time });
	}

	@Override
	public boolean isFileNameSearch() {
		return true;
	}
}