package org.eclipse.ec4e.internal.codemining;

import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.ec4j.core.model.Section;
import org.ec4j.core.parser.Location;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ec4e.search.CountSectionPatternVisitor;
import org.eclipse.ec4e.search.EditorConfigSearchQuery;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.codemining.ICodeMiningProvider;
import org.eclipse.jface.text.codemining.LineContentCodeMining;
import org.eclipse.jface.text.codemining.LineHeaderCodeMining;
import org.eclipse.jface.text.source.inlined.LineHeaderAnnotation;
import org.eclipse.search.ui.NewSearchUI;
import org.eclipse.swt.events.MouseEvent;

public class EditorConfigCodeLens extends LineHeaderCodeMining {

	private Section section;
	private final IFile configFile;

	public EditorConfigCodeLens(Section section, Location sectionStart, IFile configFile,
			ICodeMiningProvider provider) throws BadLocationException {
		super(new Position(sectionStart.getOffset(), 1), provider, null);
		this.section = section;
		this.configFile = configFile;
	}

	@Override
	public Consumer<MouseEvent> getAction() {
		return (e) -> {
			// Execute Search
			EditorConfigSearchQuery query = new EditorConfigSearchQuery(section, configFile);
			NewSearchUI.runQueryInBackground(query);
		};
	}

	public Section getSection() {
		return section;
	}

	@Override
	protected CompletableFuture<Void> doResolve(ITextViewer viewer, IProgressMonitor monitor) {
		return CompletableFuture.runAsync(() -> {
			CountSectionPatternVisitor visitor = new CountSectionPatternVisitor(this.getSection());
			try {
				configFile.getParent().accept(visitor, IResource.NONE);
				super.setLabel(visitor.getNbFiles() + " files match");
			} catch (CoreException e) {
				super.setLabel(e.getMessage());
			}
		});
	}
}
