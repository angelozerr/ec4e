package org.eclipse.ec4e.codelens;

import org.eclipse.core.resources.IFile;
import org.eclipse.ec4e.search.EditorConfigSearchQuery;
import org.eclipse.jface.text.provisional.codelens.CodeLens;
import org.eclipse.search.ui.NewSearchUI;

public class EditorConfigCodeLens extends CodeLens {

	private SectionWithLoc section;
	private final IFile configFile;

	public EditorConfigCodeLens(SectionWithLoc section, IFile configFile) {
		super(section.getStart().line);
		this.section = section;
		this.configFile = configFile;
	}

	@Override
	public void open() {
		// Execute Search
		EditorConfigSearchQuery query = new EditorConfigSearchQuery(section, configFile);
		NewSearchUI.runQueryInBackground(query);
	}

	public SectionWithLoc getSection() {
		return section;
	}

	public void update(String text) {
		org.eclipse.jface.text.provisional.codelens.Command c = new org.eclipse.jface.text.provisional.codelens.Command(
				text, "");
		super.setCommand(c);
	}

}
