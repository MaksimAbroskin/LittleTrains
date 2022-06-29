package trains

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import io.circe.jawn.decode
import trains.Models.{RoadsFileNote, Station, Train}
import trains.Logic.{isCrash, mapCrashes, mergeTwoMaps, trainToStationsMap}
import trains.data_input.JsonDataReader
import trains.result.WriterToFile

object Application extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val blocker: Blocker = Blocker.liftExecutionContext(executionContext)

    val reader = JsonDataReader[IO]
    val writer = WriterToFile[IO]("src/main/resources/results/result.json")
    val roadsFilePath = "src/main/resources/roads.json"
    val trainsRoutesFilePath = "src/main/resources/trainsRoutes.json"

    for {
      roads <- reader.readFile(roadsFilePath, decode[List[RoadsFileNote]], RoadsFileNote.toRoadsMatrix)
      trains <- reader.readFile(trainsRoutesFilePath, decode[List[Train]], List[Train])
      r = (roads, trains) match {
        case (Right(matrix), Right(trains)) =>
          val schedule = trains.map(trainToStationsMap(_)(matrix))
          val commomSchedule = if (!schedule.exists(_.isLeft)) {
            Right(schedule.map(_.getOrElse(Map.empty[(Station, TimeStamp), Set[Train]])).foldLeft(Map.empty[(Station, TimeStamp), Set[Train]])(mergeTwoMaps))
          } else {
            Left(schedule.filter(_.isLeft).flatMap {
              case Left(v) => Some(v)
              case Right(_) => None
            })
          }
          commomSchedule match {
            case Left(err) => err.mkString(", ")
            case Right(value) =>
              if (isCrash(value)) s"Crash points:\n${mapCrashes(value)}"
              else "There were no crashes"
          }
        case (Left(mErr), Left(tErr)) => s"Roads file has error:\n\t${mErr.message}\n\nTrains file has error:\n\t${tErr.message}"
        case (Right(_), Left(tErr)) => s"Trains file has error:\n\t${tErr.message}"
        case (Left(mErr), Right(_)) => s"Roads file has error:\n\t${mErr.message}"
      }
      _ <- writer.writeResult(fs2.Stream(r))
    } yield ExitCode.Success

  }
}
