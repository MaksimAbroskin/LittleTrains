package trains.result

import cats.effect.Blocker

trait ResultWriter[F[_]] {
  def writeResult(result: fs2.Stream[F, String])(implicit blocker: Blocker): F[Unit]
}
