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
package org.eclipse.ec4e.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Helper class to get NLSed messages.
 *
 */
public class EditorConfigMessages extends NLS {

	private static final String BUNDLE_NAME = "org.eclipse.ec4e.internal.EditorConfigMessages"; //$NON-NLS-1$

	// Buttons
	public static String Browse_button;

	// Wizards
	public static String NewEditorConfigWizard_windowTitle;
	public static String NewEditorConfigFileWizardPage_title;
	public static String NewEditorConfigFileWizardPage_description;
	public static String NewEditorConfigFileWizardPage_folderText_Label;
	public static String NewEditorConfigFileWizardPage_containerSelectionDialog_title;
	public static String NewEditorConfigFileWizardPage_folder_required_error;
	public static String NewEditorConfigFileWizardPage_folder_noexists_error;
	public static String NewEditorConfigFileWizardPage_project_noaccessible_error;
	public static String NewEditorConfigFileWizardPage_folder_already_editorconfig_error;

	// Search
	public static String editorconfigSearchQuery;

	static {
		NLS.initializeMessages(BUNDLE_NAME, EditorConfigMessages.class);
	}

}
