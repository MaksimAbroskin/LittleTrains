package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.Station._

class StationsTest extends AnyFlatSpec with Matchers {
  "stationFromString method" should "return Some(Station) for correct string" in {
    val s = " name:   station1  ;capacity: 6"
    stationFromString(s) shouldBe Some(Station("station1", 6))
  }

  "" should "return None for incorrect string" in {
    val s = " name - station1;capacity:6"
    stationFromString(s) shouldBe None
  }
}
