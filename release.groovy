node {
    stage("Checkout") {
        checkout scm
    }

    def parallelBuild = [:]
    def apis = ["gravitee-echo-api", "gravitee-whattimeisit-api", "gravitee-whoami-api"]

    for ( int i = 0; i < apis.size(); i++ ) {
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
                        sh "mvn -B versions:set -DnewVersion=${releaseVersion} -DgenerateBackupPoms=false"
                        sh "mvn -B -U -P gravitee-release clean install"

                        //hack
                        sh "rm -f target/*-sources.jar"
                        sh "rm -f target/*-javadoc.jar"
                        sh "docker build -t graviteeio/${api}:latest -t graviteeio/${api}:${releaseVersion} --pull=true ."

                        sh "git add --update"
                        sh "git commit -m 'release(${releaseVersion})'"
                        sh "git tag ${releaseVersion}"

                        //sh "git push --tags origin master"
                        //sh "docker push graviteeio/${api}:latest"
                        //sh "docker push graviteeio/${api}:${releaseVersion}"
                    }
                }
            }
        }
    }

    stage("Release") {
        parallel parallelBuild
    }
}