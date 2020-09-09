node() {
   def mvnHome
   def version
   def branch
   
   stage('Vorbereitung') {
				  
		mvnHome = tool 'Maven35'
		branch = env.BRANCH_NAME
		version = "1.0.4.$BUILD_NUMBER"		
		  
		echo "Version: ${version}"
		echo "Branch: ${branch}"
		echo "Maven: ${mvnHome}"

		// Get some code from a GitHub repository
		git branch: "${branch}", credentialsId: '41cdca34-1537-4179-901d-ed553d37db44', url: 'https://github.com/fx-world/GenerationGap.git'
      
      	if (!isUnix()) {
			error 'Must build with linux'
		}
   }
   
	stage('Maven') {		
    	withEnv(["MVN_HOME=$mvnHome"]) {			
            sh('"${MVN_HOME}/bin/mvn" -Dmaven.test.failure.ignore clean package')                        
      	}
	}
	  
   	stage('Ausgabe') {
		junit '**/target/surefire-reports/TEST-*.xml'
		archiveArtifacts '**/target/*site*.zip'
		
		withEnv(["MVN_HOME=$mvnHome"]) {			
            sh('"${MVN_HOME}/bin/mvn" -Dmaven.test.failure.ignore -P deployNightly install')                        
      	}
   	}
   	
   	stage('Release') {
   		def doRelease = true
   		
   		try {
			timeout(time:1, unit:'DAYS') {
				input message:'Approve release?', submitter: 'fx'
			}
		} catch (err) {
			// its ok
			doRelease = false
		}
		
		if (doRelease == true) {
			withEnv(["MVN_HOME=$mvnHome"]) {			
	            sh('"${MVN_HOME}/bin/mvn" -Dmaven.test.failure.ignore -P deployRelease install')                        
	      	}
	      	
	      	// mvn(localRepository, '-P deployRelease -f ./jars/generationgap-maven-plugin/pom.xml deploy -DskipTests -Dbuildnumber='+buildnumber)
	    }
   	}
}