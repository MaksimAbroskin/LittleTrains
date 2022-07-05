package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.ErrorMessage.{FileParsingErrorMessage, NoSuchRoadErrorMessage}
import trains.Train._

import scala.collection.immutable.HashMap

class TrainTest extends AnyFlatSpec with Matchers {
  val t1: Train = Train("t1", 1, List("s1", "s2", "s3"))
  val t2: Train = Train("t2", 1, List("s2", "s3", "s2"))

  "trainFromString method" should "return Some(Train) for correct string" in {
    val s = " name:   t1  ;speed: 1; route: s1, s2, s3"
    trainFromString(s) shouldBe Some(t1)
  }

  "" should "return None for incorrect string" in {
    val s = "name:tr1;speed:6,route:s1 ; s2, s3"
    trainFromString(s) shouldBe None
  }

  "flattenTrainSet method" should "return Right for correct list" in {
    flattenTrainSet(List(Some(t1), Some(t2))) shouldBe Right(Set(t2, t1))
  }

  "" should "return Left for list with None" in {
    flattenTrainSet(List(Some(t1), None, Some(t2))) shouldBe Left(FileParsingErrorMessage("Trains file has an error"))
  }

  val roadsMap: RoadsMap = HashMap(
    Set("s1", "s3") -> 7,
    Set("s1", "s4") -> 2,
    Set("s2", "s3") -> 5,
    Set("s2", "s4") -> 4,
    Set("s3", "s4") -> 2
  )

//    private val s1 = Station("s1", 1)
//    private val s2 = Station("s2", 1)
//    private val s3 = Station("s3", 1)
//    private val s4 = Station("s4", 1)

  "trainToSchedule method" should "work correctly for empty route" in {
    trainToSchedule(Train("t1", 1, List()))(roadsMap) shouldBe Right(Set.empty)
  }

  "" should "work correctly for only one station" in {
    val train = Train("t1", 1, List("s2"))
    trainToSchedule(train)(roadsMap) shouldBe Right(Set(Schedule("s2", 0, Set("t1"))))
  }

  "" should "work correctly for repeated station in route" in {
    val train = Train("t1", 1, List("s2", "s3", "s2"))
    trainToSchedule(train)(roadsMap) shouldBe Right(
      Set(
        Schedule("s2", 0, Set("t1")),
        Schedule("s3", 5, Set("t1")),
        Schedule("s2", 10, Set("t1"))
      )
    )
  }

  "" should "convert Train to schedule" in {
    val train = Train("t1", 1, List("s1", "s4", "s3", "s2"))
    trainToSchedule(train)(roadsMap) shouldBe Right(
      Set(
        Schedule("s1", 0, Set("t1")),
        Schedule("s4", 2, Set("t1")),
        Schedule("s3", 4, Set("t1")),
        Schedule("s2", 9, Set("t1"))
      )
    )
  }

  "" should "return NoSuchRoadErrorMessage for non-existing roads in train's route" in {
    val train = Train("t1", 1, List("s1", "s2", "s4", "s3"))
    trainToSchedule(train)(roadsMap) shouldBe Left(NoSuchRoadErrorMessage("s1", "s2"))
  }


}
