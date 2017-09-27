package org.eclipse.ec4e.internal.validation.marker;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ec4e.internal.validation.EditorConfigReconcilingStrategy;
import org.eclipse.ec4e.services.model.options.ConfigPropertyType;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IMarkerResolutionGenerator2;

public class EditorConfigMarkerResolution implements IMarkerResolutionGenerator2 {

	@Override
	public IMarkerResolution[] getResolutions(IMarker marker) {
		List<IMarkerResolution> res = new ArrayList<>(1);
		try {
			ConfigPropertyType<?> type = (ConfigPropertyType<?>) marker
					.getAttribute(EditorConfigReconcilingStrategy.EC_OPTION_TYPE);
			if (type == ConfigPropertyType.INSERT_FINAL_NEWLINE) {
				res.add(InsertFinalNewLineMarkerResolution.INSTANCE);
			} else if (type == ConfigPropertyType.TRIM_TRAILING_WHITESPACE) {
				res.add(TrimTrailingWhitespaceMarkerResolution.INSTANCE);
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return res.toArray(new IMarkerResolution[res.size()]);
	}

	@Override
	public boolean hasResolutions(IMarker marker) {
		try {
			return EditorConfigReconcilingStrategy.PROBLEM_MARKER_TYPE.equals(marker.getType());
		} catch (CoreException e) {
			return false;
		}
	}

}
