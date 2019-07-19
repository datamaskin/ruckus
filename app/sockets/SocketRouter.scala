package sockets

import org.atmosphere.play.{AtmosphereController, AtmosphereCoordinator}
import play.api.mvc.Handler
import play.core.j._
import play.libs.F.Promise
import play.mvc.Http.RequestHeader

object SocketRouter {

  def dispatch(request: RequestHeader): Handler = {
    if (!AtmosphereCoordinator.instance().matchPath(request.path)) {
      return null;
    }
    val c = classOf[AtmosphereController]
    val a = new AtmosphereController

    // Netty fail to decode headers separated by a ','
    val connectionH: Array[String] = request.headers().get("Connection")
    val webSocketH = request.getHeader("Upgrade")
    var wsSupported = false;

    if (webSocketH != null && webSocketH.equalsIgnoreCase("websocket")) {
      wsSupported = true
    }

    if (!wsSupported && connectionH != null) {
      for (c: String <- connectionH) {
        if (c != null && c.toLowerCase().equalsIgnoreCase("upgrade")) {
          wsSupported = true;
        }
      }
    }

    if (wsSupported) {
      JavaWebSocket.ofString(a.webSocket)
    } else {
      new JavaAction {
        val annotations = new JavaActionAnnotations(c, c.getMethod("http"))
        val parser = annotations.parser

        def invocation = Promise.pure(a.http)
      }
    }

  }

  import play.api.mvc.{RequestHeader => ScalaRequestHeader}

  def dispatch(request: ScalaRequestHeader): Option[Handler] = {
    if (!AtmosphereCoordinator.instance().matchPath(request.path)) {
      None
    } else {
      val c = classOf[AtmosphereController]
      val a = new AtmosphereController

      // Netty fail to decode headers separated by a ','
      val connectionH = request.headers.get("Connection")
      val webSocketH = request.headers.get("Upgrade")
      val wsSupported = webSocketH.isDefined || connectionH.map(_.toLowerCase.contains("upgrade")).getOrElse(false)

      if (wsSupported) {
        Some(JavaWebSocket.ofString(a.webSocket))
      } else {
        Some(new JavaAction {
          val annotations = new JavaActionAnnotations(c, c.getMethod("http"))
          val parser = annotations.parser

          def invocation = Promise.pure(a.http)
        }
        )
      }
    }
  }

}
