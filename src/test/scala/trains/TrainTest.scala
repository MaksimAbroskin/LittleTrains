package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.ErrorMessage.NoSuchRoadErrorMessage
import trains.Train._

import scala.collection.immutable.HashMap

class TrainTest extends AnyFlatSpec with Matchers {
  "trainFromString method" should "return Some(Train) for correct string" in {
    val s = " name:   t1  ;speed: 1; route: s1, s2, s3"
    trainFromString(s) shouldBe Some(Train("t1", 1, List("s1", "s2", "s3")))
  }

  "" should "return None for incorrect string" in {
    val s = "name:tr1;speed:6,route:s1 ; s2, s3"
    trainFromString(s) shouldBe None
  }

  val roadsMap: RoadsMap = HashMap(
    Set("s1", "s3") -> 7,
    Set("s1", "s4") -> 2,
    Set("s2", "s3") -> 5,
    Set("s2", "s4") -> 4,
    Set("s3", "s4") -> 2
  )

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
