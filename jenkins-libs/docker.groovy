def dockerLogin(registry, Map args = [:]) {
  def script = this
  def credentialsId = args.credentialsId

  withCredentials([
    usernamePassword(
      credentialsId: credentialsId,
      usernameVariable: 'DOCKER_USER',
      passwordVariable: 'DOCKER_PASS'
    )
  ]) {
    sh """
      echo "\$DOCKER_PASS" | docker login ${registry} \
        -u "\$DOCKER_USER" --password-stdin
    """
  }
}

def dockerBuild(image) {
  sh "docker build -t ${image} ."
}

def dockerPush(image) {
  sh "docker push ${image}"
}

