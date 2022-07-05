package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.ErrorMessage.FileParsingErrorMessage
import trains.Road._

class RoadsTest extends AnyFlatSpec with Matchers {
  "roadFromString method" should "return Some(Road) for correct string" in {
    val s = " from:   station1 ;to:2   ;distance: 32"
    roadFromString(s) shouldBe Some(Road("station1", "2", 32))
  }

  "" should "return None for incorrect string" in {
    val s = " from:   station1 ;to:s2   ,distance: 32"
    roadFromString(s) shouldBe None
  }

  "" should "return None for incorrect string 2" in {
    val s = " from:station1;to:s2;distance:f32"
    roadFromString(s) shouldBe None
  }

  val r1: Road = Road("s1", "s2", 23)
  val r2: Road = Road("s3", "s2", 3)

  "flattenRoadSet method" should "return Right for correct list" in {
    val s = " from:station1;to:s2;distance:f32"
    flattenRoadSet(List(Some(r1), Some(r2))) shouldBe Right(Set(r1, r2))
  }

  "" should "return Left for list with None" in {
    val s = " from:station1;to:s2;distance:f32"
    flattenRoadSet(List(Some(r1), Some(r2), None)) shouldBe Left(FileParsingErrorMessage("Roads file has an error"))
  }

}
