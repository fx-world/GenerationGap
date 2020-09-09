node() {
   def mvnHome
   def version
   def branch
   
   stage('Vorbereitung') {
				  
		mavenTool = 'Maven36'
		jdk = 'JDK11'
		branch = 'master'
		version = "1.0.4.$BUILD_NUMBER"		
		  
		echo "Version: ${version}"
		echo "Branch: ${branch}"

		// Get some code from a GitHub repository
		git branch: "${branch}", credentialsId: '41cdca34-1537-4179-901d-ed553d37db44', url: 'https://github.com/fx-world/GenerationGap.git'
      
      	if (!isUnix()) {
			error 'Must build with linux'
		}
   }
   
	stage('Maven') {
	    withMaven(jdk: jdk, maven: mavenTool, options: [artifactsPublisher(disabled: true)]) {
            sh('mvn -Dmaven.test.failure.ignore clean package')                        
        }
	}
	  
   	stage('Ausgabe') {
		archiveArtifacts '**/target/*site*.zip'
		
		withMaven(jdk: jdk, maven: mavenTool, options: [artifactsPublisher(disabled: true)]) {
            sh('mvn -Dmaven.test.failure.ignore -P deployNightly install')                        
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
	      	withMaven(jdk: jdk, maven: mavenTool, options: [artifactsPublisher(disabled: true)]) {
	            sh('mvn -Dmaven.test.failure.ignore -P deployRelease install')                        
	        }
	      	
	      	// mvn(localRepository, '-P deployRelease -f ./jars/generationgap-maven-plugin/pom.xml deploy -DskipTests -Dbuildnumber='+buildnumber)
	    }
   	}
}