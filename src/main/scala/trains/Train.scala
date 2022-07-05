package trains

import trains.ErrorMessage.{FileParsingErrorMessage, NoSuchRoadErrorMessage}

case class Train(name: String, speed: Int, route: List[String])

object Train {
  def trainFromString(s: String): Option[Train] = {
    val regex = "name:(.+);speed:(\\d+);route:(.+)".r
    s.filterNot(_ == ' ') match {
      case regex(name, speed, route) => Some(Train(name, speed.toInt, route.split(",").toList))
      case _ => None
    }
  }

  def flattenTrainSet(l: List[Option[Train]]): Either[ErrorMessage, Set[Train]] = {
    if (l.contains(None)) Left(FileParsingErrorMessage("Trains file has an error"))
    else Right(l.toSet.flatten)
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
