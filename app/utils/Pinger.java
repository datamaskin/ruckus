package utils;

import akka.actor.UntypedActor;
import org.atmosphere.cpr.AtmosphereResponse;

public class Pinger extends UntypedActor {
    private AtmosphereResponse out;

    public Pinger(AtmosphereResponse out) {
        this.out = out;
    }

    /**
     * To be implemented by concrete UntypedActor, this defines the behavior of the
     * UntypedActor.
     *
     * @param message
     */
    @Override
    public void onReceive(Object message) throws Exception {
        if (message.equals("PING")) {
            if (!out.destroyed()) {
//                ClientMessage ping = new ClientMessage();
//                ping.setType("PING");
//                Logger.info(new ObjectMapper().writeValueAsString(ping));
//                out.write(new ObjectMapper().writeValueAsString(ping));
                out.write(" ");
                out.flushBuffer();
            }
        } else {
            unhandled(message);
        }
    }
}