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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ec4e.internal.EditorConfigMessages;
import org.eclipse.ec4e.services.EditorConfigConstants;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ContainerSelectionDialog;

/**
 * Wizard page to fill information of .editorconfig file to create.
 *
 */
public class NewEditorConfigFileWizardPage extends WizardPage {

	private static final String PAGE_NAME = NewEditorConfigFileWizardPage.class.getName();

	private static final IPath EDITOTR_CONFIG_PATH = new Path(EditorConfigConstants.EDITORCONFIG);

	private Text folderText;

	private ISelection selection;

	/**
	 * Constructor for NewEditorConfigFileWizardPage.
	 * 
	 * @param pageName
	 */
	public NewEditorConfigFileWizardPage(ISelection selection) {
		super(PAGE_NAME);
		setTitle(EditorConfigMessages.NewEditorConfigFileWizardPage_title);
		setDescription(EditorConfigMessages.NewEditorConfigFileWizardPage_description);
		this.selection = selection;
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label label = new Label(container, SWT.NULL);
		label.setText(EditorConfigMessages.NewEditorConfigFileWizardPage_folderText_Label);

		folderText = new Text(container, SWT.BORDER | SWT.SINGLE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		folderText.setLayoutData(gd);
		folderText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				dialogChanged();
			}
		});

		Button button = new Button(container, SWT.PUSH);
		button.setText(EditorConfigMessages.Browse_button);
		button.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		initialize();
		dialogChanged();
		setControl(container);
	}

	/**
	 * Tests if the current workbench selection is a suitable container to use.
	 */
	private void initialize() {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			IStructuredSelection ssel = (IStructuredSelection) selection;
			if (ssel.size() > 1)
				return;
			Object obj = ssel.getFirstElement();
			if (obj instanceof IResource) {
				IContainer container;
				if (obj instanceof IContainer)
					container = (IContainer) obj;
				else
					container = ((IResource) obj).getParent();
				folderText.setText(container.getFullPath().toString());
			}
		}
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for the
	 * container field.
	 */
	private void handleBrowse() {
		ContainerSelectionDialog dialog = new ContainerSelectionDialog(getShell(),
				ResourcesPlugin.getWorkspace().getRoot(), false,
				EditorConfigMessages.NewEditorConfigFileWizardPage_containerSelectionDialog_title);
		if (dialog.open() == ContainerSelectionDialog.OK) {
			Object[] result = dialog.getResult();
			if (result.length == 1) {
				folderText.setText(((Path) result[0]).toString());
			}
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */
	private void dialogChanged() {
		IResource container = ResourcesPlugin.getWorkspace().getRoot().findMember(new Path(getContainerName()));
		if (getContainerName().length() == 0) {
			updateStatus(EditorConfigMessages.NewEditorConfigFileWizardPage_folder_required_error);
			return;
		}
		if (container == null || (container.getType() & (IResource.PROJECT | IResource.FOLDER)) == 0) {
			updateStatus(EditorConfigMessages.NewEditorConfigFileWizardPage_folder_noexists_error);
			return;
		}
		if (!container.isAccessible()) {
			updateStatus(EditorConfigMessages.NewEditorConfigFileWizardPage_project_noaccessible_error);
			return;
		}
		if (((IContainer) container).exists(EDITOTR_CONFIG_PATH)) {
			updateStatus(EditorConfigMessages.NewEditorConfigFileWizardPage_folder_already_editorconfig_error);
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getContainerName() {
		return folderText.getText();
	}

}