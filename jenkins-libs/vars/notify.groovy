def call(String status) {
  def Token = env.TG_BOT_ID
  def ChatId = env.TG_GROUP_ID
  def time = new Date().format("yyyy-MM-dd HH:mm:ss", TimeZone.getTimeZone("Asia/Shanghai"))
  def Product = env.PRODUCT
  def Site = env.Site ?: env.site
  def JobName = env.JOB_BASE_NAME
  def GitBranch = env.BRANCH ?: env.branch
  def CommitId = env.COMMIT_ID ?: env.commitid
  def TriggerUser = env.buildUser ?: "Unknown"
  def BuildNumber = env.BUILD_NUMBER
  def BuildUrl = env.BUILD_URL
  def BuildFunc = env.BUILD_FUNC ?: "frontend"

  try {
    def BuildInfo = readFile("build_info.txt")
      TriggerUser = BuildInfo.split('Commit ID:')[0].split('触发者:')[1].trim()
  } catch (ignored) {
  }
  def StatusEmoji = status == 'SUCCESS' ? '✅' : (status == 'FAILURE' ? '❌' : '🚀')
  def StatusText = ""
  switch (status) {
    case 'SUCCESS':
      StatusText = '前端编译与部署作业顺利完成'
      break
    case 'FAILURE':
      StatusText = '前端部署作业失败'
      break
    case 'START':
      StatusText = '开始编译前端代码'
      break
    default:
      StatusText = '⚠️  前端编译代码状态未知'
      break
  }

  def Pattern = /[a-f0-9]{6}/
  if ((CommitId == '' || CommitId == null) && (status != 'SUCCESS')) {
      CommitId = "无法取得commitId"
      StatusText = "前端编译代码中止"
  } else if (!(CommitId ==~ Pattern)) {
      CommitId = "${env.COMMIT_ID}-格式或字数错误"
  }

  if (BuildFunc == "backend") {
    StatusText = StatusText.replace("前端", "后端")
  }

  if (Site ==~ /.*--.*/ || Site ==~ /.*[\u4e00-\u9fa5]+.*/) {
    Site = "未知站點"
  }

  def message = ""
  if (BuildFunc == "backend") {
    message = """${StatusEmoji} ${StatusText}
📅 時間: ${time}
🏷️ 产品: ${Product}
🏷️ 任务: ${JobName}
🎯 站点: ${Site}
🔀 分支: ${GitBranch}
🆔 Commit: ${CommitId}
👤 触发: ${TriggerUser}
🔢 编号: ${BuildNumber}
🐳 image_tag：${Site}-${GitBranch}-${CommitId}
🌐 Jenkins URL: ${BuildUrl}console
"""
  } else {
    message = """${StatusEmoji} ${StatusText}
📅 時間: ${time}
🏷️ 产品: ${Product}
🏷️ 任务: ${JobName}
🎯 站点: ${Site}
🔀 分支: ${GitBranch}
🆔 Commit: ${CommitId}
👤 触发: ${TriggerUser}
🔢 编号: ${BuildNumber}
🌐 Jenkins URL: ${BuildUrl}console
"""
  }

  echo message

  sh """
    { set +x; } 2>/dev/null
    curl -s -X POST https://api.telegram.org/bot${Token}/sendMessage \
      -d chat_id=${ChatId} \
      -d text="${message}" > /dev/null
  """
}

return this
