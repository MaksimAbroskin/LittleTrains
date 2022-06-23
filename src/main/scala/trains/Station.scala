package trains

case class Station (id: Int, capacity: Int) {
  def isCrash(m: Map[(Station, TimeStamp), List[Train]]): Boolean = {
    m.exists(x => x._1._1.capacity < x._2.length )
  }

  def mapCrashes(m: Map[(Station, TimeStamp), List[Train]]): Map[(Station, TimeStamp), List[Train]] = {
    m.filter(x => x._1._1.capacity < x._2.length)
  }
}