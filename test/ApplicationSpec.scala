import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.runner._
import org.junit.runner._

import play.api.test._
import services.{UserAccount, UserAccountService}

/**
 * Add your spec here.
 * You can mock out a whole application including requests, plugins etc.
 * For more information, consult the wiki.
 */
class ApplicationSpec extends Specification with Mockito {

  "Application" should {

//    "send 404 on a bad request" in new WithApplication{
//      route(FakeRequest(GET, "/boum")) must beNone
//    }
//
//    "render the index page" in new WithApplication{
//      val home = route(FakeRequest(GET, "/")).get
//
//      status(home) must equalTo(OK)
//      contentType(home) must beSome.which(_ == "text/html")
//      contentAsString(home) must contain ("Your new application is ready.")
//    }

    "invoke the user account service" in {
      val userAccountService = mock[UserAccountService]
      val application = new controllers.Application(userAccountService)

      userAccountService.findAccount("paul.lawler@gmail.com") returns Some(UserAccount("paul", "lawler", "paul.lawler@gmail.com", Some("password")))

      application.index(FakeRequest())

      there was one(userAccountService).findAccount("paul.lawler@gmail.com")
    }

  }
}
