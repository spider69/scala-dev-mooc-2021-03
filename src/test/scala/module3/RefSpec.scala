package module3

import zio.test.DefaultRunnableSpec
import zio.test.ZSpec
import zio.test.Assertion.{anything, equalTo, hasSize, isSubtype, throws, not}
import zio.test._
import zio.test.TestAspect._

object RefSpec extends DefaultRunnableSpec {

  def spec: ZSpec[Environment, Failure] = suite("Ref spec")(
    suite("atomic update properties")(
      testM("update method should be atomic") {
        assertM(zioDS.ref.atomicUpdate)(equalTo(10000))
      } @@nonFlaky,
      testM("ref composition is not Atomic"){
          assertM(zioDS.ref.atomicUpdate2)(not(equalTo(3)))
      } @@flaky,
      test("Array equality"){
          assert(Array(1, 2, 3))(equalTo(Array(1, 2, 3)))
      }
    )
  )

}
