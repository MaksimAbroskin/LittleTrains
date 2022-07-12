package trains

import scala.collection.immutable.HashMap

case class StationSchedule(stationName: String, timeStamp: TimeStamp, trains: Set[String]) {
  override def toString: String =
    s"station_name: $stationName; time: $timeStamp; trains: ${trains.mkString(", ")}"
}

object StationSchedule {

  def stationsScheduleToCommonSchedule(set: Set[StationSchedule]): Set[StationSchedule] = {
    set.groupBy(schedule => (schedule.stationName, schedule.timeStamp)).map {
      case (k, v) => StationSchedule(k._1, k._2, v.flatMap(_.trains))
    }.toSet
  }

  def crashesOnStationsSchedule(set: Set[StationSchedule])(stations: HashMap[String, Int]): Set[StationSchedule] =
    set.filter(schedule => schedule.trains.size > stations.getOrElse(schedule.stationName, 0))
}
