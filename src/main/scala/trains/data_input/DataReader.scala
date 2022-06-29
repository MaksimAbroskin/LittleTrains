package trains.data_input

import cats.effect.Blocker
import trains.Models.ErrorMessage
import io.circe.Error

trait DataReader[F[_]] {
  def readFile[G, A](path: String, decode: String => Either[Error, G], convert: G => A): F[Either[ErrorMessage, A]]
}
