package trains.result

import cats.effect.{Blocker, ContextShift, Sync}
import fs2._

import java.nio.file.Paths
import java.nio.file.StandardOpenOption._

case class WriterToFile[F[_]: Sync : ContextShift](path: String) extends ResultWriter[F]() {
  override def writeResult(result: Stream[F, String])(implicit blocker: Blocker): F[Unit] = {
    result
      .through(text.utf8Encode)
      .through(io.file.writeAll(Paths.get(path), blocker, Seq(CREATE, TRUNCATE_EXISTING)))
      .compile
      .drain
  }
}
