package services

import java.util

import com.google.inject.Singleton
import com.stormpath.sdk.authc.UsernamePasswordRequest
import com.stormpath.sdk.impl.account.DefaultAccount
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
  def authenticate(email: String, password: String): Option[UserAccount]

}

@Singleton
class StormpathAccountService extends UserAccountService {

  lazy val client: Client = {
    val path = System.getProperty("user.home") + "/.stormpath/apiKey.properties"
//    Logger.debug(s"Stormpath props path: $path")
    val apiKey = ApiKeys.builder().setFileLocation(path).build()
    Clients.builder().setApiKey(apiKey).build()
  }

  lazy val application: Application = {
    client.getResource("https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T", classOf[Application])
  }

  override def createAccount(userAccount: UserAccount): UserAccount = {
    var account: Account = client.instantiate(classOf[Account])
    account.setGivenName(userAccount.givenName)
    account.setSurname(userAccount.surName)
    account.setEmail(userAccount.email)
    account.setPassword(userAccount.password.get)
    try {
      val result: Account = application.createAccount(account)
      UserAccount(result.getGivenName, result.getSurname, result.getEmail, None)
    } catch {
      case e: ResourceException => throw new ResourceException(e)
    }
  }

  override def findAccount(email: String): Option[UserAccount] = {
    val queryParams = new util.HashMap[String, Object]()
    queryParams.put("email", email)
    try {
      val account = application.getAccounts(queryParams).head // http://alvinalexander.com/scala/how-to-convert-maps-scala-java
      Some(UserAccount(account.getGivenName, account.getSurname, account.getEmail, None))
    } catch {
      case e: NoSuchElementException => None
    }
  }

  override def deleteAccount(userAccount: UserAccount) = {
    val queryParams = new util.HashMap[String, Object]()
    queryParams.put("email", userAccount.email)
    try {
      application.getAccounts(queryParams).head.delete()
    } catch {
      case e: NoSuchElementException => Logger.debug(s"No account with email [$userAccount.email] to delete")
    }
  }

  override def authenticate(email: String, password: String): Option[UserAccount] = {
    try {
      val authRequest = new UsernamePasswordRequest(email, password)
      val account = application.authenticateAccount(authRequest).getAccount
      Some(UserAccount(account.getGivenName, account.getSurname, account.getEmail, None))
    } catch {
      case e: ResourceException => Logger.error(e.getMessage)
      None
    }
  }

}
