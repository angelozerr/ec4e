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
package org.eclipse.ec4e.internal.validation.marker;

import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ec4e.EditorConfigPlugin;
import org.eclipse.ec4j.core.model.PropertyType;
import org.eclipse.ec4j.services.validation.Severity;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.RewriteSessionEditProcessor;
import org.eclipse.text.edits.MalformedTreeException;
import org.eclipse.text.edits.TextEdit;
import org.eclipse.text.undo.DocumentUndoManagerRegistry;
import org.eclipse.text.undo.IDocumentUndoManager;

/**
 * EditorConfig marker utilities.
 *
 * @author azerr
 *
 */
public class MarkerUtils {

	private static final String EC_ATTRIBUTE_OPTION_TYPE = "ecOptionType"; //$NON-NLS-1$
	private static final String EC_PROBLEM_MARKER_TYPE = "org.eclipse.ec4e.problem"; //$NON-NLS-1$

	public static PropertyType<?> getOptionType(IMarker marker) throws CoreException {
		return (PropertyType<?>) marker.getAttribute(EC_ATTRIBUTE_OPTION_TYPE);
	}

	public static boolean isOptionType(IMarker marker, String name) throws CoreException {
		PropertyType<?> type = getOptionType(marker);
		return type != null && type.getName().equals(name);
	}

	public static void setOptionType(IMarker marker, PropertyType<?> type) throws CoreException {
		marker.setAttribute(EC_ATTRIBUTE_OPTION_TYPE, type);
	}

	public static boolean isEditorConfigMarker(IMarker marker) {
		try {
			return EC_PROBLEM_MARKER_TYPE.equals(marker.getType());
		} catch (CoreException e) {
			return false;
		}
	}

	public static List<IMarker> findEditorConfigMarkers(IResource resource) throws CoreException {
		return Arrays.asList(resource.findMarkers(EC_PROBLEM_MARKER_TYPE, false, IResource.DEPTH_ONE));
	}

	public static IMarker createEditorConfigMarker(IResource resource) throws CoreException {
		return resource.createMarker(EC_PROBLEM_MARKER_TYPE);
	}

	public static int getSeverity(Severity severity) {
		switch (severity) {
		case info:
			return IMarker.SEVERITY_INFO;
		case warning:
			return IMarker.SEVERITY_WARNING;
		default:
			return IMarker.SEVERITY_ERROR;
		}
	}

	/**
	 * Method will apply all edits to document as single modification. Needs to
	 * be executed in UI thread.
	 *
	 * @param document
	 *            document to modify
	 * @param edits
	 *            list of LSP TextEdits
	 */
	public static void applyEdits(IDocument document, TextEdit edit) {
		if (document == null) {
			return;
		}

		IDocumentUndoManager manager = DocumentUndoManagerRegistry.getDocumentUndoManager(document);
		if (manager != null) {
			manager.beginCompoundChange();
		}
		try {
			RewriteSessionEditProcessor editProcessor = new RewriteSessionEditProcessor(document, edit,
					org.eclipse.text.edits.TextEdit.NONE);
			editProcessor.performEdits();
		} catch (MalformedTreeException | BadLocationException e) {
			EditorConfigPlugin.logError(e);
		}
		if (manager != null) {
			manager.endCompoundChange();
		}
	}
}
