package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.json.JSONException;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

/**
 * Created by mwalsh on 8/8/14.
 */
public interface IContestListService {
    @Cacheable(value = "contest")
    String getContestAsJson(String contestId) throws JsonProcessingException;

    void notifyOfNewContest(String contestId) throws JsonProcessingException;

    void removeContest(String urlId) throws JsonProcessingException;

    @CacheEvict(value = "contest", beforeInvocation = true)
    void updateContestEntries(String contestId) throws JsonProcessingException, JSONException;

    public void flushAllCaches();

}
