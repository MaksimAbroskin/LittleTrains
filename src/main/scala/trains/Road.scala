package trains

case class Road(from: String, to: String, distance: Int)

object Road {
  private val regex = "from:(.+);to:(.+);distance:(\\d+)".r

  def roadFromString(s: String): Option[Road] = {
    s.filterNot(_ == ' ') match {
      case regex(from, to, dist) => Some(Road(from, to, dist.toInt))
      case _ => None
    }
  }

  def roadSetToMap(set: Set[Road]): RoadsMap = set.map(r => Set(r.from, r.to) -> r.distance).toMap

}
