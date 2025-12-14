/**
 * Jenkins Shared Library
 * Usage:
 *   docker(
 *     action: 'login',
 *     registry: 'harbor.xxx.com',
 *     credentialsId: 'harbor-admin'
 *   )
 *
 *   docker(
 *     action: 'build',
 *     image: 'harbor.xxx.com/demo/app:tag'
 *   )
 *
 *   docker(
 *     action: 'push',
 *     image: 'harbor.xxx.com/demo/app:tag'
 *   )
 */
def call(Map args = [:]) {
  def action = args.action

  if (!action) {
    error "docker: action is required (login | build | push)"
  }

  switch (action) {
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
      error "docker: unsupported action '${action}'"
  }
}

/* ===================== internal methods ===================== */

private def dockerLogin(Map args) {
  def registry       = args.registry
  def credentialsId  = args.credentialsId

  if (!registry || !credentialsId) {
    error "docker login requires registry and credentialsId"
  }

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

private def dockerBuild(Map args) {
  def image = args.image
  if (!image) {
    error "docker build requires image"
  }

  sh "docker build -t ${image} ."
}

private def dockerPush(Map args) {
  def image = args.image
  if (!image) {
    error "docker push requires image"
  }

  sh "docker push ${image}"
}
