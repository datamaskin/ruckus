package controllers;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import dao.IContestDao;
import models.contest.Contest;
import models.contest.Entry;
import models.user.User;
import play.mvc.Result;
import securesocial.core.java.SecuredAction;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by mwalsh on 6/6/14.
 */
public class ContestEntryController extends AbstractController {

    private static final IContestDao contestDao = DaoFactory.getContestDao();

    @SecuredAction
    public static Result contestEntryCreate() {
        ContestForm contestForm = getContestForm();
        User user = getCurrentUser();
        Contest contest = contestDao.findContest(contestForm.contestId);
        contestDao.reserveEntries(user, contest, contestForm.multiplier);
        return jok("success");
    }

    @SecuredAction
    public static Result contestEntryGet() throws JsonProcessingException {
        ContestForm contestForm = getContestForm();
        User user = getCurrentUser();
        Contest contest = contestDao.findContest(contestForm.contestId);
        List<Entry> entries = contestDao.findEntries(user, contest);
        return jok(new ObjectMapper().writeValueAsString(getEntries(entries)));
    }

    @SecuredAction
    public static Result contestEntryDelete() throws JsonProcessingException {
        ContestForm contestForm = getContestForm();
        User user = getCurrentUser();
        Contest contest = contestDao.findContest(contestForm.contestId);

        int howMany = contestForm.multiplier;
        List<Entry> entries = contestDao.findEntries(user, contest);

        if (howMany > entries.size()) {
            return jerr("User has " + entries.size() + " entries. Cannot delete " + howMany + ".");
        }

        for (int i = 0; i < howMany; i++) {
            contestDao.deleteEntry(entries.get(i));
        }
        return jok(new ObjectMapper().writeValueAsString(getEntries(entries)));
    }

    private static Object getEntries(List<Entry> entries) {
        List<Map<String, String>> returnObj = new ArrayList<>();
        for (Entry entry : entries) {
            Map<String, String> map = new HashMap<>();
            map.put("entryId", String.valueOf(entry.getId()));
            returnObj.add(map);
        }
        return returnObj;
    }

    private static ContestForm getContestForm() {
        try {
            String values = request().body().asText();
            return new ObjectMapper().readValue(values, ContestForm.class);
        } catch (IOException e) {
            throw new IllegalArgumentException("Error creating LineupForm.", e);
        }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ContestForm {
        public Integer contestId;
        public Integer multiplier;
    }

}
