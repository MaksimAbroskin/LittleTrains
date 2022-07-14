package trains

import scala.collection.immutable.HashMap

case class RailwaySchedule(ends: (String, String), trainsOnRail: Set[TrainOnRail])

object RailwaySchedule {

  private def checkOppositeDirectIntersection(tr1: TimeRange, tr2: TimeRange): Boolean =
    (tr2._1 >= tr1._1 && tr2._1 < tr1._2) || (tr2._2 > tr1._1 && tr2._2 <= tr1._2)  ||
      (tr1._1 >= tr2._1 && tr1._1 < tr2._2) || (tr1._2 > tr2._1 && tr1._2 <= tr2._2)

  private def checkOneDirectIntersection(tr1: TimeRange, tr2: TimeRange): Boolean =
    (tr1._1 > tr2._1 && tr1._2 < tr2._2) || (tr2._1 > tr1._1 && tr2._2 < tr1._2)

  def crashesOnRailwaysSchedule(railway: RailwaySchedule)(map: HashMap[(String, String), Set[TrainOnRail]]): Set[RailwaySchedule] = {
    @annotation.tailrec
    def go(tnt: List[TrainOnRail], acc: Set[RailwaySchedule]): Set[RailwaySchedule] = {
      tnt match {
        case Nil => acc
        case head :: tail => go(tail, acc ++
          checkOneDirect(head, railway.ends)(map) ++
          checkOppositeDirect(head, railway.ends)(map)
        )
      }
    }
    go(railway.trainsOnRail.toList, Set.empty)
  }

  def checkOppositeDirect(train: TrainOnRail, ends: (String, String))(map: HashMap[(String, String), Set[TrainOnRail]]): Set[RailwaySchedule] = {
    val trainsAndTimes = map.getOrElse(ends.swap, Set.empty).filter(t => checkOppositeDirectIntersection(t._2, train._2))
    if (trainsAndTimes.isEmpty) Set.empty
    else Set(RailwaySchedule(ends, trainsAndTimes))
  }

  def checkOneDirect(train: TrainOnRail, ends: (String, String))(map: HashMap[(String, String), Set[TrainOnRail]]): Set[RailwaySchedule] = {
    val trainsAndTimes = map.getOrElse(ends, Set.empty).filter(t => checkOneDirectIntersection(t._2, train._2))
    if (trainsAndTimes.isEmpty) Set.empty
    else Set(RailwaySchedule(ends, trainsAndTimes))
  }

  def collectOppositeDirect(set: Set[RailwaySchedule]): Set[RailwaySchedule] = {
    set.groupBy(rs => List(rs.ends._1, rs.ends._2).sorted).map{
      case (k, v) => RailwaySchedule((k.head, k(1)), v.flatMap(_.trainsOnRail))
    }.toSet
  }

  def railwayScheduleToCommonSchedule(set: Set[RailwaySchedule]): Set[RailwaySchedule] = {
    set.groupBy(_.ends).map {
      case (k, v) => RailwaySchedule(k, v.flatMap(_.trainsOnRail))
    }.toSet
  }

  def setToMap(set: Set[RailwaySchedule]): HashMap[(String, String), Set[TrainOnRail]] = {
    set.map(rs => rs.ends -> rs.trainsOnRail).toMap.to(HashMap)
  }

}