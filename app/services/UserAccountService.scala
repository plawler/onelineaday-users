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
case class Credentials(username: String, password: String)

// may need a StormpathAccount class to hold onto important data specific to the service

trait UserAccountService {

  def createAccount(userAccount: UserAccount): UserAccount
  def findAccount(email: String): Option[UserAccount]
  def deleteAccount(userAccount: UserAccount)
  def authenticate(username: String, password: String): Option[UserAccount]

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

  override def authenticate(username: String, password: String): Option[UserAccount] = {
    try {
      val authRequest = new UsernamePasswordRequest(username, password)
      val account = application.authenticateAccount(authRequest).getAccount
      Some(UserAccount(account.getGivenName, account.getSurname, account.getEmail, None))
    } catch {
      case e: ResourceException => Logger.error(e.getMessage)
      None
    }
  }

  /**
   * Stormpath Application resource
   *
   {
      "href": "https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T",
      "name": "One Line a Day",
      "description": null,
      "status": "ENABLED",
      "tenant": {
          "href": "https://api.stormpath.com/v1/tenants/3iUCQTn38ZrvSJHz63K6vH"
      },
      "defaultAccountStoreMapping": {
          "href": "https://api.stormpath.com/v1/accountStoreMappings/6cpux00voVDqWC4MtRSpcN"
      },
      "defaultGroupStoreMapping": {
          "href": "https://api.stormpath.com/v1/accountStoreMappings/6cpux00voVDqWC4MtRSpcN"
      },
      "customData": {
          "href": "https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T/customData"
      },
      "accounts": {
          "href": "https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T/accounts"
      },
      "groups": {
          "href": "https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T/groups"
      },
      "accountStoreMappings": {
          "href": "https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T/accountStoreMappings"
      },
      "loginAttempts": {
          "href": "https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T/loginAttempts"
      },
      "passwordResetTokens": {
          "href": "https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T/passwordResetTokens"
      },
      "apiKeys": {
          "href": "https://api.stormpath.com/v1/applications/6cpZKjY12wrndZxh2cRf1T/apiKeys"
      }
    }
   */

  /**
   * Stormpath Account resource
   *
   {
      "href": "https://api.stormpath.com/v1/accounts/4JnuYzKl93jLKjeNH4tP43",
      "username": "testUser",
      "email": "testuser@onelineaday.me",
      "givenName": "Test",
      "middleName": null,
      "surname": "User",
      "fullName": "Test User",
      "status": "ENABLED",
      "emailVerificationToken": null,
      "customData": {
          "href": "https://api.stormpath.com/v1/accounts/4JnuYzKl93jLKjeNH4tP43/customData"
      },
      "providerData": {
          "href": "https://api.stormpath.com/v1/accounts/4JnuYzKl93jLKjeNH4tP43/providerData"
      },
      "directory": {
          "href": "https://api.stormpath.com/v1/directories/6cpdemqCOrL0c6NpPaFUk3"
      },
      "tenant": {
          "href": "https://api.stormpath.com/v1/tenants/3iUCQTn38ZrvSJHz63K6vH"
      },
      "groups": {
          "href": "https://api.stormpath.com/v1/accounts/4JnuYzKl93jLKjeNH4tP43/groups"
      },
      "groupMemberships": {
          "href": "https://api.stormpath.com/v1/accounts/4JnuYzKl93jLKjeNH4tP43/groupMemberships"
      },
      "apiKeys": {
          "href": "https://api.stormpath.com/v1/accounts/4JnuYzKl93jLKjeNH4tP43/apiKeys"
      }
    }
   */

}
