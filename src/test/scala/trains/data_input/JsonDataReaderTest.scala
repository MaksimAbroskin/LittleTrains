package trains.data_input

import cats.effect._
import io.circe.jawn.decode
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.Models._

import scala.concurrent.{ExecutionContext, ExecutionContextExecutor}

class JsonDataReaderTest extends AnyFlatSpec with Matchers {
  val ec: ExecutionContextExecutor = ExecutionContext.global
  implicit val cs: ContextShift[IO] = IO.contextShift(ec)
  implicit val blocker: Blocker = Blocker.liftExecutionContext(ec)

  val reader: JsonDataReader[IO] = JsonDataReader[IO]

  "method readFile" should "return Left(EmptyFileErrorMessage) for empty file" in {
    val path = "src/test/resources/data_input/empty_file.json"
    reader.readFile(path, decode[List[RoadsFileNote]], RoadsFileNote.toRoadsMatrix).unsafeRunSync() shouldBe Left(EmptyFileErrorMessage(path))
  }

  "" should "return Left(FileParsingErrorMessage) for invalid file" in {
    val path = "src/test/resources/data_input/invalid_file.json"
    reader.readFile(path, decode[List[RoadsFileNote]], RoadsFileNote.toRoadsMatrix).unsafeRunSync() shouldBe Left(FileParsingErrorMessage)
  }

  "" should "return Right() for valid file with roads" in {
    val path = "src/test/resources/data_input/valid_roads_file.json"
    reader.readFile(path, decode[List[RoadsFileNote]], RoadsFileNote.toRoadsMatrix).unsafeRunSync() shouldBe Right(
      List(
        List(-1, 3, 7, 2),
        List(3, -1, 5, 4),
        List(7, 5, -1, 2),
        List(2, 4, 2, -1)
      )
    )
  }

  "" should "return Right() for valid file with trains routes" in {
    val path = "src/test/resources/data_input/valid_trains_routes_file.json"
    reader.readFile(path, decode[List[Train]], List[Train]).unsafeRunSync() shouldBe Right(
      List(
        Train(10, List(Station(1, 1), Station(4, 1), Station(3, 1), Station(2, 1))),
        Train(20, List(Station(2, 1), Station(3, 1), Station(2, 1)))
      )
    )
  }

}
