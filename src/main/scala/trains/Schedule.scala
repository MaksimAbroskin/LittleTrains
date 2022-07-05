package trains

case class Schedule(stationName: String, timeStamp: TimeStamp, trains: Set[String])

object Schedule {

  def stationsScheduleToCommonSchedule(set: Set[Schedule]): Set[Schedule] = {
    set.groupBy(schedule => (schedule.stationName, schedule.timeStamp)).map {
      case (k, v) => Schedule(k._1, k._2, v.flatMap(_.trains))
    }.toSet
  }

  def isCrash(set: Set[Schedule])(implicit stations: Map[String, Int]): Boolean =
    set.exists(schedule => schedule.trains.size > stations.getOrElse(schedule.stationName, 0))

  def crashesSchedule(set: Set[Schedule])(implicit stations: Map[String, Int]): Set[Schedule] =
    set.filter(schedule => schedule.trains.size > stations.getOrElse(schedule.stationName, 0))
}
