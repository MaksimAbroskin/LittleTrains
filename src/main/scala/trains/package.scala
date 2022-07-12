import scala.collection.immutable.HashMap

package object trains {
  type TimeStamp = Int
  type TimeRange = (TimeStamp, TimeStamp)
  type TrainOnRail = (String, TimeRange)
  type RoadsMap = HashMap[(String, String), Int]
}
