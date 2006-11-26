/*************************************************************************
 *
 * $RCSfile: JavaMainProvider.java,v $
 *
 * $Revision: 1.1 $
 *
 * last change: $Author: cedricbosdo $ $Date: 2006/11/26 21:37:03 $
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
package org.openoffice.ide.eclipse.java;

import java.util.Vector;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaCore;
import org.openoffice.ide.eclipse.core.launch.IMainProvider;

/**
 * Class providing the XMain implementations in Java
 * 
 * @author cedricbosdo
 *
 */
public class JavaMainProvider implements IMainProvider {

	/*
	 * (non-Javadoc)
	 * @see org.openoffice.ide.eclipse.core.launch.IMainProvider#getMainNames(org.eclipse.core.resources.IProject)
	 */
	public Vector<String> getMainNames(IProject project) {
		Vector<String> mains = new Vector<String>();
		
		IJavaProject javaPrj = JavaCore.create(project);
		try {
			mains.addAll(getInternalMainNames(javaPrj));
		} catch (Exception e) {
		}
		
		return mains;
	}
	
	/**
	 * Recursive method to find the Classes and check their hierarchy
	 * 
	 * @param element
	 * @return
	 */
	private Vector<String> getInternalMainNames(IParent element) {
		Vector<String> mains = new Vector<String>();
		
		try {
			for (IJavaElement child : element.getChildren()) {
				
				boolean visit = true; 
				
				if (child instanceof IPackageFragmentRoot) {
					IPackageFragmentRoot root = (IPackageFragmentRoot)child;
					if (root.getKind() != IPackageFragmentRoot.K_SOURCE) {
						visit = false;
					}
				}
				
				if (visit) {
					if (child instanceof ICompilationUnit) {
						ICompilationUnit unit = (ICompilationUnit)child;
						IType type = unit.findPrimaryType();
						
						if (isMainImplementation(type)) {
							mains.add(type.getFullyQualifiedName());
						}
					} else if (child instanceof IParent){
						mains.addAll(getInternalMainNames((IParent)child));
					}
				}
			}
		} catch (Exception e) {}
		
		return mains;
	}
	
	private boolean isMainImplementation(IType type) {
		boolean isMainImplementation = false;
		
		try {
			ITypeHierarchy hierarchy = type.newSupertypeHierarchy(null);
			IType[] superInterfaces = hierarchy.getAllSuperInterfaces(type);
			
			int i=0;
			while(!isMainImplementation && i<superInterfaces.length) {
				if (superInterfaces[i].getFullyQualifiedName().equals("com.sun.star.lang.XMain")) {
					isMainImplementation = true;
				} else {
					i++;
				}
			}
		} catch (Exception e) {}
		
		return isMainImplementation;
	}
}
