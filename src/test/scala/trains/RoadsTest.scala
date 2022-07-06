package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
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
}
