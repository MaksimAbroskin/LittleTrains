package trains

sealed trait ErrorMessage {
  val message: String
}

object ErrorMessage {
  case class FileParsingErrorMessage(fileName: String) extends ErrorMessage {
    override val message: String = s"Failed parsing of file $fileName"
  }

  case class NoSuchRoadErrorMessage(s1: String, s2: String) extends ErrorMessage {
    override val message: String = s"No road between stations with ids = $s1 and $s2"
  }

  case class NoSuchStationErrorMessage(s: String) extends ErrorMessage {
    override val message: String = s"No data about station $s in the stations file"
  }
}


