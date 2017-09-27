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
package org.eclipse.ec4e.internal.wizards;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.IScopeContext;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ec4e.EditorConfigPlugin;
import org.eclipse.ec4e.internal.EditorConfigMessages;
import org.eclipse.ec4e.services.EditorConfigConstants;
import org.eclipse.ec4e.services.EditorConfigService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.editors.text.EditorsUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.AbstractDecoratedTextEditorPreferenceConstants;

/**
 * Wizard to create an .editorconfig file by using Eclipse preferences.
 */

public class NewEditorConfigWizard extends Wizard implements INewWizard {

	private NewEditorConfigFileWizardPage page;
	private ISelection selection;

	@Override
	public void addPages() {
		page = new NewEditorConfigFileWizardPage(selection);
		addPage(page);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection currentSelection) {
		setWindowTitle(EditorConfigMessages.NewEditorConfigWizard_windowTitle);
		setNeedsProgressMonitor(true);
		this.selection = currentSelection;
	}

	@Override
	public boolean performFinish() {
		final String containerName = page.getContainerName();
		final String fileName = EditorConfigConstants.EDITORCONFIG;
		try {
			getContainer().run(true, false, (monitor) -> {
				try {
					doFinish(containerName, fileName, monitor);
				} catch (CoreException e) {
					throw new InvocationTargetException(e);
				} finally {
					monitor.done();
				}
			});
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}
		return true;
	}

	/**
	 * The worker method. It will find the container, create the file if missing or
	 * just replace its contents, and open the editor on the newly created file.
	 */

	private void doFinish(String containerName, String fileName, IProgressMonitor monitor) throws CoreException {
		// create a sample file
		monitor.beginTask("Creating " + fileName, 2);
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IResource resource = root.findMember(new Path(containerName));
		if (!resource.exists() || !(resource instanceof IContainer)) {
			throwCoreException("Container \"" + containerName + "\" does not exist.");
		}
		IContainer container = (IContainer) resource;
		final IFile file = container.getFile(new Path(fileName));
		try (InputStream stream = openContentStream(container);) {
			file.create(stream, false, monitor);
		} catch (IOException e) {
		}
		monitor.worked(1);
		monitor.setTaskName("Opening file for editing...");
		getShell().getDisplay().asyncExec(() -> {
			IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
			try {
				IDE.openEditor(page, file, true);
			} catch (PartInitException e) {
			}
		});
		monitor.worked(1);
	}

	/**
	 * Returns the content of the .editorconfig file to generate.
	 * 
	 * @param container
	 * 
	 * @return the content of the .editorconfig file to generate.
	 */
	private InputStream openContentStream(IContainer container) {
		IPreferenceStore store = EditorsUI.getPreferenceStore();
		boolean spacesForTabs = store.getBoolean(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_SPACES_FOR_TABS);
		int tabWidth = store.getInt(AbstractDecoratedTextEditorPreferenceConstants.EDITOR_TAB_WIDTH);
		String lineDelimiter = getLineDelimiter(container);
		String endOfLine = EditorConfigService.getEndOfLine(lineDelimiter);

		StringBuilder content = new StringBuilder("# EditorConfig is awesome: http://EditorConfig.org");
		content.append(lineDelimiter);
		content.append(lineDelimiter);
		content.append("[*]");
		content.append(lineDelimiter);
		content.append("indent_style = ");
		content.append(spacesForTabs ? "space" : "tab");
		content.append(lineDelimiter);
		content.append("indent_size = ");
		content.append(tabWidth);
		if (endOfLine != null) {
			content.append(lineDelimiter);
			content.append("end_of_line = ");
			content.append(endOfLine);
		}

		return new ByteArrayInputStream(content.toString().getBytes());
	}

	private void throwCoreException(String message) throws CoreException {
		IStatus status = new Status(IStatus.ERROR, EditorConfigPlugin.PLUGIN_ID, IStatus.OK, message, null);
		throw new CoreException(status);
	}

	private static String getLineDelimiter(IContainer file) {
		String lineDelimiter = getLineDelimiterPreference(file);
		if (lineDelimiter == null) {
			lineDelimiter = System.getProperty("line.separator");
		}
		if (lineDelimiter != null) {
			return lineDelimiter;
		}
		return "\n";
	}

	private static String getLineDelimiterPreference(IContainer file) {
		IScopeContext[] scopeContext;
		if (file != null && file.getProject() != null) {
			// project preference
			scopeContext = new IScopeContext[] { new ProjectScope(file.getProject()) };
			String lineDelimiter = Platform.getPreferencesService().getString(Platform.PI_RUNTIME,
					Platform.PREF_LINE_SEPARATOR, null, scopeContext);
			if (lineDelimiter != null)
				return lineDelimiter;
		}
		// workspace preference
		scopeContext = new IScopeContext[] { InstanceScope.INSTANCE };
		return Platform.getPreferencesService().getString(Platform.PI_RUNTIME, Platform.PREF_LINE_SEPARATOR, null,
				scopeContext);
	}

}