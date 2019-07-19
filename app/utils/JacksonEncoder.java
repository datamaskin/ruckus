package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.managed.Encoder;
import common.ChatMessage;

import java.io.IOException;

/**
 * Created by mgiles on 6/13/14.
 */
public class JacksonEncoder implements Encoder<ChatMessage, String> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String encode(ChatMessage m) {
        try {
            return mapper.writeValueAsString(m);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
