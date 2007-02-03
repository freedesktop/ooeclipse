/*************************************************************************
 *
 * $RCSfile: PackagePropertiesFormPage.java,v $
 *
 * $Revision: 1.1 $
 *
 * last change: $Author: cedricbosdo $ $Date: 2007/02/03 21:29:51 $
 *
 * The Contents of this file are made available subject to the terms of
 * either of the GNU Lesser General Public License Version 2.1
 *
 * Sun Microsystems Inc., October, 2000
 *
 *
 * GNU Lesser General Public License Version 2.1
 * =============================================
 * Copyright 2000 by Sun Microsystems, Inc.
 * 901 San Antonio Road, Palo Alto, CA 94303, USA
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston,
 * MA 02111-1307 USA
 * 
 * The Initial Developer of the Original Code is: Sun Microsystems, Inc..
 *
 * Copyright: 2002 by Sun Microsystems, Inc.
 *
 * All Rights Reserved.
 *
 * Contributor(s): Cedric Bosdonnat
 *
 *
 ************************************************************************/
package org.openoffice.ide.eclipse.core.editors;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ScrolledForm;
/**
 * Page displaying the Package properties in a more user friendly way
 * 
 * @author cedricbosdo
 *
 */
public class PackagePropertiesFormPage extends FormPage {
	
	private ContentsSection mContents;
	private LibsSection mLibs;
	private PackageDescriptionSection mDescriptions;
	
	public PackagePropertiesFormPage(FormEditor editor, String id) {
		super(editor, id, "Package");
	}

	public PackagePropertiesFormPage(String id) {
		super(id, "Package");
	}

	public IProject getProject() {
		IProject prj = null;
		if (getEditorInput() instanceof IFileEditorInput) {
			prj = ((IFileEditorInput)getEditorInput()).getFile().getProject();
		}
		return prj;
	}
	
	@Override
	protected void createFormContent(IManagedForm managedForm) {
		super.createFormContent(managedForm);
		ScrolledForm form = managedForm.getForm();
		
		form.setText("Package");
		
		// Create the only section with a tree representing the files 
		// and dirs in its client area
		form.getBody().setLayout(new GridLayout(2, true));
		
		mContents = new ContentsSection(this);
		mLibs = new LibsSection(this);
		mDescriptions = new PackageDescriptionSection(this);
		
		// update the model from the source
		PackagePropertiesEditor editor = (PackagePropertiesEditor)getEditor();
		editor.getModel().setQuiet(true);
		
		editor.loadFromSource();

		List<IResource> contents = editor.getModel().getContents();
		mContents.setContents(contents);
		
		// Get the Libs and Descriptions properties from the document
		
		mLibs.setLibraries(editor.getModel());
		
		mDescriptions.setDescriptions(editor.getModel().getDescriptionFiles());
		editor.getModel().setQuiet(false);
	}
}
