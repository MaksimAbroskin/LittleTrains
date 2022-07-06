package trains

import trains.ErrorMessage.NoSuchRoadErrorMessage

case class Train(name: String, speed: Int, route: List[String])

object Train {
  private val regex = "name:(.+);speed:(\\d+);route:(.+)".r

  def trainFromString(s: String): Option[Train] = {
    s.filterNot(_ == ' ') match {
      case regex(name, speed, route) => Some(Train(name, speed.toInt, route.split(",").toList))
      case _ => None
    }
  }

  def trainToSchedule(t: Train)(roadsMap: RoadsMap): Either[ErrorMessage, Set[Schedule]] = {
    @annotation.tailrec
    def go(route: List[String], timestamp: TimeStamp, acc: Set[Schedule]): Either[ErrorMessage, Set[Schedule]] = {
      route match {
        case Nil => Right(acc)
        case s :: Nil => Right(acc + Schedule(s, timestamp, Set(t.name)))
        case cur :: next :: tail =>
          roadsMap.get(Set(cur, next)) match {
            case Some(distance) => go(next :: tail, timestamp + (distance / t.speed), acc + Schedule(cur, timestamp, Set(t.name)))
            case None => Left(NoSuchRoadErrorMessage(cur, next))
          }
      }
    }

    go(t.route, 0, Set.empty)
  }
}
