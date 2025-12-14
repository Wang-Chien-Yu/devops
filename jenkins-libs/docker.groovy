// 注意：移除了 def script = this，并添加了 script 参数
def dockerLogin(script, registry, Map args = [:]) {
  def credentialsId = args.credentialsId

  script.withCredentials([ // 必须使用 script.withCredentials
    script.usernamePassword(
      credentialsId: credentialsId,
      usernameVariable: 'DOCKER_USER',
      passwordVariable: 'DOCKER_PASS'
    )
  ]) {
    script.sh """ // 必须使用 script.sh
      echo "\$DOCKER_PASS" | docker login ${registry} \
        -u "\$DOCKER_USER" --password-stdin
    """
  }
}

def dockerBuild(script, image) { // 添加 script 参数
  script.sh "docker build -t ${image} ." // 必须使用 script.sh
}

def dockerPush(script, image) { // 添加 script 参数
  script.sh "docker push ${image}" // 必须使用 script.sh
}

return this