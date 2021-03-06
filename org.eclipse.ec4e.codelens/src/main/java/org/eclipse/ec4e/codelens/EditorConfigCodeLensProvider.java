package org.eclipse.ec4e.codelens;

import java.io.IOException;
import java.util.List;

import org.ec4j.core.EditorConfigLoader;
import org.ec4j.core.Resource.Resources;
import org.ec4j.core.model.Section;
import org.ec4j.core.parser.EditorConfigParser;
import org.ec4j.core.parser.ErrorHandler;
import org.ec4j.core.parser.Location;
import org.eclipse.codelens.editors.IEditorCodeLensContext;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ec4e.IDEEditorConfigManager;
import org.eclipse.ec4e.search.CountSectionPatternVisitor;
import org.eclipse.ec4e.utils.EditorUtils;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.provisional.codelens.AbstractSyncCodeLensProvider;
import org.eclipse.jface.text.provisional.codelens.ICodeLens;
import org.eclipse.jface.text.provisional.codelens.ICodeLensContext;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorConfigCodeLensProvider extends AbstractSyncCodeLensProvider {

	@Override
	protected ICodeLens[] provideSyncCodeLenses(ICodeLensContext context, IProgressMonitor monitor) {
		ITextEditor textEditor = ((IEditorCodeLensContext) context).getTextEditor();
		IFile file = EditorUtils.getFile(textEditor);
		if (file == null) {
			return null;
		}
		IDocument document = context.getViewer().getDocument();
		IDEEditorConfigManager editorConfigManager = IDEEditorConfigManager.getInstance();
		final ErrorHandler errorHandler = ErrorHandler.IGNORING;
		SectionsHandler handler = new SectionsHandler(editorConfigManager.getRegistry(), editorConfigManager.getVersion());
		EditorConfigParser parser = EditorConfigParser.default_();
		try {
			parser.parse(Resources.ofString(file.getFullPath().toString(), document.get()), handler, errorHandler );
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
		IFile file = EditorUtils.getFile(textEditor);
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
