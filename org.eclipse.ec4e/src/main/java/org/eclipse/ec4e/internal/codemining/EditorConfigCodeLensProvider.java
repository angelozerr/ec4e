package org.eclipse.ec4e.internal.codemining;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.ec4j.core.Resource.Resources;
import org.ec4j.core.model.Section;
import org.ec4j.core.parser.EditorConfigParser;
import org.ec4j.core.parser.ErrorHandler;
import org.ec4j.core.parser.Location;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ec4e.IDEEditorConfigManager;
import org.eclipse.ec4e.utils.EditorUtils;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.codemining.AbstractCodeMiningProvider;
import org.eclipse.jface.text.codemining.ICodeMining;
import org.eclipse.ui.texteditor.ITextEditor;

public class EditorConfigCodeLensProvider extends AbstractCodeMiningProvider {

	@Override
	public CompletableFuture<List<? extends ICodeMining>> provideCodeMinings(ITextViewer viewer,
			IProgressMonitor monitor) {
		return CompletableFuture.supplyAsync(() -> {
			monitor.isCanceled();
			ITextEditor textEditor = super.getAdapter(ITextEditor.class);
			IFile file = EditorUtils.getFile(textEditor);
			if (file == null) {
				return Collections.emptyList();
			}

			IDocument document = viewer.getDocument();
			IDEEditorConfigManager editorConfigManager = IDEEditorConfigManager.getInstance();
			final ErrorHandler errorHandler = ErrorHandler.IGNORING;
			SectionsHandler handler = new SectionsHandler(editorConfigManager.getRegistry(),
					editorConfigManager.getVersion());
			EditorConfigParser parser = EditorConfigParser.default_();
			try {
				parser.parse(Resources.ofString(file.getFullPath().toString(), document.get()), handler, errorHandler);
			} catch (IOException e) {
				/* Will not happen with Resources.ofString() */
				throw new RuntimeException(e);
			}
			List<Section> sections = handler.getEditorConfig().getSections();
			List<Location> sectionLocations = handler.getSectionLocations();

			List<EditorConfigCodeLens> lenses = new ArrayList<>(sections.size());

			for (int i = 0; i < sections.size(); i++) {
				try {
					lenses.add(new EditorConfigCodeLens(sections.get(i), sectionLocations.get(i), file, this));
				} catch (BadLocationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			return lenses;

		});
	}

}
