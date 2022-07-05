package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.Schedule._

import scala.collection.immutable.HashMap

class ScheduleTest extends AnyFlatSpec with Matchers {

  "stationsScheduleToCommonSchedule method" should "work correctly for empty set" in {
    stationsScheduleToCommonSchedule(Set.empty) shouldBe Set.empty
  }

  "" should "work for sets without intersections" in {
    val s1 = Set(
      Schedule("s1", 0, Set("t1")),
      Schedule("s4", 2, Set("t1")),
      Schedule("s3", 4, Set("t1")),
      Schedule("s2", 9, Set("t1"))
    )
    val s2 = Set(
      Schedule("s2", 0, Set("t2")),
      Schedule("s3", 5, Set("t2")),
      Schedule("s2", 10, Set("t2"))
    )
    stationsScheduleToCommonSchedule(s1 ++ s2) shouldBe Set(
      Schedule("s1", 0, Set("t1")),
      Schedule("s4", 2, Set("t1")),
      Schedule("s3", 4, Set("t1")),
      Schedule("s2", 9, Set("t1")),
      Schedule("s2", 0, Set("t2")),
      Schedule("s3", 5, Set("t2")),
      Schedule("s2", 10, Set("t2"))
    )
  }

  "" should "work for sets with intersections" in {
    val s1 = Set(
      Schedule("s2", 0, Set("t1")),
      Schedule("s4", 4, Set("t1")),
      Schedule("s3", 6, Set("t1")),
      Schedule("s1", 13, Set("t1"))
    )
    val s2 = Set(
      Schedule("s2", 0, Set("t2")),
      Schedule("s3", 5, Set("t2")),
      Schedule("s2", 10, Set("t2")),
      Schedule("s1", 13, Set("t2"))
    )
    stationsScheduleToCommonSchedule(s1 ++ s2) shouldBe Set(
      Schedule("s4", 4, Set("t1")),
      Schedule("s3", 6, Set("t1")),
      Schedule("s1", 13, Set("t1", "t2")),
      Schedule("s2", 0, Set("t2", "t1")),
      Schedule("s3", 5, Set("t2")),
      Schedule("s2", 10, Set("t2"))
    )
  }

  implicit val stations: Map[String, Int] = HashMap(
    "s1" -> 1,
    "s2" -> 1,
    "s3" -> 1,
    "s4" -> 1,
  )

    "isCrash and crashesSchedule" should "be false and empty for empty set" in {
      isCrash(Set.empty) shouldBe false
      crashesSchedule(Set.empty) shouldBe Set.empty
    }

    "" should "be false and empty if no crashes" in {
      val s = Set(
        Schedule("s1", 0, Set("t1")),
        Schedule("s4", 2, Set("t1")),
        Schedule("s3", 4, Set("t1")),
        Schedule("s2", 9, Set("t1")),
        Schedule("s2", 0, Set("t2")),
        Schedule("s3", 5, Set("t2")),
        Schedule("s2", 10, Set("t2"))
      )

      isCrash(s) shouldBe false
      crashesSchedule(s) shouldBe Set.empty
    }

    "" should "be true and corresponding set if there are crashes" in {
      implicit val stations: Map[String, Int] = HashMap(
        "s1" -> 1,
        "s2" -> 2,
        "s3" -> 1,
        "s4" -> 1,
      )
      val m = Set(
        Schedule("s4", 4, Set("t1")),
        Schedule("s3", 6, Set("t1")),
        Schedule("s1", 13, Set("t1", "t2")),
        Schedule("s2", 0, Set("t2", "t1")),
        Schedule("s3", 5, Set("t2")),
        Schedule("s2", 10, Set("t2"))
      )

      isCrash(m) shouldBe true
      crashesSchedule(m) shouldBe Set(
        Schedule("s1", 13, Set("t1", "t2"))
      )
    }

    "" should "be true and corresponding set if there are crash in start Station" in {
      implicit val stations: Map[String, Int] = HashMap(
        "s1" -> 2,
        "s2" -> 1,
        "s3" -> 1,
        "s4" -> 1,
      )
      val m = Set(
        Schedule("s4", 4, Set("t1")),
        Schedule("s3", 6, Set("t1")),
        Schedule("s1", 13, Set("t1", "t2")),
        Schedule("s2", 0, Set("t2", "t1")),
        Schedule("s3", 5, Set("t2")),
        Schedule("s2", 10, Set("t2"))
      )

      isCrash(m) shouldBe true
      crashesSchedule(m) shouldBe Set(
        Schedule("s2", 0, Set("t1", "t2"))
      )
    }

}
