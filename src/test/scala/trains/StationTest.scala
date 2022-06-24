package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import TrainsMain._

class StationTest extends AnyFlatSpec with Matchers {

  private val s1 = Station(1, 1)
  private val s2 = Station(2, 1)
  private val s3 = Station(3, 1)
  private val s4 = Station(4, 1)

  "isCrash and mapCrashes" should "be false and empty for empty map" in {
    isCrash(Map.empty) shouldBe false
    mapCrashes(Map.empty) shouldBe Map.empty
  }

  "" should "be false and empty if no crashes" in {
    val t1 = Train(1, List())
    val t2 = Train(2, List())
    val m = Map(
      (s1, 0) -> Set(t1),
      (s4, 2) -> Set(t1),
      (s3, 4) -> Set(t1),
      (s2, 9) -> Set(t1),
      (s2, 0) -> Set(t2),
      (s3, 5) -> Set(t2),
      (s2, 10) -> Set(t2)
    )

    isCrash(m) shouldBe false
    mapCrashes(m) shouldBe Map.empty
  }

  "" should "be true and corresponding map if there are crashes" in {
    val extendedS2 = s2.copy(capacity = 2)
    val t1 = Train(1, List())
    val t2 = Train(2, List())
    val m = Map(
      (s4, 4) -> Set(t1),
      (s3, 6) -> Set(t1),
      (s1, 13) -> Set(t2, t1),
      (extendedS2, 0) -> Set(t1, t2),
      (s3, 5) -> Set(t2),
      (extendedS2, 10) -> Set(t2)
    )

    isCrash(m) shouldBe true
    mapCrashes(m) shouldBe Map((s1, 13) -> Set(t1, t2))
  }

  "" should "be true and corresponding map if there are crash in start Station" in {
    val extendedS1 = s1.copy(capacity = 2)
    val t1 = Train(1, List())
    val t2 = Train(2, List())
    val m = Map(
      (s4, 4) -> Set(t1),
      (s3, 6) -> Set(t1),
      (extendedS1, 13) -> Set(t2, t1),
      (s2, 0) -> Set(t1, t2),
      (s3, 5) -> Set(t2),
      (s2, 10) -> Set(t2)
    )

    isCrash(m) shouldBe true
    mapCrashes(m) shouldBe Map((s2, 0) -> Set(t1, t2))
  }

}
