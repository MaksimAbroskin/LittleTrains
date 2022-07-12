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

  def trainToStationsSchedule(t: Train)(roadsMap: RoadsMap): Set[StationSchedule] = {
    @annotation.tailrec
    def go(route: List[String], timestamp: TimeStamp, acc: Set[StationSchedule]): Set[StationSchedule] = {
      route match {
        case Nil => acc
        case s :: Nil => acc + StationSchedule(s, timestamp, Set(t.name))
        case cur :: next :: tail =>
          val nextTimeStamp = timestamp + (roadsMap.getOrElse((cur, next), 0) / t.speed)
          go(next :: tail, nextTimeStamp, acc + StationSchedule(cur, timestamp, Set(t.name)))
      }
    }

    go(t.route, 0, Set.empty)
  }

  def trainToRailwaySchedule(t: Train)(roadsMap: RoadsMap): Set[RailwaySchedule] = {
    @annotation.tailrec
    def go(route: List[String], timestamp: TimeStamp, acc: Set[RailwaySchedule]): Set[RailwaySchedule] = {
      route match {
        case _ :: Nil | Nil => acc
        case cur :: next :: tail =>
          val nextTimeStamp = timestamp + (roadsMap.getOrElse((cur, next), 0) / t.speed)
          go(next :: tail, nextTimeStamp, acc + RailwaySchedule((cur, next), Set((t.name, (timestamp, nextTimeStamp)))))
      }
    }

    go(t.route, 0, Set.empty)
  }

}
