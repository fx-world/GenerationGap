node {
	def error = null

	try {
		//////////////////////////////////////////////////////////////////////////////
		stage 'Checkout'

		// Get some code from a GitHub repository
		git url: 'https://github.com/fx-world/GenerationGap.git'

		//////////////////////////////////////////////////////////////////////////////
		stage 'Prepare'

		// set correct java version
		env.JAVA_HOME="${tool 'JDK1.8'}"
		env.PATH="${env.JAVA_HOME}/bin:${env.PATH}"
		sh 'java -version'
		
		// get maven tool
		def mvnHome = tool 'Maven339'
        env.PATH = "${mvnHome}/bin:${env.PATH}"
        wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'maven-settings', replaceTokens: false, targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
            sh 'mvn -s $MAVEN_SETTINGS -v' //
        }

		// get git commiter date as version
		sh('git  log -1 --pretty=format:%cd --date=iso > GIT_COMMITER_DATE')
		def date=readFile('GIT_COMMITER_DATE')
		echo "date " + date
		def VERSION='v' + date[0..3] + date[5..6] + date[8..9] + '-' + date[11..12] + date[14..15]
		echo "version " + VERSION

		//////////////////////////////////////////////////////////////////////////////
		stage 'Build'

		try {
			// Run the maven build
			wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'maven-settings', replaceTokens: false, targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
                sh 'mvn -s $MAVEN_SETTINGS clean install' + " -Dversion=${VERSION}" // -Dversion=${VERSION}
            }
            
		} catch (err) {
			step([$class: 'ArtifactArchiver', artifacts: '**/target/*site*.zip'])
			step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])
			
			throw err
		}
		
		archive '**/target/*site*.zip'
		step([$class: 'ArtifactArchiver', artifacts: '**/target/*site*.zip'])
		step([$class: 'JUnitResultArchiver', testResults: '**/target/surefire-reports/TEST-*.xml'])

		//////////////////////////////////////////////////////////////////////////////
		stage 'Release'

		def doRelease = true
		
		// Run the delploy nightly
		wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'maven-settings', replaceTokens: false, targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
            sh 'mvn -s $MAVEN_SETTINGS -P deployNightly -f ./sites/de.fxworld.generationgap.site/pom.xml install' + " -Dversion=${VERSION}"
        }

		try {
			timeout(time:5, unit:'DAYS') {
				input message:'Approve release?', submitter: 'fx'
			}
		} catch (err) {
			// its ok
			doRelease = false
		}
		
		if (doRelease == true) {
			// Run the delploy release
			wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'maven-settings', replaceTokens: false, targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
                echo "test"
                sh 'mvn -s $MAVEN_SETTINGS -P deployRelease -f ./sites/de.fxworld.generationgap.site/pom.xml install' + " -Dversion=${VERSION}" //-Dversion=${VERSION}
                
            }
		}

	} catch (err) {
		currentBuild.result = "FAILURE"

		// mail me the error
		mail body: "GenerationGap build error: ${err}" ,
		from: 'fx@fx-world.de',
		subject: 'GenerationGap build failed',
		to: 'fx@fx-world.de'
		
		throw err
	}
}