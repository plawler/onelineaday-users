package services

import com.stormpath.sdk.resource.ResourceException
import org.junit.runner.RunWith
import org.specs2.mutable.Specification
import org.specs2.runner.JUnitRunner

/**
 * Created By: paullawler
 */
@RunWith(classOf[JUnitRunner])
class StormpathAccountServiceSpec extends Specification {

  lazy val stormpath = new StormpathAccountService

  "StormpathAccountService" should {

    "fail to create an account with an invalid password" in {
      val userAccount: UserAccount = UserAccount("Paul", "Lawler", "paul.lawler@gmail.com", Some("password"))
      stormpath.createAccount(userAccount) must throwA[ResourceException]
    }

    "create a new account" in {
      val account = stormpath.createAccount(UserAccount("Paul", "Lawler", "paul.lawler@gmail.com", Some("pAssw0rd")))
      account must beAnInstanceOf[UserAccount]
    }

    "find an account" in {
      stormpath.findAccount("paul.lawler@gmail.com") must not be None
    }

    "authenticate an account" in {
      stormpath.authenticate("paul.lawler@gmail.com", "pAssw0rd") must not be None
    }

    "delete an account" in {
      val account = stormpath.findAccount("paul.lawler@gmail.com").get
      stormpath.deleteAccount(account)
      stormpath.findAccount("paul.lawler@gmail.com") mustEqual None
    }

    "find an account that doesn't exist" in {
      stormpath.findAccount("doesnot@exist.com") mustEqual None
    }

  }

}
