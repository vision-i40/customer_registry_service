package domain

case class Collector(
  id: String,
  uid: String,
  deviceType: Devices.Value,
  authToken: String,
  channels: List[Channel]
)
