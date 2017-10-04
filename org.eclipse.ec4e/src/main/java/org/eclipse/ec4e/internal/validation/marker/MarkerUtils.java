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
import org.eclipse.ec4e.services.model.optiontypes.OptionType;
import org.eclipse.ec4e.services.validation.Severity;

/**
 * EditorConfig marker utilities.
 * 
 * @author azerr
 *
 */
public class MarkerUtils {

	private static final String EC_ATTRIBUTE_OPTION_TYPE = "ecOptionType"; //$NON-NLS-1$
	private static final String EC_PROBLEM_MARKER_TYPE = "org.eclipse.ec4e.problem"; //$NON-NLS-1$

	public static OptionType<?> getOptionType(IMarker marker) throws CoreException {
		return (OptionType<?>) marker.getAttribute(EC_ATTRIBUTE_OPTION_TYPE);
	}

	public static boolean isOptionType(IMarker marker, String name) throws CoreException {
		OptionType<?> type = getOptionType(marker);
		return type != null && type.getName().equals(name);
	}

	public static void setOptionType(IMarker marker, OptionType<?> type) throws CoreException {
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
}
