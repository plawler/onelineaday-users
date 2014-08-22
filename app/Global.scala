import com.google.inject.{AbstractModule, Guice}
import play.api.GlobalSettings
import services.{StormpathAccountService, UserAccountService}

/**
 * Created By: paullawler
 */
object Global extends GlobalSettings {

  val injector = Guice.createInjector(new AbstractModule {
    override def configure(): Unit = {
      bind(classOf[UserAccountService]).to(classOf[StormpathAccountService])
    }
  })

  /**
   * Controllers must be resolved through the application context. There is a special method of GlobalSettings
   * that we can override to resolve a given controller. This resolution is required by the Play router.
   */
  override def getControllerInstance[A](controllerClass: Class[A]): A = injector.getInstance(controllerClass)

}