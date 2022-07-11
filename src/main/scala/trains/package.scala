package object trains {
  type TimeStamp = Int
  type TimeRange = (Int, Int)
  type TrainOnRail = (String, TimeRange)
  type RoadsMap = Map[(String, String), Int]
}
