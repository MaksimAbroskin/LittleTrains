package trains

case class Train(name: String, speed: Int, route: List[String])

object Train {
  private val regex = "name:(.+);speed:(\\d+);route:(.+)".r

  def trainFromString(s: String): Option[Train] = {
    s.filterNot(_ == ' ') match {
      case regex(name, speed, route) => Some(Train(name, speed.toInt, route.split(",").toList))
      case _ => None
    }
  }

  def trainToSchedule(t: Train)(roadsMap: RoadsMap): Set[Schedule] = {
    @annotation.tailrec
    def go(route: List[String], timestamp: TimeStamp, acc: Set[Schedule]): Set[Schedule] = {
      route match {
        case Nil => acc
        case s :: Nil => acc + Schedule(s, timestamp, Set(t.name))
        case cur :: next :: tail =>
          go(next :: tail, timestamp + (roadsMap.getOrElse((cur, next), 0) / t.speed), acc + Schedule(cur, timestamp, Set(t.name)))
      }
    }

    go(t.route, 0, Set.empty)
  }
}
