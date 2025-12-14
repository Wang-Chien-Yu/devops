def dockerLogin(registry, Map args = [:]) {
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

def build(image) {
  sh "docker build -t ${image} ."
}

def push(image) {
  sh "docker push ${image}"
}

return this
