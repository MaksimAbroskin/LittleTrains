package trains.storage

import cats.effect.Blocker
import trains.Models.{ErrorMessage, Train}

trait DataReader[F[_]] {
  def readRoadsFile(path: String)(implicit blocker: Blocker): F[Either[ErrorMessage, List[List[Int]]]]
  def readTrainRoutesFile(path: String)(implicit blocker: Blocker): F[Either[ErrorMessage, List[Train]]]
}
