package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import TrainsMain.trainToStationsMap

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
    trainToStationsMap(train) shouldBe Map((s2, 0) -> List(train))
  }

  "" should "work correctly for repeated station in route" in {
    val train = Train(1, List(s2, s3, s2))
    trainToStationsMap(train) shouldBe Map(
      (s2, 0) -> List(train),
      (s3, 5) -> List(train),
      (s2, 10) -> List(train)
    )
  }

  "" should "convert Train to stations map" in {
    val train = Train(1, List(s1, s4, s3, s2))
    trainToStationsMap(train) shouldBe Map(
      (s1, 0) -> List(train),
      (s4, 2) -> List(train),
      (s3, 4) -> List(train),
      (s2, 9) -> List(train),
    )
  }
}
