def call(Map args = [:]) {
  if (!args.action) {
    error "dockerUtil: action is required"
  }

  switch (args.action) {
    case 'login':
      dockerLogin(args)
      break
    case 'build':
      dockerBuild(args)
      break
    case 'push':
      dockerPush(args)
      break
    default:
      error "dockerUtil: unsupported action ${args.action}"
  }
}

def dockerLogin(Map args) {
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

def dockerBuild(Map args) {
  sh "docker build -t ${args.image} ."
}

def dockerPush(Map args) {
  sh "docker push ${args.image}"
}
