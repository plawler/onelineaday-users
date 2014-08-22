package controllers

import com.google.inject.{Singleton, Inject}
import com.stormpath.sdk.resource.ResourceException
import play.api._
import play.api.mvc._
import services.UserAccountService

@Singleton
class Application @Inject()(userService: UserAccountService) extends Controller {

  def index = Action {
    userService.findAccount("paul.lawler@gmail.com") match {
      case None => Ok(views.html.index("Your application is wired up and calling the UserAccountService but returned None."))
      case _ => Ok(views.html.index("Your new application is ready."))
    }
  }

}