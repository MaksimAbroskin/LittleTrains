package trains

case class RailwaySchedule(ends: (String, String), trainsAndTimes: List[TrainOnRail])

object RailwaySchedule {

  private def checkIntersection(tr1: TimeRange, tr2: TimeRange): Boolean =
    (tr2._1 >= tr1._1 && tr2._1 < tr1._2) || (tr2._2 > tr1._1 && tr2._2 <= tr1._2) ||
      (tr1._1 > tr2._1 && tr1._2 < tr2._2) || (tr2._1 > tr1._1 && tr2._2 < tr1._2)

  def crashesOnRailwaysSchedule(railway: RailwaySchedule): List[CrashOnRail] = {
    @annotation.tailrec
    def go(tnt: List[TrainOnRail], acc: List[CrashOnRail]): List[CrashOnRail] = {
      tnt match {
        case _ :: Nil | Nil => acc
        case head :: tail => go(tail, acc ++ checkTrainOnRail(head, tail).map(tup => CrashOnRail(railway.ends, tup._1, tup._2)))
      }
    }
    go(railway.trainsAndTimes, List.empty)
  }

  private def checkTrainOnRail(train: TrainOnRail, tnt: List[TrainOnRail]): List[(TrainOnRail, TrainOnRail)] = {
    tnt.filter(t => checkIntersection(t._2, train._2)).map((train, _))
  }

  def collectOppositeDirection(set: Set[RailwaySchedule]): Set[RailwaySchedule] = {
    set.groupBy(s => List(s.ends._1, s.ends._2).sorted).map{
      case (k, v) => RailwaySchedule((k.head, k(1)), v.flatMap(_.trainsAndTimes).toList)
    }.toSet
  }

}