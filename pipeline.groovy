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
		
		// get git commiter date as version
		sh('git log -1 --pretty=format:%cd --date=iso > GIT_COMMITER_DATE')
		def date=readFile('GIT_COMMITER_DATE')
		echo "date " + date
		def buildnumber='v' + date[0..3] + date[5..6] + date[8..9] + '-' + date[11..12] + date[14..15]
		echo "buildnumber " + buildnumber

		// get maven tool
		def localRepository = pwd([tmp: true]) + "/mavenRepository"
		def mvnHome = tool 'Maven339'
        env.PATH = "${mvnHome}/bin:${env.PATH}"
        //def mvnCmd = 'mvn -s $MAVEN_SETTINGS -Dmaven.repo.local=' + localRepository
        //wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'maven-settings', replaceTokens: false, targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
        //    sh 'mvn -s $MAVEN_SETTINGS -Dmaven.repo.local=./.m2 -v' //
        //}
        mvn(localRepository, '-v')

		//////////////////////////////////////////////////////////////////////////////
		stage 'Build'

		try {
			// Run the maven build
			//wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'maven-settings', replaceTokens: false, targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
            //    sh 'mvn -s $MAVEN_SETTINGS -Dmaven.repo.local=./.m2 clean install' + " -Dversion=${VERSION}" // -Dversion=${VERSION}
            //}
            mvn(localRepository, 'clean install')
            
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
		//wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'maven-settings', replaceTokens: false, targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
        //    sh 'mvn -s $MAVEN_SETTINGS -Dmaven.repo.local=./.m2 -P deployNightly -f ./sites/de.fxworld.generationgap.site/pom.xml install' + " -Dversion=${VERSION}"
        //}
        mvn(localRepository, '-P deployNightly -f ./sites/de.fxworld.generationgap.site/pom.xml install')

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
			//wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'maven-settings', replaceTokens: false, targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
            //    echo "test"
            //    sh 'mvn -s $MAVEN_SETTINGS -Dmaven.repo.local=./.m2 -P deployRelease -f ./sites/de.fxworld.generationgap.site/pom.xml install' + " -Dversion=${VERSION}" //-Dversion=${VERSION}
            //}
            mvn(localRepository, '-P deployRelease -f ./sites/de.fxworld.generationgap.site/pom.xml install')
            
            mvn(localRepository, '-P deployRelease -f ./jars/generationgap-maven-plugin/pom.xml deploy -DskipTests -Dbuildnumber='+buildnumber)
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

def mvn(localRepository, parameter) {
    wrap([$class: 'ConfigFileBuildWrapper', managedFiles: [[fileId: 'maven-settings', replaceTokens: false, targetLocation: '', variable: 'MAVEN_SETTINGS']]]) {
        sh 'mvn -s $MAVEN_SETTINGS -Dmaven.repo.local=' + localRepository + ' ' + parameter
    }
}