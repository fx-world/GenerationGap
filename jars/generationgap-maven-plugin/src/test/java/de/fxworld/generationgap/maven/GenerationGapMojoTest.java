package de.fxworld.generationgap.maven;

/*
 * #%L
 * generationgap-maven-plugin Maven Plugin
 * %%
 * Copyright (C) 2016 fx-world Softwareentwicklung
 * %%
 * This program and the accompanying materials are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v. 1.0 which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * #L%
 */


import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.codehaus.plexus.configuration.PlexusConfiguration;
import org.codehaus.plexus.util.ReaderFactory;
import org.codehaus.plexus.util.xml.Xpp3Dom;
import org.codehaus.plexus.util.xml.Xpp3DomBuilder;

public class GenerationGapMojoTest extends AbstractMojoTestCase {
	
    /* (non-Javadoc)
     * @see org.apache.maven.plugin.testing.AbstractMojoTestCase#setUp()
     */
	@Override
    protected void setUp() throws Exception {
        // required
        super.setUp();
    }
    
    /* (non-Javadoc)
     * @see org.codehaus.plexus.PlexusTestCase#tearDown()
     */
	@Override
    protected void tearDown() throws Exception {
        // required
        super.tearDown();
    }

    /**
     * @throws Exception if any
     */
    public void testGenerator() throws Exception {
    	File testPackage = new File("src/test/resources/unit/simpleGeneration/src-gen/de/fxworld/testpackage/impl/TestpackageFactoryImpl.java");
    	if (testPackage.exists()) {
    		testPackage.delete();
    	}
    	
    	File testGroupImpl = new File("src/test/resources/unit/simpleGeneration/src-gen/de/fxworld/testpackage/impl/GroupImpl.java");
    	if (testGroupImpl.exists()) {
    		testGroupImpl.delete();
    	}
    	
    	File testUserImpl = new File("src/test/resources/unit/simpleGeneration/src-gen/de/fxworld/testpackage/impl/UserImpl.java");
    	if (testGroupImpl.exists()) {
    		testGroupImpl.delete();
    	}
    	
        File pom = getTestFile("src/test/resources/unit/simpleGeneration/pom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        
        File pluginPom = new File( getBasedir(), "pom.xml" );
        Xpp3Dom pluginPomDom = Xpp3DomBuilder.build( ReaderFactory.newXmlReader( pluginPom ) );
        String artifactId = pluginPomDom.getChild( "artifactId" ).getValue();
        String groupId = "de.fx-world";
        String version = "0.0.1-SNAPSHOT";
        PlexusConfiguration pluginConfiguration = extractPluginConfiguration( artifactId, pom );
        String goal = "generationgap";
        GenerationGapMojo mojo = (GenerationGapMojo) lookupMojo(groupId, artifactId, version, goal, pluginConfiguration);
        //GenerationGapMojo mojo = (GenerationGapMojo) lookupMojo("generationgap", pom);        
        assertNotNull(mojo);
        
        List<String> genModels = new ArrayList<String>();
        genModels.add("src/test/resources/unit/simpleGeneration/metamodel/test.genmodel");
        setVariableValueToObject(mojo, "genModels", genModels);

        mojo.execute();

                
        assertTrue(testPackage.exists());
        
        assertTrue(Files.newBufferedReader(testPackage.toPath())
        	.lines()
        	.filter(line -> line.contains("GroupImplCustom"))
        	.findAny()
        	.isPresent());       
        
        assertFalse(Files.newBufferedReader(testPackage.toPath())
            	.lines()
            	.filter(line -> line.contains("UserImplCustom"))
            	.findAny()
            	.isPresent());
        
        // the group should not implment the custom class
        assertFalse(Files.newBufferedReader(testGroupImpl.toPath())
            	.lines()
            	.filter(line -> line.contains("implements GroupImplCustom"))
            	.findAny()
            	.isPresent());
        
        // also abstract classes should be extended
        assertTrue(Files.newBufferedReader(testGroupImpl.toPath())
            	.lines()
            	.filter(line -> line.contains("extends NamedElementImplCustom"))
            	.findAny()
            	.isPresent());
        
        // also abstract classes should be extended
        assertTrue(Files.newBufferedReader(testUserImpl.toPath())
            	.lines()
            	.filter(line -> line.contains("extends NamedElementImplCustom"))
            	.findAny()
            	.isPresent());
    }
}
