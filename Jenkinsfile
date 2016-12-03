node {
    stage("Checkout") {
        checkout scm
    }

    stage("Build index image") {

        dir("gravitee-sample-index") {
            sh "docker build -t graviteeio/gravitee-sample-index:latest --pull=true ."
            sh "docker push graviteeio/gravitee-sample-index:latest"
        }
    }

    def parallelBuild = [:]
    def apis = ["gravitee-echo-api", "gravitee-whattimeisit-api", "gravitee-whoami-api"]

    for (int i = 0; i < apis.size(); i++) {
        def api = apis[i]
        parallelBuild[api] = {
            node() {
                sh 'rm -rf *'
                sh 'rm -rf .git'
                checkout scm

                def mvnHome = tool 'MVN33'
                def javaHome = tool 'JDK 8'
                withEnv(["PATH+MAVEN=${mvnHome}/bin",
                         "M2_HOME=${mvnHome}",
                         "JAVA_HOME=${javaHome}"]) {
                    dir(api) {
                        sh "mvn clean deploy"
                        sh "docker build -t graviteeio/${api}:nightly --pull=true ."
                        sh "docker push graviteeio/${api}:nightly"
                    }
                }
            }
        }
    }

    stage("Build apis") {
        parallel parallelBuild
    }
}