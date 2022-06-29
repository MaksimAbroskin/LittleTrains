package trains

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import io.circe.jawn.decode
import trains.Models.{RoadsFileNote, Station, Train}
import trains.Logic.{isCrash, mapCrashes, mergeTwoMaps, trainToStationsMap}
import trains.data_input.JsonDataReader

object Application extends IOApp{
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val blocker: Blocker = Blocker.liftExecutionContext(executionContext)

    val reader = JsonDataReader[IO]
    val roadsFilePath = "src/main/resources/roads.json"
    val trainsRoutesFilePath = "src/main/resources/trainsRoutes.json"
    for {
      roads <- reader.readFile(roadsFilePath, decode[List[RoadsFileNote]], RoadsFileNote.toRoadsMatrix)
      trains <- reader.readFile(trainsRoutesFilePath, decode[List[Train]], List[Train])
      _ <- (roads, trains) match {
        case (Right(matrix), Right(trains)) =>
          val map = trains.map(trainToStationsMap(_)(matrix)).foldLeft(Map.empty[(Station, TimeStamp), Set[Train]])(mergeTwoMaps)
          val a = mapCrashes(map)
          val x = isCrash(map)
          IO(println(s"map = $a\nisCrash = $x"))
        case _ => IO(println("Error"))
      }
    } yield ExitCode.Success
  }
}
