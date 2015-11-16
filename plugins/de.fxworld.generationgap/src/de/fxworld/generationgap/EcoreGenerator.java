/*
 * COPYRIGHT_START
 * 
 * Copyright (C) 2015 Pascal Weyprecht
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * COPYRIGHT_END
 *
 * Contributors:
 *    itemis AG - exemplary code
 *    fx-world Softwareentwicklung - initial implementation
 */
package de.fxworld.generationgap;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.emf.codegen.ecore.generator.Generator;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory;
import org.eclipse.emf.codegen.ecore.generator.GeneratorAdapterFactory.Descriptor;
import org.eclipse.emf.codegen.ecore.genmodel.GenBase;
import org.eclipse.emf.codegen.ecore.genmodel.GenModel;
import org.eclipse.emf.codegen.ecore.genmodel.GenModelPackage;
import org.eclipse.emf.codegen.ecore.genmodel.GenPackage;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenBaseGeneratorAdapter;
import org.eclipse.emf.codegen.ecore.genmodel.generator.GenModelGeneratorAdapter;
import org.eclipse.emf.codegen.merge.java.JControlModel;
import org.eclipse.emf.codegen.util.ImportManager;
import org.eclipse.emf.common.notify.Adapter;
import org.eclipse.emf.common.util.BasicMonitor;
import org.eclipse.emf.common.util.Diagnostic;
import org.eclipse.emf.common.util.Monitor;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.common.util.WrappedException;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EClassifier;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.plugin.EcorePlugin;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIConverter;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.mwe.utils.GenModelHelper;
import org.eclipse.emf.mwe2.ecore.CvsIdFilteringGeneratorAdapterFactoryDescriptor;
import org.eclipse.emf.mwe2.runtime.Mandatory;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowComponent;
import org.eclipse.emf.mwe2.runtime.workflow.IWorkflowContext;

/**
 * @author Pascal Weyprecht
 */
public class EcoreGenerator implements IWorkflowComponent {

	protected static Logger log = Logger.getLogger(EcoreGenerator.class);
	static {
		EcorePackage.eINSTANCE.getEFactoryInstance();
		GenModelPackage.eINSTANCE.getEFactoryInstance();
	}

	protected boolean generateEdit          = false;
	protected boolean generateEditor        = false;
	protected boolean generateCustomClasses = false;
	protected String  customSrcPath         = null;

	protected List<String> srcPaths = new ArrayList<String>();
	protected String genModel;
	
	protected Set<IErrorHandler> errorHandlers = new HashSet<IErrorHandler>();
	protected Monitor            monitor       = new BasicMonitor();

	
	public EcoreGenerator() {
		errorHandlers.add(new IErrorHandler() {			
			@Override
			public void handleError(IStatus status) {
				log.error(status, status.getException());				
			}
		});
	}
	
	@Override
	public void preInvoke() {
		new ResourceSetImpl().getResource(URI.createURI(genModel), true);
	}
	
	@Override
	public void postInvoke() {
	}
	
	protected GenModelHelper createGenModelSetup() {
		return new GenModelHelper();
	}

	@Override
	public void invoke(IWorkflowContext ctx) {

		ResourceSet resourceSet      = new ResourceSetImpl();
		resourceSet.getURIConverter().getURIMap().putAll(EcorePlugin.computePlatformURIMap(true)); //new
		URI         uri              = URI.createURI(genModel);
        Resource    genModelResource = resourceSet.getResource(uri, true);
        
        final GenModel genModel = (GenModel)genModelResource.getContents().get(0);

        IStatus status = genModel.validate();
        
        if (!status.isOK()) {
          notifyErrorHandlers(status);
          
        } else {		
			//genModel.reconcile();
			genModel.setCanGenerate(true);
			genModel.setUpdateClasspath(false); //was true
	
			if (generateCustomClasses) {
				generateCustomClasses(genModel);
			}
			
			createGenModelSetup().registerGenModel(genModel);
	
			Generator generator = new Generator() {
				@Override
				public JControlModel getJControlModel() {
					return new JControlModel(){
						@Override
						public boolean canMerge() {
							return false;
						}
					};
				}
			};
	
			generator.getAdapterFactoryDescriptorRegistry().addDescriptor(GenModelPackage.eNS_URI, (Descriptor) new GeneratorAdapterDescriptor(getTypeMapper()));
			generator.setInput(genModel);
			
			log.info("generating EMF code for " + this.genModel);
			
			Diagnostic diagnostic = generator.generate(genModel, GenBaseGeneratorAdapter.MODEL_PROJECT_TYPE, getMonitor());
	
			if (diagnostic.getSeverity() != Diagnostic.OK)
				log.info(diagnostic);
	
			if (generateEdit) {
				Diagnostic editDiag = generator.generate(genModel, GenBaseGeneratorAdapter.EDIT_PROJECT_TYPE, getMonitor());
				if (editDiag.getSeverity() != Diagnostic.OK)
					log.info(editDiag);
			}
	
			if (generateEditor) {	
				Diagnostic editorDiag = generator.generate(genModel, GenBaseGeneratorAdapter.EDITOR_PROJECT_TYPE, getMonitor());
				
				if (editorDiag.getSeverity() != Diagnostic.OK) {
					log.info(editorDiag);
				}
				
			}
			
			modifyJavaModelClasses(genModel);
        }
	}

	protected void generateCustomClasses(GenModel genModel) {
		for (GenPackage genPackage : genModel.getAllGenPackagesWithClassifiers()) {
			for (EClassifier eClassifier : genPackage.getEcorePackage().getEClassifiers()) {
				if (eClassifier instanceof EClass) {
					EClass eClass = (EClass) eClassifier;
					String from   = genPackage.getClassPackageName() + "." + eClass.getName() + "Impl";
					
					if (!existsCustomClass(from)) {
						URI createURI = URI.createURI(customSrcPath + "/" + from.replace('.', '/') + "Custom.java");
						String customClassName = from+"Custom";
						
						generate(from,customClassName,createURI);
					}
				}
			}
		}
	}
	
	protected void modifyJavaModelClasses(GenModel genModel) {
		String directory = genModel.getModelDirectory();
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFolder folder = root.getFolder(new Path(directory));		
		
		try {
			folder.accept(new IResourceVisitor() {
				
				@Override
				public boolean visit(IResource resource) throws CoreException {
					boolean result = false;
					File file = resource.getLocation().toFile();
					
					if (file.isFile()) {
						if (file.exists() && file.getName().endsWith("Impl.java")) {
							modifyJavaModelClass(file);
						}
					} else {
						result = true;
					}
					
					return result;
				}
			});
			
		} catch (CoreException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	protected void modifyJavaModelClass(File inputFile) {
		try {
		    // Get the object of DataInputStream
			File            outputFile = new File(inputFile.getAbsolutePath() + ".tmp");
			FileReader      fileReader = new FileReader(inputFile);
			FileWriter      fileWriter = new FileWriter(outputFile);
		    BufferedReader  reader     = new BufferedReader(fileReader);
		    BufferedWriter  writer     = new BufferedWriter(fileWriter);
		    String          line       = null;
		     		     
		    //Read File Line By Line
		    while ((line = reader.readLine()) != null)   {
		    	if (!line.contains("// TODO: implement this method") &&	!line.contains("// Ensure that you remove @generated or mark it @generated NOT")) {
		    	
		    		line = line.replace("throw new UnsupportedOperationException();", "throw new UnsupportedOperationException(\"This method is not supported in \" + this.getClass().getName());");
		    	
		    		writer.write(line);
		    		writer.newLine();
		    	}
		    }
		     
		    writer.flush();
		    
		    //Close the input stream
		    fileReader.close();
		    fileWriter.close();
		    
		    inputFile.delete();
		    outputFile.renameTo(inputFile);
		    
		} catch (IOException e) {
			throw new WrappedException(e);
		}
	         
	}

	protected IFunction<String, String> getTypeMapper() {
		return new Mapper();
	}

	
	protected final class Mapper implements IFunction<String, String> {
		public String apply(String from) {
			String result = null;
			
			if (!from.startsWith("org.eclipse.emf.ecore")) {
				
				if (existsCustomClass(from)) {
					result = from+"Custom";
				}
			}
			
			return result;
		}
	}
	
	public void generate(String from,String customClassName, URI path) {
		StringBuilder sb =new StringBuilder();
		//sb.append(copyright()).append("\n");
		int lastIndexOfDot = customClassName.lastIndexOf('.');
		sb.append("package ").append(customClassName.substring(0, lastIndexOfDot)).append(";\n\n\n");
		sb.append("public class ").append(customClassName.substring(lastIndexOfDot+1)).append(" extends ").append(from).append(" {\n\n");
		sb.append("}\n");
		
		try {
			OutputStream stream = URIConverter.INSTANCE.createOutputStream(path);
			stream.write(sb.toString().getBytes());
			stream.close();
		} catch (IOException e) {
			throw new WrappedException(e);
		}
	}
	
	protected boolean existsCustomClass(String from) {
		boolean result = false;
		
		for(String srcPath: srcPaths) {
			URI createURI = URI.createURI(srcPath+"/"+from.replace('.', '/')+"Custom.java");
				
			if (URIConverter.INSTANCE.exists(createURI, null)) {
				result = true;
				break;
			}
		}
		
		return result;
	}

	/**
	 * @author Sven Efftinge - Initial contribution and API
	 */
	protected static class GeneratorAdapterDescriptor extends CvsIdFilteringGeneratorAdapterFactoryDescriptor {
		/**
		 * @author Sebastian Zarnekow - Initial contribution and API
		 */
		private final class CustomImplAwareGeneratorAdapterFactory extends IdFilteringGenModelGeneratorAdapterFactory {
			@Override
			public Adapter createGenClassAdapter() {
				return new IdFilteringGenClassAdapter(this) {
					@Override
					protected void createImportManager(String packageName, String className) {
						importManager = new ImportManagerHack(packageName, typeMapper);
						importManager.addMasterImport(packageName, className);
						if (generatingObject != null) {
							((GenBase) generatingObject).getGenModel().setImportManager(importManager);
						}
					}
				};
			}

			@Override
			public Adapter createGenEnumAdapter() {
				return new IdFilteringGenEnumAdapter(this) {
					@Override
					protected void createImportManager(String packageName, String className) {
						importManager = new ImportManagerHack(packageName, typeMapper);
						importManager.addMasterImport(packageName, className);
						if (generatingObject != null) {
							((GenBase) generatingObject).getGenModel().setImportManager(importManager);
						}
					}
				};
			}

			@Override
			public Adapter createGenModelAdapter() {
				if (genModelGeneratorAdapter == null) {
					genModelGeneratorAdapter = new GenModelGeneratorAdapter(this) {
						@Override
						protected void createImportManager(String packageName, String className) {
							importManager = new ImportManagerHack(packageName, typeMapper);
							importManager.addMasterImport(packageName, className);
							if (generatingObject != null) {
								((GenBase) generatingObject).getGenModel().setImportManager(importManager);
							}
						}

					};
				}
				return genModelGeneratorAdapter;
			}

			@Override
			public Adapter createGenPackageAdapter() {
				return new IdFilteringGenPackageAdapter(this) {
					@Override
					protected void createImportManager(String packageName, String className) {
						importManager = new ImportManagerHack(packageName, typeMapper);
						importManager.addMasterImport(packageName, className);
						if (generatingObject != null) {
							((GenBase) generatingObject).getGenModel().setImportManager(importManager);
						}
					}
				};
			}
		}

		private IFunction<String, String> typeMapper;

		protected GeneratorAdapterDescriptor(IFunction<String,String> typeMapper) {
			this.typeMapper = typeMapper;
		}

		@Override
		public GeneratorAdapterFactory createAdapterFactory() {
			return new CustomImplAwareGeneratorAdapterFactory();
		}
	}

	protected static class ImportManagerHack extends ImportManager {

		private IFunction<String, String> typeMapper;

		public ImportManagerHack(String compilationUnitPackage, IFunction<String,String> typeMapper) {
			super(compilationUnitPackage);
			this.typeMapper = typeMapper;
		}

		@Override
		public String getImportedName(String qualifiedName, boolean autoImport) {
			String mapped = typeMapper.apply(qualifiedName);
			if (mapped != null) {
				log.debug("mapping " + qualifiedName + " to " + mapped);
				return super.getImportedName(mapped, autoImport);
			} else
				return super.getImportedName(qualifiedName, autoImport);
		}
	}
	
	protected void notifyErrorHandlers(IStatus status) {
		for (IErrorHandler handler : new HashSet<IErrorHandler>(errorHandlers)) {
			handler.handleError(status);
		}		
	}

	public Monitor getMonitor() {
		return monitor;
	}

	public void setMonitor(Monitor monitor) {
		this.monitor = monitor;
	}
	
	public void addErrorHandler(IErrorHandler errorHandler) {
		errorHandlers.add(errorHandler);
	}
	

	public void removeErrorHandler(IErrorHandler errorHandler) {
		errorHandlers.remove(errorHandler);
	}
	
	public void setGenerateEdit(boolean generateEdit) {
		this.generateEdit = generateEdit;
	}
	
	public void setGenerateEditor(boolean generateEditor) {
		this.generateEditor = generateEditor;
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

	@Mandatory
	public void addSrcPath(String srcPath) {
		this.srcPaths.add(srcPath);
	}
	
	@Mandatory
	public void setGenModel(String genModel) {
		this.genModel = genModel;
	}
}
