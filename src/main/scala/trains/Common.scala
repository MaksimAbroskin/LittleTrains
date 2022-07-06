package trains

import trains.ErrorMessage.FileParsingErrorMessage

object Common {
  def flattenOptionsSet[A](l: List[Option[A]])(fileName: String): Either[ErrorMessage, Set[A]] = {
    if (l.contains(None)) Left(FileParsingErrorMessage(fileName))
    else Right(l.toSet.flatten)
  }
}
