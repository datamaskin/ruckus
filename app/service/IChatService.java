package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import common.ChatMessage;

/**
 * Created by mwalsh on 8/23/14.
 */
public interface IChatService {
    void destroyChannel(String urlId);

    void addMessage(String contestId, ChatMessage message);

    String getMessageList(String contestId) throws JsonProcessingException;
}
