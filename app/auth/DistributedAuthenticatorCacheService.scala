package auth

import common.GlobalConstants
import securesocial.core.services.CacheService

import scala.concurrent.Future

/**
 * Created by mwalsh on 8/4/14.
 */
object DistributedAuthenticatorCacheService {

  class Default extends CacheService {

    import distributed.DistributedServices

import scala.reflect.ClassTag

    override def set[T](key: String, value: T, ttlInSeconds: Int): Future[Unit] = {
      Future.successful(DistributedServices.getInstance().getMap(GlobalConstants.SECURESOCIAL_SESSION_MAP).put(key, value))
    }

    override def getAs[T](key: String)(implicit ct: ClassTag[T]): Future[Option[T]] = Future.successful {
      Option(DistributedServices.getInstance().getMap(GlobalConstants.SECURESOCIAL_SESSION_MAP).get(key))
    }

    override def remove(key: String): Future[Unit] = {
      import scala.concurrent.ExecutionContext.Implicits.global
      Future {
        DistributedServices.getInstance().getMap(GlobalConstants.SECURESOCIAL_SESSION_MAP).remove(key)
        ()
      }
    }
  }

}