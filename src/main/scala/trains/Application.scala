package trains

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import trains.Common.flattenOptionsSet
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
    val stationsFilePath = "src/main/resources/string/stations.txt"
    val trainsFilePath = "src/main/resources/string/trains.txt"

    for {
      roads <- reader.readFile(roadsFilePath, roadFromString)
      stations <- reader.readFile(stationsFilePath, stationFromString)
      trains <- reader.readFile(trainsFilePath, trainFromString)
      roadSet = flattenOptionsSet[Road](roads)(roadsFilePath)
      stationSet = flattenOptionsSet[Station](stations)(stationsFilePath)
      trainSet = flattenOptionsSet[Train](trains)(trainsFilePath)
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
              isCrash(schedule) match {
                case Right(b) =>
                  if (b) s"Crash points:\n${crashesSchedule(schedule).getOrElse(Set.empty).mkString("\n")}"
                  else "There were no crashes"
                case Left(err) => err.message
              }
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
          List(roadsFileError, stationsFileError, trainsFileError).mkString("\n")
      }
      _ <- WriterToFile[IO](writeResultPath).writeResult(fs2.Stream(result))
    } yield ExitCode.Success

  }
}
