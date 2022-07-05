package trains.data_input

import cats.effect.{Blocker, ContextShift, Sync}
import fs2.{text, io}

import java.nio.file.Paths

case class StringDataReader[F[_] : Sync : ContextShift]()(implicit blocker: Blocker) extends DataReader[F] {
  override def readFile[A, B](path: String, decode: String => Option[B]): F[List[Option[B]]] = {
    io.file.readAll(Paths.get(path), blocker, 1024)
      .through(text.utf8Decode)
      .through(text.lines)
      .map(decode)
      .compile
      .toList
  }
}
