package org.eclipse.ec4e.codelens;

import java.util.List;

import org.eclipse.codelens.editors.IEditorCodeLensContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ec4e.search.CountSectionPatternVisitor;
import org.eclipse.ec4j.model.Section;
import org.eclipse.ec4j.parser.EditorConfigParser;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.provisional.codelens.AbstractSyncCodeLensProvider;
import org.eclipse.jface.text.provisional.codelens.ICodeLens;
import org.eclipse.jface.text.provisional.codelens.ICodeLensContext;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorConfigCodeLensProvider extends AbstractSyncCodeLensProvider {

	@Override
	protected ICodeLens[] provideSyncCodeLenses(ICodeLensContext context, IProgressMonitor monitor) {
		ITextEditor textEditor = ((IEditorCodeLensContext) context).getTextEditor();
		IFile file = EditorConfigCodeLensControllerProvider.getFile(textEditor);
		if (file == null) {
			return null;
		}
		IDocument document = context.getViewer().getDocument();
		SectionsHandler handler = new SectionsHandler(file.getParent().getFullPath().toString() + "/");
		new EditorConfigParser<>(handler).setTolerant(true).parse(document.get());
		List<Section> sections = handler.getEditorConfig().getSections();

		ICodeLens[] lenses = new ICodeLens[sections.size()];
		for (int i = 0; i < lenses.length; i++) {
			lenses[i] = new EditorConfigCodeLens((SectionWithLoc) sections.get(i), file);
		}
		return lenses;
	}

	@Override
	protected ICodeLens resolveSyncCodeLens(ICodeLensContext context, ICodeLens codeLens, IProgressMonitor monitor) {
		ITextEditor textEditor = ((IEditorCodeLensContext) context).getTextEditor();
		IFile file = EditorConfigCodeLensControllerProvider.getFile(textEditor);
		if (file == null) {
			return null;
		}
		EditorConfigCodeLens cl = (EditorConfigCodeLens) codeLens;
		CountSectionPatternVisitor visitor = new CountSectionPatternVisitor(cl.getSection());
		try {
			file.getParent().accept(visitor, IResource.NONE);
			cl.update(visitor.getNbFiles() + " files match");
		} catch (CoreException e) {
			cl.update(e.getMessage());
		}
		return cl;
	}

}
