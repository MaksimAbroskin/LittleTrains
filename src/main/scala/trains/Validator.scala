package trains

import trains.ErrorMessage.{NoSuchRoadErrorMessage, NoSuchStationErrorMessage}

case class Validator(roads: RoadsMap, stations: Set[Station], trains: Set[Train]) {
  private val allRoutes = trains.map(_.route)

  def listToTuplesSet[A](l: List[A]): Set[(A, A)] = {
    @annotation.tailrec
    def go(l: List[A], acc: Set[(A, A)]): Set[(A, A)] = {
      l match {
        case _ :: Nil | Nil => acc
        case cur :: next :: tail => go(next :: tail, acc + ((cur, next): (A, A)))
      }
    }
    go(l, Set.empty)
  }

  val validateStations: Option[ErrorMessage] = {
    val stationsNames = stations.map(_.name)
    allRoutes.flatten.find(!stationsNames.contains(_)).map(NoSuchStationErrorMessage)
  }

  val validateRouts: Option[ErrorMessage] = {
    allRoutes.flatMap(listToTuplesSet).find(!roads.keySet.contains(_)).map(ss => NoSuchRoadErrorMessage(ss._1, ss._2))
  }

  val errorsList: List[ErrorMessage] = List(validateStations, validateRouts).flatten

}
