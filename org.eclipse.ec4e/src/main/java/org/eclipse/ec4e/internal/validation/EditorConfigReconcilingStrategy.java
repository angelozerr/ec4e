package org.eclipse.ec4e.internal.validation;

import static org.eclipse.ec4e.internal.EditorConfigPreferenceStore.EDITOR_INSERT_FINAL_NEWLINE;
import static org.eclipse.ec4e.internal.EditorConfigPreferenceStore.EDITOR_TRIM_TRAILING_WHITESPACE;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.reconciler.DirtyRegion;
import org.eclipse.jface.text.reconciler.IReconcilingStrategy;
import org.eclipse.jface.text.reconciler.IReconcilingStrategyExtension;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

public class EditorConfigReconcilingStrategy
		implements IReconcilingStrategy, IReconcilingStrategyExtension, IPropertyChangeListener {

	private final IPreferenceStore preferenceStore;
	private boolean trimTrailingWhiteSpace;
	private boolean insertFinalNewline;

	public EditorConfigReconcilingStrategy(IPreferenceStore preferenceStore) {
		this.preferenceStore = preferenceStore;
	}

	@Override
	public void setProgressMonitor(IProgressMonitor monitor) {

	}

	@Override
	public void initialReconcile() {

	}

	@Override
	public void setDocument(IDocument document) {

	}

	@Override
	public void reconcile(DirtyRegion dirtyRegion, IRegion subRegion) {

	}

	@Override
	public void reconcile(IRegion partition) {
		System.err.println("trimTrailingWhiteSpace=" + trimTrailingWhiteSpace);
		System.err.println("insertFinalNewline=" + insertFinalNewline);
	}

	public void install(ITextViewer textViewer) {
		preferenceStore.addPropertyChangeListener(this);
	}

	public void uninstall() {
		preferenceStore.removePropertyChangeListener(this);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (EDITOR_TRIM_TRAILING_WHITESPACE.equals(event.getProperty())) {
			this.trimTrailingWhiteSpace = preferenceStore.getBoolean(EDITOR_TRIM_TRAILING_WHITESPACE);
		} else if (EDITOR_INSERT_FINAL_NEWLINE.equals(event.getProperty())) {
			this.insertFinalNewline = preferenceStore.getBoolean(EDITOR_INSERT_FINAL_NEWLINE);
		}
	}

}
