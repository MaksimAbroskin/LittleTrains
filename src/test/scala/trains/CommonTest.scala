package trains

import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers.should.Matchers
import trains.Common.flattenOptionsSet
import trains.ErrorMessage.FileParsingErrorMessage

class CommonTest extends AnyFlatSpec with Matchers {
  val t1: Train = Train("t1", 1, List("s1", "s2", "s3"))
  val t2: Train = Train("t2", 1, List("s2", "s3", "s2"))

  val fileName = "file"

  "flattenOptionsSet method" should "return Right for correct list" in {
    flattenOptionsSet(List(Some(t1), Some(t2)))(fileName) shouldBe Right(Set(t2, t1))
  }

  "" should "return Left for list with None" in {
    flattenOptionsSet(List(Some(t1), None, Some(t2)))(fileName) shouldBe Left(FileParsingErrorMessage(fileName))
  }

}
