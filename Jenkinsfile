node() {
   def mvnHome
   def version
   def branch
   
   stage('Vorbereitung') {
				  
		mvnHome = tool 'Maven353'
		branch = env.BRANCH_NAME
		version = "1.0.3.$BUILD_NUMBER-$branch"		
		  
		echo "Version: ${inniusVersion}"
		echo "Branch: ${inniusBranch}"
		echo "Maven: ${mvnHome}"

		// Get some code from a GitHub repository
		//git branch: "${branch}", credentialsId: '26f2ceaa-8291-42cc-8656-38d5d7859226', url: 'https://github.com/fx-world/GenerationGap.git'
      
      	//if (isUnix()) {
		//	error 'Muss unter Windows laufen'
		//}
   }
   
	stage('Maven') {		
    	withEnv(["MVN_HOME=$mvnHome"]) {			
            sh('"%MVN_HOME%/bin/mvn" -Dmaven.test.failure.ignore clean package')                        
      	}
	}
	  
   	stage('Ausgabe') {
		junit '**/target/surefire-reports/TEST-*.xml'
		archiveArtifacts '**/target/*site*.zip'
		
		withEnv(["MVN_HOME=$mvnHome"]) {			
            sh('"%MVN_HOME%/bin/mvn" -Dmaven.test.failure.ignore -P deployNightly install')                        
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
	            sh('"%MVN_HOME%/bin/mvn" -Dmaven.test.failure.ignore -P deployRelease install')                        
	      	}
	      	
	      	// mvn(localRepository, '-P deployRelease -f ./jars/generationgap-maven-plugin/pom.xml deploy -DskipTests -Dbuildnumber='+buildnumber)
	    }
   	}
}