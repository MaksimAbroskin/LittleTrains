package trains

object TrainsMain {
  implicit val roadsMatrix: Array[Array[Int]] = Array.empty
  val trains: List[Train] = Nil

  def trainToStationsMap(t: Train)(implicit roadsMatrix: Array[Array[Int]]): Map[(Station, TimeStamp), Set[Train]] = {
    @annotation.tailrec
    def help(s: List[Station], timestamp: TimeStamp, acc: Map[(Station, TimeStamp), Set[Train]]): Map[(Station, TimeStamp), Set[Train]] = {
      s match {
        case Nil => acc
        case s :: Nil => acc.updated((s, timestamp), acc.getOrElse((s, timestamp), Set.empty) + t)
        case cur :: next :: tail =>
          val trainsListNow = acc.getOrElse((cur, timestamp), Set.empty)
          help(next :: tail, timestamp + (roadsMatrix(cur.id - 1)(next.id - 1) / t.speed), acc.updated((cur, timestamp), trainsListNow + t))
      }
    }
    help(t.route, 0, Map.empty)
  }

  def mergeTwoMaps(a: Map[(Station, TimeStamp), Set[Train]], b: Map[(Station, TimeStamp), Set[Train]]): Map[(Station, TimeStamp), Set[Train]] =
    (a.keySet ++ b.keySet).map(k => k -> a.getOrElse(k, Set.empty).++(b.getOrElse(k, Set.empty))).toMap

  val map: Map[(Station, TimeStamp), Set[Train]] = trains.map(trainToStationsMap).foldLeft(Map.empty[(Station, TimeStamp), Set[Train]])(mergeTwoMaps)

  def isCrash(m: Map[(Station, TimeStamp), Set[Train]]): Boolean = {
    m.exists(x => x._1._1.capacity < x._2.size )
  }

  def mapCrashes(m: Map[(Station, TimeStamp), Set[Train]]): Map[(Station, TimeStamp), Set[Train]] = {
    m.filter(x => x._1._1.capacity < x._2.size)
  }

}
