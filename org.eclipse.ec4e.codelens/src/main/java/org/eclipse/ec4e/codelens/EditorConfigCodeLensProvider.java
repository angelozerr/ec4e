package org.eclipse.ec4e.codelens;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.List;

import org.eclipse.codelens.editors.IEditorCodeLensContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ec4e.IDEEditorConfigManager;
import org.eclipse.ec4e.search.CountSectionPatternVisitor;
import org.eclipse.ec4j.core.EditorConfigLoader;
import org.eclipse.ec4j.core.Resources;
import org.eclipse.ec4j.core.model.Section;
import org.eclipse.ec4j.core.parser.EditorConfigParser;
import org.eclipse.ec4j.core.parser.Location;
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
		EditorConfigLoader loader = IDEEditorConfigManager.getInstance().getSession().getLoader();
		SectionsHandler handler = new SectionsHandler(loader.getRegistry(), loader.getVersion());
		EditorConfigParser parser = EditorConfigParser.builder().tolerant().build();
		try {
			parser.parse(Resources.ofString(file.getFullPath().toString(), document.get()), handler);
		} catch (IOException e) {
			/* Will not happen with Resources.ofString() */
			throw new RuntimeException(e);
		}
		List<Section> sections = handler.getEditorConfig().getSections();
		List<Location> sectionLocations = handler.getSectionLocations();

		ICodeLens[] lenses = new ICodeLens[sections.size()];
		for (int i = 0; i < lenses.length; i++) {
			lenses[i] = new EditorConfigCodeLens(sections.get(i), sectionLocations.get(i), file);
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
