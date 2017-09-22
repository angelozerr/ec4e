package org.eclipse.ec4e.internal.hover;

import org.eclipse.ec4e.internal.DocumentCharProvider;
import org.eclipse.ec4e.services.EditorConfigService;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.Region;

public class EditorConfigTextHover implements ITextHover {

	@Override
	public String getHoverInfo(ITextViewer textViewer, IRegion hoverRegion) {
		try {
			return EditorConfigService.getHover(hoverRegion.getOffset(), textViewer.getDocument(),
					DocumentCharProvider.INSTANCE);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public IRegion getHoverRegion(ITextViewer textViewer, int offset) {
		return new Region(offset, 1);
	}

}
