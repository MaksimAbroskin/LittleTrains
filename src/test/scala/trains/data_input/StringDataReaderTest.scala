package trains.data_input

import cats.effect._
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.Road.roadFromString
import trains.Station.stationFromString
import trains.Train.trainFromString
import trains.{Road, Station, Train}

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

class StringDataReaderTest extends AnyFlatSpec with Matchers {
  val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val blocker: Blocker = Blocker.liftExecutionContext(ec)

  val reader: StringDataReader[IO] = StringDataReader[IO]

  "method readFile" should "return empty List for empty file" in {
    val path = "src/test/resources/data_input/empty_file.txt"
    reader.readFile(path, roadFromString).unsafeRunSync() shouldBe List()
  }

  "" should "return List(_) for valid file with roads" in {
    val path = "src/test/resources/data_input/valid_roads_file.txt"
    reader.readFile(path, roadFromString).unsafeRunSync() shouldBe List(
      Road("s1", "s2", 2),
      Road("s2", "s3", 4),
      Road("s3", "s4", 6)
    ).map(Some(_))
  }

  "" should "return List(_) for valid file with trains routes" in {
    val path = "src/test/resources/data_input/valid_trains_file.txt"
    reader.readFile(path, trainFromString).unsafeRunSync() shouldBe List(
      Train("t1", 1, List("s1", "s3", "s2")),
      Train("t2", 1, List("s2", "s1", "s3"))
    ).map(Some(_))
  }

  "" should "return List(_) for valid file with stations" in {
    val path = "src/test/resources/data_input/valid_stations_file.txt"
    reader.readFile(path, stationFromString).unsafeRunSync() shouldBe List(
      Station("s1", 2),
      Station("s2", 1),
      Station("s3", 1)
    ).map(Some(_))
  }

}
