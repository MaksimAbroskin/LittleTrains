package trains

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import trains.storage.JsonDataReader

object Application extends IOApp{
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val blocker: Blocker = Blocker.liftExecutionContext(executionContext)

    val reader = JsonDataReader[IO]
    for {
      f1 <- reader.readRoadsFile("src/main/resources/roads.json")
      f2 <- reader.readTrainRoutesFile("src/main/resources/trainsRoutes.json")
//      _ <- IO(println("roads = \n" + f1))
//      _ <- IO(println("\n\nTrains = \n" + f2))
    } yield ExitCode.Success
  }
}
