package trains

import cats.effect.{Blocker, ExitCode, IO, IOApp}
import trains.Common.flattenOptionsSet
import trains.RailwaySchedule._
import trains.Road._
import trains.StationSchedule._
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
          val roadsInfo = roadSetToMap(r)
          Validator(roadsInfo, s, t).errorsList match {
            case Nil =>
              val stationsSchedule = stationsScheduleToCommonSchedule(t.flatMap(trainToStationsSchedule(_)(roadsInfo)))
              val railwaysSchedule = railwayScheduleToCommonSchedule(t.flatMap(trainToRailwaySchedule(_)(roadsInfo)))
              val stationsMap = stationSetToMap(s)
              val crashesOnStations = crashesOnStationsSchedule(stationsSchedule)(stationsMap)
              val railwaysMap = setToMap(railwaysSchedule)
              val crashesOnRailways = collectOppositeDirect(railwaysSchedule.flatMap(crashesOnRailwaysSchedule(_)(railwaysMap)))
              if (crashesOnStations.nonEmpty || crashesOnRailways.nonEmpty)
                s"Crash points:\n${crashesOnStations.mkString("\n")}\n${crashesOnRailways.mkString("\n")}"
              else "There were no crashes"
            case l => l.mkString("\n")
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
