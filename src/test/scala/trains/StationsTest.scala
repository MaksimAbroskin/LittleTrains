package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.ErrorMessage.FileParsingErrorMessage
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

  val s1: Station = Station("s1", 1)
  val s2: Station = Station("s2", 4)

  "flattenStationSet" should "return Right for correct list" in {
    flattenStationSet(List(Some(s1), Some(s2))) shouldBe Right(Set(s2, s1))
  }

  "" should "return Left for list with None" in {
    flattenStationSet(List(Some(s1), None, Some(s2))) shouldBe Left(FileParsingErrorMessage("Stations file has an error"))
  }

}
