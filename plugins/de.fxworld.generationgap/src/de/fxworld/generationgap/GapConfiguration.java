package de.fxworld.generationgap;

/*
 * #%L
 * de.fxworld.generationgap
 * %%
 * Copyright (C) 2016 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 * 
 * Contributors:
 *    itemis AG - exemplary code
 *    fx-world Softwareentwicklung - initial implementation
 */


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.ICommand;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaModelException;

public class GapConfiguration {

	protected static final String GEN_MODELS              = "genModels";
	protected static final String CLEAR_SRC_PATHS         = "srcPaths";
	protected static final String CUSTOM_SRC_PATH         = "customSrcPath";
	protected static final String GENERATE_CUSTOM_CLASSES = "generateCustomClasses";
	protected static final String GENERATE_EDITOR         = "generateEditor";
	protected static final String GENERATE_EDIT           = "generateEdit";

	private IJavaProject javaProject = null;
	
	private boolean generateEdit          = false;
	private boolean generateEditor        = false;
	private boolean generateCustomClasses = false;
	private String  customSrcPath         = "";

	private List<String> srcPaths  = new ArrayList<String>();
	private List<String> genModels = new ArrayList<String>();
	
	public GapConfiguration() {
		
	}
	
	public GapConfiguration(IJavaProject javaProject) {
		setJavaProject(javaProject);
		load();
	}
		
	public void load() {
		try {
			Map<String, String> properties = getBuilderArguments(getJavaProject().getProject());
			
			if (existProperties(properties)) {	 
				setProperties(properties);
			} else {
				resetDefault();
			}
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void save() {
		try {
			IProject            project  = getJavaProject().getProject();
			IProjectDescription desc     = project.getDescription();
			ICommand[]          commands = desc.getBuildSpec();
			
			for (ICommand command : commands) {
				if (command.getBuilderName().equals(Activator.BUILDER_ID)) {
					command.setArguments(getProperties());
					break;
				}
			}
			
			desc.setBuildSpec(commands);
			project.setDescription(desc, null);
		
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void resetDefault() {
		final IProject     project       = getJavaProject().getProject();
		final List<String> genModels     = new ArrayList<String>();
		final List<String> clearSrcPaths = new ArrayList<String>();
		
		setGenerateEdit(false);
		setGenerateEditor(false);
		setGenerateCustomClasses(false);

		// add all generator models
		try {
			project.accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					if ((resource != null) && (resource instanceof IFile)) {
						IFile file = (IFile) resource;
						
						if ((file.exists()) && ("genmodel".equals(file.getFileExtension()))) {
							genModels.add(file.getProjectRelativePath().toString());
						}
					}
					
					return true; // visit children too
				}
			});
			
		} catch (CoreException e) {
			e.printStackTrace();
		}
		
		// get all source paths
		try {
			boolean found = false;
			
			for (IPackageFragmentRoot root : javaProject.getAllPackageFragmentRoots()) {
				if ((root.getKind() == IPackageFragmentRoot.K_SOURCE) && (root.getJavaProject() == javaProject)) {
					if (root.getElementName().contains("src-gen")) {
						clearSrcPaths.add(root.getResource().getProjectRelativePath().toString());
					
					} else if (!found) {
						setCustomSrcPath(root.getResource().getProjectRelativePath().toString());
						found = true;
					}
				}
			}
			
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setGenModels(genModels);
		setSrcPaths(clearSrcPaths);		
	}
	
	protected ICommand getBuilderCommand(IProject project) throws CoreException {
		ICommand            result   = null;
		IProjectDescription desc     = project.getDescription();
		ICommand[]          commands = desc.getBuildSpec();
		
		for (ICommand command : commands) {
			if (command.getBuilderName().equals(Activator.BUILDER_ID)) {
				result = command;
				break;
			}
		}
		
		return result;
	}
	
	protected Map<String,String> getBuilderArguments(IProject project) throws CoreException {
		Map<String, String> result  = null;
		ICommand            command = getBuilderCommand(project);
		
		if (command != null) {
			result = command.getArguments();
		}
		
		return result;
	}
	
	protected void setBuilderArguments(IProject project, Map<String,String> arguments) throws CoreException {
		ICommand            command = getBuilderCommand(project);
		
		if (command != null) {
			command.setArguments(arguments);
		}
	}

	public IJavaProject getJavaProject() {
		return javaProject;
	}

	public void setJavaProject(IJavaProject javaProject) {
		this.javaProject = javaProject;
	}

	public boolean isGenerateEdit() {
		return generateEdit;
	}

	public void setGenerateEdit(boolean generateEdit) {
		this.generateEdit = generateEdit;
	}

	public boolean isGenerateEditor() {
		return generateEditor;
	}

	public void setGenerateEditor(boolean generateEditor) {
		this.generateEditor = generateEditor;
	}

	public boolean isGenerateCustomClasses() {
		return generateCustomClasses;
	}

	public void setGenerateCustomClasses(boolean generateCustomClasses) {
		this.generateCustomClasses = generateCustomClasses;
	}

	public String getCustomSrcPath() {
		return customSrcPath;
	}

	public void setCustomSrcPath(String customSrcPath) {
		this.customSrcPath = customSrcPath;
	}

	public List<String> getSrcPaths() {
		return srcPaths;
	}

	public void setSrcPaths(List<String> srcPaths) {
		this.srcPaths = srcPaths;
	}

	public List<String> getGenModels() {
		return genModels;
	}

	public void setGenModels(List<String> genModels) {
		this.genModels = genModels;
	}

	protected boolean existProperties(Map<String, String> properties) {
		boolean result = false;
		
		if (properties != null) {
			result |= (properties.get(GENERATE_EDIT) != null);
			result |= (properties.get(GENERATE_EDITOR) != null);
			result |= (properties.get(GENERATE_CUSTOM_CLASSES) != null);
			result |= (properties.get(CUSTOM_SRC_PATH) != null);
			result |= (properties.get(CLEAR_SRC_PATHS) != null);
			result |= (properties.get(GEN_MODELS) != null);
		}
		
		return result;
	}
	
	protected Map<String, String> getProperties() {
		Map<String, String> result = new HashMap<String, String>();
		
		result.put(GENERATE_EDIT,           Boolean.toString(generateEdit));
		result.put(GENERATE_EDITOR,         Boolean.toString(generateEditor));
		result.put(GENERATE_CUSTOM_CLASSES, Boolean.toString(generateCustomClasses));
		result.put(CUSTOM_SRC_PATH,         customSrcPath);
		result.put(CLEAR_SRC_PATHS,         convertToString(srcPaths));
		result.put(GEN_MODELS,              convertToString(genModels));
		
		return result;
	}
	
	protected void setProperties(Map<String, String> properties) {
		generateEdit          = Boolean.parseBoolean(readProperty(properties, GENERATE_EDIT, Boolean.toString(false)));
		generateEditor        = Boolean.parseBoolean(readProperty(properties, GENERATE_EDITOR, Boolean.toString(false)));
		generateCustomClasses = Boolean.parseBoolean(readProperty(properties, GENERATE_CUSTOM_CLASSES, Boolean.toString(false)));
		customSrcPath         = readProperty(properties, CUSTOM_SRC_PATH, "");
		srcPaths              = convertToList(readProperty(properties, CLEAR_SRC_PATHS, ""));
		genModels             = convertToList(readProperty(properties, GEN_MODELS, ""));
	}
	
	protected String readProperty(Map<String, String> properties, String name, String defaultValue) {
		String result = defaultValue;
		
		if (properties != null) {
			String value = properties.get(name);
			
			if (value != null) {
				result = value;
			}
		}
		
		return result;
	}

	protected String convertToString(List<String> values) {
		StringBuilder result = new StringBuilder();
		boolean       first  = true;
		
		for (String value : values) {
			if (first) {
				first = false;
			} else {
				result.append(";");
			}
			
			result.append(value);
		}
		
		return result.toString();
	}
	
	protected List<String> convertToList(String values) {
		List<String> result = new ArrayList<String>();
		
		if ((values != null) && (!values.trim().isEmpty())) {
			for (String value : values.split(";")) {
				result.add(value);
			}
		}
		
		return result;
	}
}
