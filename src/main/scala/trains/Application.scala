package trains

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import io.circe.jawn.decode
import trains.Logic.{crashesSchedule, isCrash, mergeTwoSchedules, trainToSchedule}
import trains.Models.{RoadsFileNote, Station, Train}
import trains.data_input.JsonDataReader
import trains.result.WriterToFile

object Application extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val blocker: Blocker = Blocker.liftExecutionContext(executionContext)
    val emptyMap = Map.empty[(Station, TimeStamp), Set[Train]]

    val reader = JsonDataReader[IO]
    val writeResultPath = "src/main/resources/results/result.json"
    val roadsFilePath = "src/main/resources/roads.json"
    val trainsRoutesFilePath = "src/main/resources/trainsRoutes.json"

    for {
      roads <- reader.readFile(roadsFilePath, decode[List[RoadsFileNote]], RoadsFileNote.toRoadsMatrix)
      trains <- reader.readFile(trainsRoutesFilePath, decode[List[Train]], List[Train])
      result = (roads, trains) match {
        case (Right(matrix), Right(trains)) =>
          val schedule = trains.map(trainToSchedule(_)(matrix))
          val commonSchedule = if (!schedule.exists(_.isLeft)) {
            Right(schedule.map(_.getOrElse(emptyMap)).foldLeft(emptyMap)(mergeTwoSchedules))
          } else {
            Left(schedule.filter(_.isLeft).flatMap {
              case Left(v) => Some(v)
              case Right(_) => None
            })
          }
          commonSchedule match {
            case Left(err) => err.mkString(", ")
            case Right(schedule) =>
              if (isCrash(schedule)) s"Crash points:\n${crashesSchedule(schedule)}"
              else "There were no crashes"
          }
        case (Left(mErr), Left(tErr)) => s"Roads file has error:\n\t${mErr.message}\n\nTrains file has error:\n\t${tErr.message}"
        case (Right(_), Left(tErr)) => s"Trains file has error:\n\t${tErr.message}"
        case (Left(mErr), Right(_)) => s"Roads file has error:\n\t${mErr.message}"
      }
      _ <- WriterToFile[IO](writeResultPath).writeResult(fs2.Stream(result))
    } yield ExitCode.Success

  }
}
