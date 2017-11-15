package org.eclipse.ec4e.internal.outline;

import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.ec4j.core.model.Property;
import org.ec4j.core.model.Section;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.ICommonLabelProvider;

public class EditorConfigLabelProvider extends LabelProvider implements ICommonLabelProvider, IStyledLabelProvider {

	@Override
	public void restoreState(IMemento aMemento) {
		// TODO Auto-generated method stub

	}

	@Override
	public void saveState(IMemento aMemento) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getDescription(Object anElement) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public StyledString getStyledText(Object element) {
		if (element instanceof Section) {
			return new StyledString(((Section) element).getGlob().getSource());
		} else if (element instanceof Property) {
			return new StyledString(((Property) element).toString());
		}
		return null;
	}

	@Override
	public void init(ICommonContentExtensionSite aConfig) {
		// TODO Auto-generated method stub

	}

}
