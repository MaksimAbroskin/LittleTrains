package trains

case class CrashOnRail(ends: (String, String), train1: TrainOnRail, train2: TrainOnRail) {
  override def toString: String = {
    s"""Railway: ${ends._1} <-> ${ends._2}:
       |    train ${train1._1} in time range ${train1._2}
       |    train ${train2._1} in time range ${train2._2}
       |""".stripMargin
  }
}