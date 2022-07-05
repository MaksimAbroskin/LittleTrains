package trains

import trains.ErrorMessage.FileParsingErrorMessage

case class Station(name: String, capacity: Int)

object Station {
  def stationFromString(s: String): Option[Station] = {
    val regex = "name:(.+);capacity:(\\d+)".r
    s.filterNot(_ == ' ') match {
      case regex(n, c) => Some(Station(n, c.toInt))
      case _ => None
    }
  }

  def flattenStationSet(l: List[Option[Station]]): Either[ErrorMessage, Set[Station]] = {
    if (l.contains(None)) Left(FileParsingErrorMessage("Stations file has an error"))
    else Right(l.toSet.flatten)
  }

  def stationSetToMap(set: Set[Station]): Map[String, Int] = set.map(s => s.name -> s.capacity).toMap

}
