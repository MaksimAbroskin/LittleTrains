package trains

import trains.ErrorMessage.FileParsingErrorMessage

case class Road(from: String, to: String, distance: Int)

object Road {
  def roadFromString(s: String): Option[Road] = {
    val regex = "from:(.+);to:(.+);distance:(\\d+)".r
    s.filterNot(_ == ' ') match {
      case regex(from, to, dist) => Some(Road(from, to, dist.toInt))
      case _ => None
    }
  }

  def flattenRoadSet(l: List[Option[Road]]): Either[ErrorMessage, Set[Road]] = {
    if (l.contains(None)) Left(FileParsingErrorMessage("Roads file has an error"))
    else Right(l.toSet.flatten)
  }

  def roadSetToMap(set: Set[Road]): RoadsMap = set.map(r => Set(r.from, r.to) -> r.distance).toMap

}
