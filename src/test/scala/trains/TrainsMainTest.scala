package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.Models.{Station, Train}
import trains.TrainsMain.{mergeTwoMaps, trainToStationsMap}

class TrainsMainTest extends AnyFlatSpec with Matchers {
  implicit val roadsMatrix: Array[Array[Int]] = Array(
    Array(0 , 3 , 7 , 2),
    Array(3 , 0 , 5 , 4),
    Array(7 , 5 , 0 , 2),
    Array(2 , 4 , 2 , 0)
  )

  private val s1 = Station(1, 1)
  private val s2 = Station(2, 1)
  private val s3 = Station(3, 1)
  private val s4 = Station(4, 1)

  "trainToStationsMap method" should "work correctly for empty route" in {
    val train = Train(1, List())
    trainToStationsMap(train) shouldBe Map.empty
  }

  "" should "work correctly for only one station" in {
    val train = Train(1, List(s2))
    trainToStationsMap(train) shouldBe Map((s2, 0) -> Set(train))
  }

  "" should "work correctly for repeated station in route" in {
    val train = Train(1, List(s2, s3, s2))
    trainToStationsMap(train) shouldBe Map(
      (s2, 0) -> Set(train),
      (s3, 5) -> Set(train),
      (s2, 10) -> Set(train)
    )
  }

  "" should "convert Train to stations map" in {
    val train = Train(1, List(s1, s4, s3, s2))
    trainToStationsMap(train) shouldBe Map(
      (s1, 0) -> Set(train),
      (s4, 2) -> Set(train),
      (s3, 4) -> Set(train),
      (s2, 9) -> Set(train),
    )
  }

  "mergeTwoMaps method" should "work correctly for both empty maps" in {
    mergeTwoMaps(Map.empty, Map.empty) shouldBe Map.empty
  }

  "" should "work correctly for first empty map" in {
    val t1 = Train(1, List())
    val m = Map(
      (s1, 0) -> Set(t1),
      (s4, 2) -> Set(t1),
      (s3, 4) -> Set(t1),
      (s2, 9) -> Set(t1),
    )
    mergeTwoMaps(m, Map.empty) shouldBe m
  }

  "" should "work correctly for second empty map" in {
    val t1 = Train(1, List())
    val m = Map(
      (s1, 0) -> Set(t1),
      (s4, 2) -> Set(t1),
      (s3, 4) -> Set(t1),
      (s2, 9) -> Set(t1),
    )
    mergeTwoMaps(Map.empty, m) shouldBe m
  }

  "" should "work two maps without intersections" in {
    val t1 = Train(1, List())
    val t2 = Train(2, List())
    val m1 = Map(
      (s1, 0) -> Set(t1),
      (s4, 2) -> Set(t1),
      (s3, 4) -> Set(t1),
      (s2, 9) -> Set(t1),
    )
    val m2 = Map(
      (s2, 0) -> Set(t2),
      (s3, 5) -> Set(t2),
      (s2, 10) -> Set(t2)
    )
    mergeTwoMaps(m1, m2) shouldBe Map(
      (s1, 0) -> Set(t1),
      (s4, 2) -> Set(t1),
      (s3, 4) -> Set(t1),
      (s2, 9) -> Set(t1),
      (s2, 0) -> Set(t2),
      (s3, 5) -> Set(t2),
      (s2, 10) -> Set(t2)
    )
  }

  "" should "work two maps with intersections" in {
    val t1 = Train(1, List())
    val t2 = Train(2, List())
    val m1 = Map(
      (s2, 0) -> Set(t1),
      (s4, 4) -> Set(t1),
      (s3, 6) -> Set(t1),
      (s1, 13) -> Set(t1),
    )
    val m2 = Map(
      (s2, 0) -> Set(t2),
      (s3, 5) -> Set(t2),
      (s2, 10) -> Set(t2),
      (s1, 13) -> Set(t2)
    )
    mergeTwoMaps(m1, m2) shouldBe Map(
      (s4, 4) -> Set(t1),
      (s3, 6) -> Set(t1),
      (s1, 13) -> Set(t2, t1),
      (s2, 0) -> Set(t1, t2),
      (s3, 5) -> Set(t2),
      (s2, 10) -> Set(t2)
    )
  }
}
