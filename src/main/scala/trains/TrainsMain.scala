package trains

object TrainsMain {
  implicit val roadsMatrix: Array[Array[Int]] = Array.empty // Filled
  val trains: List[Train] = Nil

  def trainToStationsMap(t: Train)(implicit roadsMatrix: Array[Array[Int]]): Map[(Station, TimeStamp), List[Train]] = {
    @annotation.tailrec
    def help(s: List[Station], timestamp: TimeStamp, acc: Map[(Station, TimeStamp), List[Train]]): Map[(Station, TimeStamp), List[Train]] = {
      s match {
        case Nil | _ :: Nil => acc
        case cur :: next :: tail =>
          val trainsListNow = acc.getOrElse((cur, timestamp), Nil)
          help(next :: tail, timestamp + (roadsMatrix(cur.id)(next.id) / t.speed), acc.updated((cur, timestamp), trainsListNow :+ t))
      }
    }
    help(t.route, 0, Map.empty)
  }

  def mergeTwoMaps(a: Map[(Station, TimeStamp), List[Train]], b: Map[(Station, TimeStamp), List[Train]]): Map[(Station, TimeStamp), List[Train]] =
    (a.keySet ++ b.keySet).map(k => k -> a.getOrElse(k, Nil).++(b.getOrElse(k, Nil))).toMap

  val map: Map[(Station, TimeStamp), List[Train]] = trains.map(trainToStationsMap).foldLeft(Map.empty[(Station, TimeStamp), List[Train]])((a, b) => mergeTwoMaps(a, b))

}
