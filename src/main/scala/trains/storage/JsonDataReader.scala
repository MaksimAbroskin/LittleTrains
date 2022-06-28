package trains.storage

import cats.effect.{Blocker, ContextShift, Sync}
import cats.implicits.toFunctorOps
import fs2.{text, io => fs2io}
import io.circe.parser.decode
import trains.Models._

import java.nio.file.Paths

case class JsonDataReader[F[_]: Sync : ContextShift]() extends DataReader[F] {

  def readRoadsFile(path: String)(implicit blocker: Blocker): F[Either[ErrorMessage, List[List[Int]]]] = {
    fs2io.file.readAll(Paths.get(path), blocker, 4096)
      .through(text.utf8Decode)
      .map {
        decode[List[RoadsFileNote]](_) match {
          case Left(err) => Left(FileParsingErrorMessage(err.getMessage))
          case Right(note) => RoadsFileNote.toRoadsMatrix(note)
        }
      }
      .compile
      .toList
      .map(_.headOption.getOrElse(Left(FileParsingErrorMessage("")))) // ToDo Maybe another error?
  }

  override def readTrainRoutesFile(path: String)(implicit blocker: Blocker): F[Either[ErrorMessage, List[Train]]] = {
    fs2io.file.readAll(Paths.get(path), blocker, 4096)
      .through(text.utf8Decode)
      .map {
        decode[List[Train]](_) match {
          case Left(err) => Left(FileParsingErrorMessage(err.getMessage))
          case Right(note) => Right(note)
        }
      }
      .compile
      .toList
      .map(_.headOption.getOrElse(Left(FileParsingErrorMessage("")))) // ToDo Maybe another error?
  }
}

