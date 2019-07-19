package common;

import com.fasterxml.jackson.annotation.JsonRawValue;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Created by dan on 4/14/14.
 */
public class ClientMessage {

    private String type;
    private Object payload;
    private String jsonPayload;

    @JsonRawValue
    public Object getPayload() throws JsonProcessingException {
        if (jsonPayload != null) {
            return jsonPayload;
        } else {
            return new ObjectMapper().writeValueAsString(payload);
        }
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }

    public void setJsonPayload(String payload) {
        this.jsonPayload = payload;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
