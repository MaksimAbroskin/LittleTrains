package trains

case class Station(name: String, capacity: Int)

object Station {
  private val regex = "name:(.+);capacity:(\\d+)".r

  def stationFromString(s: String): Option[Station] = {
    s.filterNot(_ == ' ') match {
      case regex(n, c) => Some(Station(n, c.toInt))
      case _ => None
    }
  }

  def stationSetToMap(set: Set[Station]): Map[String, Int] = set.map(s => s.name -> s.capacity).toMap

}
