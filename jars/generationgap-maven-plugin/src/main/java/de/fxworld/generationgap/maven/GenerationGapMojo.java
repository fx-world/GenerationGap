package de.fxworld.generationgap.maven;

/*
 * #%L
 * generationgap-maven-plugin Maven Plugin
 * %%
 * Copyright (C) 2016 - 2018 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 * 
 * SPDX-License-Identifier: EPL-2.0
 * #L%
 */


import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.plugin.EcorePlugin;

import de.fxworld.generationgap.EcoreGenerator;


/**
 * Goal which touches a timestamp file.
 *
 */
@Mojo( 
	name = "generationgap", 
	defaultPhase = LifecyclePhase.PROCESS_SOURCES, 
	requiresOnline = false, 
	//requiresProject = true,
    threadSafe = false
)
public class GenerationGapMojo extends AbstractMojo {
    	
	@Parameter(defaultValue="${project}", readonly=true)
	private MavenProject project;
		
	
	@Parameter(property="generateEdit", defaultValue = "false")
	private boolean generateEdit;
	
	@Parameter(property="generateEditor", defaultValue = "false")	
	private boolean generateEditor;
	
	@Parameter(property="generateCustomClasses", defaultValue = "false")
	private boolean generateCustomClasses;
	
	@Parameter(property="customSrcPath", defaultValue = "src")
	private String  customSrcPath;

	@Parameter(property="srcPaths")
	private List<String> srcPaths;
	
	@Parameter(property="genModels")
	private List<String> genModels;
	
	@Parameter(property="platformResourceMap")
	private Map<String, String> platformResourceMap;

    public void execute() throws MojoExecutionException {
    	
    	if (platformResourceMap != null) {
    		for (Entry<String, String> entry: platformResourceMap.entrySet()) {
    			String name     = entry.getKey();
    			URI    location = convertToURI(entry.getValue(), false);
    			
    			EcorePlugin.getPlatformResourceMap().put(name, location);
    		}
    	}    	
    	
    	EcoreGenerator generator = new EcoreGenerator();
    	generator.setGenerateEdit(generateEdit);
    	generator.setGenerateEditor(generateEditor);
    	generator.setGenerateCustomClasses(generateCustomClasses);
    	generator.setCustomSourceGenerateURI(convertToURI(customSrcPath, false));
    	
    	for (String srcPath : srcPaths) {
    		generator.addCustomSourceURI(convertToURI(srcPath, false));
    	}
    	
    	for (String genModel : genModels) {    		
    		generator.setGenModelURI(convertToURI(genModel, true));
    		
    		generator.preInvoke();
    		generator.invoke();
    		generator.postInvoke();
    	}
    }

	protected URI convertToURI(String path, boolean useFileURI) {
		URI  result = null;
		File file   = new File(path);
		
		if (useFileURI) {
			result = URI.createFileURI(file.getAbsolutePath());
		} else {
			result = URI.createURI("file:///" + file.getAbsolutePath().replace("\\", "/") + "/");
		}
		
		return result;
	}
}
