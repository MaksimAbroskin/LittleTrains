package trains.data_input

trait DataReader[F[_]] {
  def readFile[A, B](path: String, decode: String => Option[B]): F[List[Option[B]]]
}
