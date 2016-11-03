node {
    stage("Build") {
        checkout scm
        def mvnHome = tool 'MVN33'
        def javaHome = tool 'JDK 8'
        withEnv(["PATH+MAVEN=${mvnHome}/bin",
                 "M2_HOME=${mvnHome}",
                 "JAVA_HOME=${javaHome}"]) {
            def dirs = ["gravitee-echo-api", "gravitee-whoami-api"]
            for( int i = 0; i < dirs.size(); i++){
                dir(dirs[i]) {
                    sh "mvn clean package"
                    sh "docker build -t graviteeio/${dirs[i]}:latest --pull=true ."
                    sh "docker push graviteeio/${dirs[i]}:latest"
                }
            }
        }
    }
}