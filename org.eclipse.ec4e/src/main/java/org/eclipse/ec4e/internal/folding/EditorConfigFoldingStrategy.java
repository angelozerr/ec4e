/**
 *  Copyright (c) 2017 Angelo ZERR.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *  Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 */
package org.eclipse.ec4e.internal.folding;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.ec4j.core.EditorConfigConstants;
import org.eclipse.ec4j.core.PropertyTypeRegistry;
import org.eclipse.ec4j.core.Resources;
import org.eclipse.ec4j.core.model.Comments.CommentBlock;
import org.eclipse.ec4j.core.model.Comments.CommentBlocks;
import org.eclipse.ec4j.core.model.EditorConfig;
import org.eclipse.ec4j.core.model.Section;
import org.eclipse.ec4j.core.model.Version;
import org.eclipse.ec4j.core.parser.EditorConfigModelHandler;
import org.eclipse.ec4j.core.parser.EditorConfigParser;
import org.eclipse.ec4j.core.parser.ErrorHandler;
import org.eclipse.ec4j.core.parser.LocationAwareModelHandler;
import org.eclipse.ec4j.core.parser.Span;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Position;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconciler;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.projection.IProjectionListener;
import org.eclipse.jface.text.source.projection.ProjectionAnnotation;
import org.eclipse.jface.text.source.projection.ProjectionAnnotationModel;
import org.eclipse.jface.text.source.projection.ProjectionViewer;

/**
 * EditorConfig Folding to folds:
 * <ul>
 * <li>section</li>
 * </ul>
 *
 */
public class EditorConfigFoldingStrategy
		implements IReconcilingStrategy, IReconcilingStrategyExtension, IReconciler, IProjectionListener {

	private Annotation[] oldAnnotations;
	private ProjectionViewer viewer;
	private ProjectionAnnotationModel projectionAnnotationModel;
	private IDocument document;

	@Override
	public void install(ITextViewer textViewer) {
		if (!(textViewer instanceof ProjectionViewer)) {
			return;
		}
		if (viewer != null) {
			viewer.removeProjectionListener(this);
		}
		viewer = (ProjectionViewer) textViewer;
		viewer.addProjectionListener(this);
		this.projectionAnnotationModel = viewer.getProjectionAnnotationModel();
	}

	@Override
	public void uninstall() {
		setDocument(null);

		if (viewer != null) {
			viewer.removeProjectionListener(this);
			viewer = null;
		}

		projectionDisabled();
	}

	@Override
	public IReconcilingStrategy getReconcilingStrategy(String contentType) {
		return null;
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {

	}

	@Override
	public void initialReconcile() {
		updateFolding();
	}

	@Override
	public void setDocument(IDocument document) {
		this.document = document;
	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {
		updateFolding();
	}

	private void updateFolding() {
		EditorConfig editorConfig = parse(document);
		updateFolding(editorConfig);
	}

	private void updateFolding(EditorConfig editorConfig) {
		if (projectionAnnotationModel == null) {
			return;
		}
		List<Section> sections = editorConfig.getSections();
		CommentBlocks commentBlocks = editorConfig.getAdapter(CommentBlocks.class);
		List<CommentBlock> comments = commentBlocks != null ? commentBlocks.getCommentBlocks()
				: Collections.emptyList();
		Map<Annotation, Position> newAnnotations = new HashMap<>();
		// Collection section and comment spans;
		List<Span> spans = /*Stream.concat(sections.stream(), comments.stream())*/
				sections.stream()
				.map(a -> a.getAdapter(Span.class))
				.sorted((s1, s2) -> s1.getStart().line - s2.getStart().line).collect(Collectors.toList());
		Annotation[] annotations = new Annotation[spans.size()];
		for (int i = 0; i < spans.size(); i++) {
			Span span = spans.get(i);
			int startOffset = span.getStart().offset;
			int endOffset = span.getEnd().offset;
			ProjectionAnnotation annotation = new ProjectionAnnotation();
			newAnnotations.put(annotation, new Position(startOffset, endOffset - startOffset));
			annotations[i] = annotation;
		}
		projectionAnnotationModel.modifyAnnotations(oldAnnotations, newAnnotations, null);
		oldAnnotations = annotations;
	}

	@Override
	public void reconcile(IRegion partition) {
	}

	private EditorConfig parse(IDocument document) {
		EditorConfigModelHandler handler = new LocationAwareModelHandler(PropertyTypeRegistry.getDefault(),
				Version.CURRENT);
		EditorConfigParser parser = EditorConfigParser.builder().build();
		try {
			parser.parse(Resources.ofString(EditorConfigConstants.EDITORCONFIG, document.get()), handler,
					ErrorHandler.THROWING);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return handler.getEditorConfig();
	}

	@Override
	public void projectionEnabled() {
		if (viewer != null) {
			projectionAnnotationModel = viewer.getProjectionAnnotationModel();
		}
	}

	@Override
	public void projectionDisabled() {
		projectionAnnotationModel = null;
	}

}
