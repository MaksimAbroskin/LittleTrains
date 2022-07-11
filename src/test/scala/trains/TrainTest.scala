package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.Train._

class TrainTest extends AnyFlatSpec with Matchers {
  "trainFromString method" should "return Some(Train) for correct string" in {
    val s = " name:   t1  ;speed: 1; route: s1, s2, s3"
    trainFromString(s) shouldBe Some(Train("t1", 1, List("s1", "s2", "s3")))
  }

  "" should "return None for incorrect string" in {
    val s = "name:tr1;speed:6,route:s1 ; s2, s3"
    trainFromString(s) shouldBe None
  }

  private val roads = Set(
    Road("s1", "s3", 7),
    Road("s1", "s4", 2),
    Road("s2", "s3", 5),
    Road("s2", "s4", 4),
    Road("s3", "s4", 2),
  )

  val roadsMap: RoadsMap = Road.roadSetToMap(roads)

  "trainToSchedule method" should "work correctly for empty route" in {
    trainToStationsSchedule(Train("t1", 1, List()))(roadsMap) shouldBe Set.empty
  }

  "" should "work correctly for only one station" in {
    val train = Train("t1", 1, List("s2"))
    trainToStationsSchedule(train)(roadsMap) shouldBe Set(StationSchedule("s2", 0, Set("t1")))
  }

  "" should "work correctly for repeated station in route" in {
    val train = Train("t1", 1, List("s2", "s3", "s2"))
    trainToStationsSchedule(train)(roadsMap) shouldBe Set(
      StationSchedule("s2", 0, Set("t1")),
      StationSchedule("s3", 5, Set("t1")),
      StationSchedule("s2", 10, Set("t1"))
    )
  }

  "" should "convert Train to schedule" in {
    val train = Train("t1", 1, List("s1", "s4", "s3", "s2"))
    trainToStationsSchedule(train)(roadsMap) shouldBe Set(
      StationSchedule("s1", 0, Set("t1")),
      StationSchedule("s4", 2, Set("t1")),
      StationSchedule("s3", 4, Set("t1")),
      StationSchedule("s2", 9, Set("t1"))
    )
  }

}
