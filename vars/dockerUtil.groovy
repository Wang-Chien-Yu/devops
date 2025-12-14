#!/usr/bin/env groovy

def call(Map args = [:]) {
  def action = args.action

  if (!action) {
    error "[dockerUtil] action is required (login | build | push)"
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
      error "[dockerUtil] Unsupported action: ${action}"
  }
}

/* =========================
 * docker login
 * ========================= */
def dockerLogin(Map args) {
  def registry = args.registry
  def credentialsId = args.credentialsId

  if (!registry || !credentialsId) {
    error "[dockerUtil][login] registry & credentialsId are required"
  }

  withCredentials([
    usernamePassword(
      credentialsId: credentialsId,
      usernameVariable: 'DOCKER_USER',
      passwordVariable: 'DOCKER_PASS'
    )
  ]) {
    sh '''
      set -e
      echo "$DOCKER_PASS" | docker login ''' + registry + ''' \
        -u "$DOCKER_USER" --password-stdin
    '''
  }
}

/* =========================
 * docker build
 * ========================= */
def dockerBuild(Map args) {
  def image = args.image

  if (!image) {
    error "[dockerUtil][build] image is required"
  }

  sh """
    set -e
    docker build -t ${image} .
  """
}

/* =========================
 * docker push
 * ========================= */
def dockerPush(Map args) {
  def image = args.image

  if (!image) {
    error "[dockerUtil][push] image is required"
  }

  sh """
    set -e
    docker push ${image}
  """
}
