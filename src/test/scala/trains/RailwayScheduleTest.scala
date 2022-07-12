package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.RailwaySchedule._

class RailwayScheduleTest extends AnyFlatSpec with Matchers {
  private val railway = ("s1", "s2")

  "crashesOnRailwaysSchedule method for two opposite trains" should "return crashes for simultaneously start" in {
    val t1: TrainOnRail = ("t1", (0, 2))
    val t2: TrainOnRail = ("t2", (0, 2))
    val rs1 = RailwaySchedule(railway, Set(t1))
    val rs2 = RailwaySchedule(railway.swap, Set(t2))

    crashesOnRailwaysSchedule(rs1)(setToMap(Set(rs1, rs2))) shouldBe Set(RailwaySchedule(rs1.ends, Set(t2)))
  }

  "" should "return crashes for non-simultaneously start" in {
    val t1: TrainOnRail = ("t1", (0, 2))
    val t2: TrainOnRail = ("t2", (1, 3))
    val rs1 = RailwaySchedule(railway, Set(t1))
    val rs2 = RailwaySchedule(railway.swap, Set(t2))

    crashesOnRailwaysSchedule(rs1)(setToMap(Set(rs1, rs2))) shouldBe Set(RailwaySchedule(rs1.ends, Set(t2)))
  }

  "" should "return crashes for one-length and one-speed trains" in {
    val t1: TrainOnRail = ("t1", (0, 1))
    val t2: TrainOnRail = ("t2", (0, 1))
    val rs1 = RailwaySchedule(railway, Set(t1))
    val rs2 = RailwaySchedule(railway.swap, Set(t2))

    crashesOnRailwaysSchedule(rs2)(setToMap(Set(rs1, rs2))) shouldBe Set(RailwaySchedule(rs2.ends, Set(t1)))
  }

  "" should "return no crashes if second train start when first one finished" in {
    val t1: TrainOnRail = ("t1", (0, 2))
    val t2: TrainOnRail = ("t2", (2, 4))
    val rs1 = RailwaySchedule(railway, Set(t1))
    val rs2 = RailwaySchedule(railway.swap, Set(t2))

    crashesOnRailwaysSchedule(rs1)(setToMap(Set(rs1, rs2))) shouldBe Set.empty
  }

  "crashesOnRailwaysSchedule method for two trains on one direction" should "return crashes if first train caught up with second one" in {
    val t1: TrainOnRail = ("t1", (0, 4))
    val t2: TrainOnRail = ("t2", (2, 3))
    val rs1 = RailwaySchedule(railway, Set(t1))
    val rs2 = RailwaySchedule(railway.swap, Set(t2))

    crashesOnRailwaysSchedule(rs2)(setToMap(Set(rs1, rs2))) shouldBe Set(RailwaySchedule(rs2.ends, Set(t1)))
  }
}
