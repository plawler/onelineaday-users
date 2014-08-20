package services

import java.util

import com.stormpath.sdk.resource.ResourceException

import scala.collection.JavaConversions._

import com.stormpath.sdk.account.{AccountList, Account}
import com.stormpath.sdk.api.ApiKeys
import com.stormpath.sdk.application.Application
import com.stormpath.sdk.client.{Clients, Client}
import play.api.Logger

/**
 * Created By: paullawler
 */

case class UserAccount(givenName: String, surName: String, email: String, password: Option[String])

// may need a StormpathAccount class to hold onto important data specific to the service

trait UserAccountService {

  def createAccount(userAccount: UserAccount): UserAccount
  def findAccount(email: String): Option[UserAccount]
  def deleteAccount(userAccount: UserAccount)

}

class StormpathAccountService extends UserAccountService {

  lazy val client: Client = {
    val path = System.getProperty("user.home") + "/.stormpath/apiKey.properties"
    Logger.debug(s"Stormpath props path: $path")
    val apiKey = ApiKeys.builder().setFileLocation(path).build()
    Clients.builder().setApiKey(apiKey).build()
  }

  lazy val application: Application = {
    client.getResource("https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T", classOf[Application])
  }

  override def createAccount(userAccount: UserAccount) = {
    var account: Account = client.instantiate(classOf[Account])
    account.setGivenName(userAccount.givenName)
    account.setSurname(userAccount.surName)
    account.setEmail(userAccount.email)
    account.setPassword(userAccount.password.get)
//    CustomData customData = account.getCustomData();
//    customData.put("favoriteColor", "white");
    try {
      val acct: Account = application.createAccount(account)
      UserAccount(acct.getGivenName, acct.getSurname, acct.getEmail, None)
    } catch {
      case e: ResourceException => throw new ResourceException(e)
    }
  }

  override def findAccount(email: String): Option[UserAccount] = {
    val queryParams = new util.HashMap[String, Object]()
    queryParams.put("email", email)

    val account = application.getAccounts(queryParams).head
    Some(UserAccount(account.getGivenName, account.getSurname, account.getEmail, None))
  }

  override def deleteAccount(userAccount: UserAccount) = {
    val queryParams = new util.HashMap[String, Object]()
    queryParams.put("email", userAccount.email)

    val account = application.getAccounts(queryParams).head
    account.delete()
  }
}
