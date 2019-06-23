package infrastructure.config

import com.google.inject.Singleton

import scala.util.Try

@Singleton
class PubSubConfig extends Configuration {
  private val ROOT = "pubsub"
  private val IS_LOCAL = s"$ROOT.is_local"
  private val HOST_PORT_LOCAL = s"$ROOT.host_port_local"
  private val PROJECT_ID = s"$ROOT.project_id"
  private val CREDENTIALS_FILE = s"$ROOT.credentials_file"
  private val USER_EVENTS_TOPIC = s"$ROOT.user_events.topic_name"

  def isLocal: Boolean = config.getBoolean(IS_LOCAL)
  def hostPort: String = config.getString(HOST_PORT_LOCAL)
  def projectId: String = config.getString(PROJECT_ID)
  def credentialsFile: Option[String] = Try{config.getString(CREDENTIALS_FILE)}.toOption
  def userEventsTopic: String = config.getString(USER_EVENTS_TOPIC)
}


