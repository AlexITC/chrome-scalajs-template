import org.scalatest.wordspec.AnyWordSpec
import org.scalatest.matchers.must.Matchers._

class ExampleSpec extends AnyWordSpec {
  "example" should {
    "work" in {
      println("It works!")
      true must be(true)
    }
  }
}
