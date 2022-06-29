package trains

import trains.Models.{ErrorMessage, NoSuchRoadErrorMessage, Station, Train}

object Logic {
  def trainToStationsMap(t: Train)(roadsMatrix: List[List[Int]]): Either[ErrorMessage, Map[(Station, TimeStamp), Set[Train]]] = {
    @annotation.tailrec
    def help(s: List[Station], timestamp: TimeStamp, acc: Map[(Station, TimeStamp), Set[Train]]): Either[ErrorMessage, Map[(Station, TimeStamp), Set[Train]]] = {
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

  def mergeTwoMaps(a: Map[(Station, TimeStamp), Set[Train]], b: Map[(Station, TimeStamp), Set[Train]]): Map[(Station, TimeStamp), Set[Train]] =
    (a.keySet ++ b.keySet).map(k => k -> a.getOrElse(k, Set.empty).++(b.getOrElse(k, Set.empty))).toMap

  def isCrash(m: Map[(Station, TimeStamp), Set[Train]]): Boolean = {
    m.exists(x => x._1._1.capacity < x._2.size )
  }

  def mapCrashes(m: Map[(Station, TimeStamp), Set[Train]]): Map[(Station, TimeStamp), Set[Train]] = {
    m.filter(x => x._1._1.capacity < x._2.size)
  }

}