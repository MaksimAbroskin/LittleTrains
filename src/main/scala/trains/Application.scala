package trains

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import trains.Road._
import trains.Schedule._
import trains.Station._
import trains.Train._
import trains.data_input.StringDataReader
import trains.result.WriterToFile

object Application extends IOApp {
  override def run(args: List[String]): IO[ExitCode] = {

    implicit val blocker: Blocker = Blocker.liftExecutionContext(executionContext)

    val reader = StringDataReader[IO]
    val writeResultPath = "src/main/resources/result.txt"
    val roadsFilePath = "src/main/resources/string/roads.txt"
    val trainsFilePath = "src/main/resources/string/trains.txt"
    val stationsFilePath = "src/main/resources/string/stations.txt"

    for {
      roads <- reader.readFile(roadsFilePath, roadFromString)
      stations <- reader.readFile(stationsFilePath, stationFromString)
      trains <- reader.readFile(trainsFilePath, trainFromString)
      roadSet = flattenRoadSet(roads)
      stationSet = flattenStationSet(stations)
      trainSet = flattenTrainSet(trains)
      result = (roadSet, stationSet, trainSet) match {
        case (Right(r), Right(s), Right(t)) =>
          implicit val stationsInfo: Map[String, Int] = stationSetToMap(s)
          val roadsInfo = roadSetToMap(r)
          val schedule = t.map(trainToSchedule(_)(roadsInfo))
          val commonSchedule = if (!schedule.exists(_.isLeft)) {
            Right(stationsScheduleToCommonSchedule(schedule.flatMap(_.getOrElse(Set.empty[Schedule]))))
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
        case (r, s, t) =>
          val roadsFileError = r match {
            case Left(e) => e.message
            case Right(_) => ""
          }
          val stationsFileError = s match {
            case Left(e) => e.message
            case Right(_) => ""
          }
          val trainsFileError = t match {
            case Left(e) => e.message
            case Right(_) => ""
          }
          "Error during parsing files:\n" + List(roadsFileError, stationsFileError, trainsFileError).mkString("\n")
      }
      _ <- WriterToFile[IO](writeResultPath).writeResult(fs2.Stream(result))
    } yield ExitCode.Success

  }
}
