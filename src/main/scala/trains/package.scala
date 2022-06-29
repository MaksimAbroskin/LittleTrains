import trains.Models.{Station, Train}

package object trains {
  type TimeStamp = Int
  type RoadsMatrix = List[List[Int]]
  type Schedule = Map[(Station, TimeStamp), Set[Train]]
}
