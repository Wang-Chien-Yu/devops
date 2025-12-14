def call(Map args = [:]) {
  def action = args.action

  if (!action) {
    error "dockerUtil: action is required"
  }

  switch (action) {
    case 'login':
      login(args)
      break
    case 'build':
      buildImage(args)
      break
    case 'push':
      pushImage(args)
      break
    default:
      error "dockerUtil: unsupported action ${action}"
  }
}

private def login(Map args) {
  withCredentials([
    usernamePassword(
      credentialsId: args.credentialsId,
      usernameVariable: 'DOCKER_USER',
      passwordVariable: 'DOCKER_PASS'
    )
  ]) {
    sh """
      echo "\$DOCKER_PASS" | docker login ${args.registry} \
        -u "\$DOCKER_USER" --password-stdin
    """
  }
}

private def buildImage(Map args) {
  sh "docker build -t ${args.image} ."
}

private def pushImage(Map args) {
  sh "docker push ${args.image}"
}
