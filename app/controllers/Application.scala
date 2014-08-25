package controllers

import com.google.inject.{Singleton, Inject}

import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.mvc.{Action, Controller}
import services.{UserAccount, Credentials, UserAccountService}

@Singleton
class Application @Inject()(userService: UserAccountService) extends Controller {

  implicit val fmtCreds = Json.format[Credentials]
  implicit val fmtAccount = Json.format[UserAccount]

//  implicit val writeCredentials = Json.writes[Credentials]

  def index = Action {
    userService.findAccount("paul.lawler@gmail.com") match {
      case None => Ok(views.html.index("Your application is wired up and calling the UserAccountService but returned None."))
      case _ => Ok(views.html.index("Your new application is ready."))
    }
  }

  def authenticate = Action(parse.json) { request =>
    request.body.validate[Credentials].map { credentials =>
      userService.authenticate(credentials.username, credentials.password) match {
        case Some(account) => Ok(Json.toJson(account))
        case None => Unauthorized("The credentials you presented failed authentication")
      }
    }.recoverTotal {
      e => BadRequest("Error:" + JsError.toFlatJson(e))
    }
  }

}