package domain

case class Channel(
  id: String,
  number: Int,
  channelType: ChannelType.Value,
  isCumulative: Boolean
)
