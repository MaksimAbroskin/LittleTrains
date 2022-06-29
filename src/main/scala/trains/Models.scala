package trains

import io.circe.{Decoder, HCursor}
import io.circe.generic.semiauto.deriveDecoder

object Models {
  case class Station (id: Int, capacity: Int)

  object Station {
    implicit val decoder: Decoder[Station] =
      (c: HCursor) => {
        for {
          id <- c.downField("station_name").as[Int]
          capacity <- c.downField("station_capacity").as[Int]
        } yield Station(id, capacity)
      }
  }

  case class Train (id: Int, route: List[Station], speed: Int = 1)

  object Train {
    implicit val decoder: Decoder[Train] =
      (c: HCursor) => {
        for {
          id <- c.downField("train").as[Int]
          speed <- c.downField("speed").as[Int]
          route <- c.downField("route").as[List[Station]]
        } yield Train(id, route, speed)
      }
  }

  case class RoadsFileNote(from: Station, to: Station, distance: Int)

  object RoadsFileNote {
    implicit val decoder: Decoder[RoadsFileNote] = deriveDecoder

    def toRoadsMatrix(notes: List[RoadsFileNote]): List[List[Int]] = {
      @annotation.tailrec
      def help(notes: List[RoadsFileNote], acc: List[List[Int]]): List[List[Int]] = {
        notes match {
          case Nil => acc
          case n :: tail =>
            val fromS = n.from.id - 1
            val toS = n.to.id - 1
            help(tail, acc.updated(fromS, acc(fromS).updated(toS, n.distance)).updated(toS, acc(toS).updated(fromS, n.distance)))
        }
      }
      val numStations = notes.flatMap(n => List(n.from, n.to)).toSet.size
      help(notes, List.fill(numStations)(List.fill(numStations)(-1)))
    }
  }

  trait ErrorMessage {
    val message: String
  }

  case class FileParsingErrorMessage() extends ErrorMessage {
    override val message: String = s"Failed to parse invalid json-file"
  }

  case class EmptyFileErrorMessage(fileName: String) extends ErrorMessage {
    override val message: String = s"There is no data in file $fileName"
  }

}
