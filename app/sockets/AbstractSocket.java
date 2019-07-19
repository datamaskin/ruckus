package sockets;

import akka.actor.ActorRef;
import akka.actor.Cancellable;
import akka.actor.Props;
import auth.AuthenticationException;
import common.GlobalConstants;
import common.SecureSocialCookie;
import dao.DaoFactory;
import distributed.DistributedServices;
import models.user.User;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.AtmosphereResourceEvent;
import play.Logger;
import play.libs.Akka;
import scala.concurrent.duration.Duration;
import utils.Pinger;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by mgiles on 5/16/14.
 */
public abstract class AbstractSocket {
    private Cancellable cancellable;

    protected ConcurrentMap<String, Object> getDistributedSession(String id) {
        return DistributedServices.getHttpSession(id);
    }

    protected String getCookieValueFromHeader(String cookieKey, String header) {
        String cookieValue = null;
        if (header != null) {
            String[] cookies = header.split(";");
            for (String cookie : cookies) {
                String[] kvp = cookie.split("=");
                String key = kvp[0].trim();
                String value = kvp.length > 1 ? kvp[1].trim() : "";
                if (key.equals(cookieKey)) {
                    return value;
                }
            }
        }
        return cookieValue;
    }

    protected User getCurrentUser(final AtmosphereResource r) {
        return (User) r.session(true).getAttribute(GlobalConstants.SECURESOCIAL_SESSION_MAP);
    }

    public void ready(final AtmosphereResource r) throws AuthenticationException {
        String securesocialCookie = getCookieValueFromHeader(SecureSocialCookie.getName(), r.getRequest().getHeader("cookie"));
        Long userId = (Long) DistributedServices.getInstance().getMap(GlobalConstants.DISTRIBUTED_SESSION_MAP)
                .get(securesocialCookie);
        User user = DaoFactory.getUserDao().findUser(userId);
        if (user == null) {
            throw new AuthenticationException("You must be logged in to connect to this socket");
        }
        // add user to the session
        r.session(true).setAttribute(GlobalConstants.SECURESOCIAL_SESSION_MAP, user);

        Logger.debug("Client {} connected to Socket.", user.getUserName());
        // AWS LoadBalancer will time out inactive socket connections after 30 seconds
        // Need an actual data PING to keep alive
        // Atmosphere ping is not recognized by AWS... uggg

        final ActorRef pingActor = Akka.system().actorOf(Props.create(Pinger.class, r.getResponse()));
        cancellable = Akka.system().scheduler().schedule(Duration.create(30, TimeUnit.SECONDS),
                Duration.create(30, TimeUnit.SECONDS),
                pingActor,
                "PING",
                Akka.system().dispatcher(),
                null
        );
    }

    /**
     * Invoked when the client disconnect or when an unexpected closing of the underlying connection happens.
     *
     * @param event
     */
    public void disconnect(AtmosphereResourceEvent event) {
        if (cancellable != null && !cancellable.isCancelled()) {
            cancellable.cancel();
        }
        if (event != null) {
            if (event.isCancelled()) {
                Logger.info("---------Browser {} unexpectedly disconnected---------", getCurrentUser(event.getResource()).getUserName());
            } else if (event.isClosedByClient()) {
                Logger.info("---------Browser {} closed the connection---------", getCurrentUser(event.getResource()).getUserName());
            }
        } else {
            Logger.info("---------Connection was closed for unknown reason---------");
        }
    }

    abstract public void onDisconnect(AtmosphereResourceEvent event);

    /**
     * Uses a synchronized block to safely write the provided data out to the socket without it being clobbered
     * by other data that has been received from another topic.
     *
     * @param resource      The web socket to write data to.
     * @param data          The data to write to the socket.
     */
    protected void writeData(AtmosphereResource resource, String data) {
        synchronized (this) {
            resource.getResponse().write(data);
        }
    }
}
