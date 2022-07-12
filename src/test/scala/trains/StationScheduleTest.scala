package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.StationSchedule._

import scala.collection.immutable.HashMap

class StationScheduleTest extends AnyFlatSpec with Matchers {

  "stationsScheduleToCommonSchedule method" should "work correctly for empty set" in {
    stationsScheduleToCommonSchedule(Set.empty) shouldBe Set.empty
  }

  "" should "work for sets without intersections" in {
    val s1 = Set(
      StationSchedule("s1", 0, Set("t1")),
      StationSchedule("s4", 2, Set("t1")),
      StationSchedule("s3", 4, Set("t1")),
      StationSchedule("s2", 9, Set("t1"))
    )
    val s2 = Set(
      StationSchedule("s2", 0, Set("t2")),
      StationSchedule("s3", 5, Set("t2")),
      StationSchedule("s2", 10, Set("t2"))
    )
    stationsScheduleToCommonSchedule(s1 ++ s2) shouldBe Set(
      StationSchedule("s1", 0, Set("t1")),
      StationSchedule("s4", 2, Set("t1")),
      StationSchedule("s3", 4, Set("t1")),
      StationSchedule("s2", 9, Set("t1")),
      StationSchedule("s2", 0, Set("t2")),
      StationSchedule("s3", 5, Set("t2")),
      StationSchedule("s2", 10, Set("t2"))
    )
  }

  "" should "work for sets with intersections" in {
    val s1 = Set(
      StationSchedule("s2", 0, Set("t1")),
      StationSchedule("s4", 4, Set("t1")),
      StationSchedule("s3", 6, Set("t1")),
      StationSchedule("s1", 13, Set("t1"))
    )
    val s2 = Set(
      StationSchedule("s2", 0, Set("t2")),
      StationSchedule("s3", 5, Set("t2")),
      StationSchedule("s2", 10, Set("t2")),
      StationSchedule("s1", 13, Set("t2"))
    )
    stationsScheduleToCommonSchedule(s1 ++ s2) shouldBe Set(
      StationSchedule("s4", 4, Set("t1")),
      StationSchedule("s3", 6, Set("t1")),
      StationSchedule("s1", 13, Set("t1", "t2")),
      StationSchedule("s2", 0, Set("t2", "t1")),
      StationSchedule("s3", 5, Set("t2")),
      StationSchedule("s2", 10, Set("t2"))
    )
  }

  val stations: HashMap[String, Int] = HashMap(
    "s1" -> 1,
    "s2" -> 1,
    "s3" -> 1,
    "s4" -> 1,
  )

  "isCrash and crashesSchedule" should "be false and empty for empty set" in {
    crashesOnStationsSchedule(Set.empty)(stations) shouldBe Set.empty
  }

  "" should "be false and empty if no crashes" in {
    val s = Set(
      StationSchedule("s1", 0, Set("t1")),
      StationSchedule("s4", 2, Set("t1")),
      StationSchedule("s3", 4, Set("t1")),
      StationSchedule("s2", 9, Set("t1")),
      StationSchedule("s2", 0, Set("t2")),
      StationSchedule("s3", 5, Set("t2")),
      StationSchedule("s2", 10, Set("t2"))
    )

    crashesOnStationsSchedule(s)(stations) shouldBe Set.empty
  }

  "" should "be true and corresponding set if there are crashes" in {
    val stations: HashMap[String, Int] = HashMap(
      "s1" -> 1,
      "s2" -> 2,
      "s3" -> 1,
      "s4" -> 1,
    )
    val m = Set(
      StationSchedule("s4", 4, Set("t1")),
      StationSchedule("s3", 6, Set("t1")),
      StationSchedule("s1", 13, Set("t1", "t2")),
      StationSchedule("s2", 0, Set("t2", "t1")),
      StationSchedule("s3", 5, Set("t2")),
      StationSchedule("s2", 10, Set("t2"))
    )

    crashesOnStationsSchedule(m)(stations) shouldBe Set(StationSchedule("s1", 13, Set("t1", "t2")))
  }

  "" should "be true and corresponding set if there are crash in start Station" in {
    val stations: HashMap[String, Int] = HashMap(
      "s1" -> 2,
      "s2" -> 1,
      "s3" -> 1,
      "s4" -> 1,
    )
    val m = Set(
      StationSchedule("s4", 4, Set("t1")),
      StationSchedule("s3", 6, Set("t1")),
      StationSchedule("s1", 13, Set("t1", "t2")),
      StationSchedule("s2", 0, Set("t2", "t1")),
      StationSchedule("s3", 5, Set("t2")),
      StationSchedule("s2", 10, Set("t2"))
    )

    crashesOnStationsSchedule(m)(stations) shouldBe Set(StationSchedule("s2", 0, Set("t1", "t2")))
  }

}
