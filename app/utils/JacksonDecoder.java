package utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.atmosphere.config.managed.Decoder;
import common.ChatMessage;

import java.io.IOException;

/**
 * Created by mgiles on 6/13/14.
 */
public class JacksonDecoder implements Decoder<String, ChatMessage> {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public ChatMessage decode(String s) {
        try {
            return mapper.readValue(s, ChatMessage.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
