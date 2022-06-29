package trains

import trains.Models.{ErrorMessage, NoSuchRoadErrorMessage, Station, Train}

object Logic {
  def trainToSchedule(t: Train)(roadsMatrix: RoadsMatrix): Either[ErrorMessage, Schedule] = {
    @annotation.tailrec
    def help(s: List[Station], timestamp: TimeStamp, acc: Schedule): Either[ErrorMessage, Schedule] = {
      s match {
        case Nil => Right(acc)
        case s :: Nil => Right(acc.updated((s, timestamp), acc.getOrElse((s, timestamp), Set.empty) + t))
        case cur :: next :: tail =>
          val trainsListNow = acc.getOrElse((cur, timestamp), Set.empty)
          val road = roadsMatrix(cur.id - 1)(next.id - 1)
          if (road == -1) Left(NoSuchRoadErrorMessage(cur, next))
          else help(next :: tail, timestamp + (roadsMatrix(cur.id - 1)(next.id - 1) / t.speed), acc.updated((cur, timestamp), trainsListNow + t))
      }
    }
    help(t.route, 0, Map.empty)
  }

  def mergeTwoSchedules(a: Schedule, b: Schedule): Schedule =
    (a.keySet ++ b.keySet).map(k => k -> a.getOrElse(k, Set.empty).++(b.getOrElse(k, Set.empty))).toMap

  def isCrash(m: Schedule): Boolean = {
    m.exists(x => x._1._1.capacity < x._2.size )
  }

  def crashesSchedule(m: Schedule): Schedule = {
    m.filter(x => x._1._1.capacity < x._2.size)
  }

}
