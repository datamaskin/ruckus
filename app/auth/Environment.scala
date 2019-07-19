package auth

import dao.{IUserDao, UserDao}
import models.user.User
import securesocial.controllers.ViewTemplates
import securesocial.core.{EventListener, RuntimeEnvironment}
import securesocial.core.authenticator.{HttpHeaderAuthenticatorBuilder, AuthenticatorStore, CookieAuthenticatorBuilder}
import securesocial.core.providers.{FacebookProvider, GoogleProvider, UsernamePasswordProvider}
import securesocial.core.services.{AuthenticatorService, UserService}

import scala.collection.immutable.ListMap

class Environment extends RuntimeEnvironment.Default[User] {

  private val userDao: IUserDao = new UserDao

  override val userService: UserService[User] = new AppUserService(userDao)

  override lazy val viewTemplates: ViewTemplates = new CustomViewTemplates(this)

  override lazy val eventListeners: List[EventListener[User]] = List(new AppEventListener(userDao))

  override lazy val providers = ListMap(
    // oauth 2 client providers
    include(new FacebookProvider(routes, cacheService, oauth2ClientFor(FacebookProvider.Facebook))),
    include(new GoogleProvider(routes, cacheService, oauth2ClientFor(GoogleProvider.Google))),
    //include(new TwitterProvider(routes, cacheService, oauth1ClientFor(TwitterProvider.Twitter))),
    // username password
    include(new UsernamePasswordProvider[User](userService, avatarService, viewTemplates, passwordHashers))
  )

  val distributedCacheService = new DistributedAuthenticatorCacheService.Default();

  override lazy val authenticatorService = new AuthenticatorService(
    new CookieAuthenticatorBuilder[User](new AuthenticatorStore.Default(distributedCacheService), idGenerator),
    new HttpHeaderAuthenticatorBuilder[User](new AuthenticatorStore.Default(distributedCacheService), idGenerator)
  )

}
