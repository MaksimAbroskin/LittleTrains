package trains.data_input

import cats.effect.{Blocker, ContextShift, Sync}
import cats.implicits.toFunctorOps
import fs2.{text, io => fs2io}
import trains.Models._

import java.nio.file.Paths

case class JsonDataReader[F[_]: Sync : ContextShift]() extends DataReader[F] {

  override def readFile[G, A](path: String, decode: String => Either[io.circe.Error, G], convert: G => A)(implicit blocker: Blocker): F[Either[ErrorMessage, A]] = {
    fs2io.file.readAll(Paths.get(path), blocker, 4096)
      .through(text.utf8Decode)
      .map {
        decode(_) match {
          case Left(_) => Left(FileParsingErrorMessage())
          case Right(note) => Right(convert(note))
        }
      }
      .compile
      .toList
      .map(_.headOption.getOrElse(Left(EmptyFileErrorMessage(path))))
  }
}

