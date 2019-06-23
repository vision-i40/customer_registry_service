package infrastructure.pubsub

import java.io.FileInputStream
import java.util.concurrent.TimeUnit

import com.google.api.core.ApiFuture
import com.google.api.gax.core.{CredentialsProvider, FixedCredentialsProvider, NoCredentialsProvider}
import com.google.api.gax.grpc.GrpcTransportChannel
import com.google.api.gax.rpc.FixedTransportChannelProvider
import com.google.auth.oauth2.GoogleCredentials
import com.google.cloud.pubsub.v1.{Publisher, TopicAdminClient, TopicAdminSettings}
import com.google.inject.{Inject, Singleton}
import com.google.protobuf.ByteString
import com.google.pubsub.v1.{ProjectTopicName, PubsubMessage, Topic}
import infrastructure.config.PubSubConfig
import io.grpc.ManagedChannelBuilder
import sun.plugin.dom.exception.InvalidStateException

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class PubSubPublisher @Inject()(implicit ec: ExecutionContext) {

  def publish(message: String)
             (implicit config: PubSubConfig): Future[String] = {
    val result = pub(message)
    result.onComplete( _ => channel.shutdown())
    result
  }

  private def pub(message: String)
                 (implicit config: PubSubConfig): Future[String] = {
    Future {
      Publisher
        .newBuilder(topicName)
        .setChannelProvider(channelProvider)
        .setCredentialsProvider(credentialsProvider)
        .build
        .publish(buildMessage(message))
        .get
    }
  }

  private def buildMessage(message: String): PubsubMessage = {
    val data = ByteString.copyFromUtf8(message)
    PubsubMessage.newBuilder.setData(data).build
  }

  def setupTopic(implicit config: PubSubConfig): Topic = {
    val topicClient = TopicAdminClient
      .create(
        TopicAdminSettings
          .newBuilder
          .setTransportChannelProvider(channelProvider)
          .setCredentialsProvider(credentialsProvider).build)

    topicClient.createTopic(topicName)
  }

  private def topicName(implicit config: PubSubConfig): ProjectTopicName = {
    ProjectTopicName.of(config.projectId, config.userEventsTopic)
  }

  private def channelProvider(implicit config: PubSubConfig): FixedTransportChannelProvider = {
    FixedTransportChannelProvider.create(channel)
  }

  private def channel(implicit config: PubSubConfig): GrpcTransportChannel = {
    if(config.isLocal) {
      GrpcTransportChannel.create(ManagedChannelBuilder.forTarget(config.hostPort).usePlaintext.build)
    } else {
      GrpcTransportChannel.newBuilder().build()
    }
  }

  private def credentialsProvider(implicit config: PubSubConfig): CredentialsProvider = {
    if(config.isLocal) {
      NoCredentialsProvider.create
    } else {
      val credentials = config
            .credentialsFile
            .map {filePath => GoogleCredentials.fromStream(new FileInputStream(filePath))}
            .getOrElse {
              throw new InvalidStateException("Credential file not set")
            }

      FixedCredentialsProvider.create(credentials)
    }
  }
}
