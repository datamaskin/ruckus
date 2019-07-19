package controllers;

import com.avaje.ebean.Ebean;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.contest.Contest;
import play.mvc.Result;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *

 1. A single sport is required for it to be engaged (at the start, it will be auto-engaged since we only have NFL)

 2. If no contest size has been selected, the player will be sent to a H2H Anonymous table for a maximum of 10% of their
 available bankroll. If no contest size is available for 10% of their bankroll (example: $10 bankroll = $1 buy-in; but
 our minimum buy-in is $2), then it will be defaulted to the $2 tournament).

 3. If the contest size has been selected, or multiple contest sizes have been selected, it will default to the
 smallest of the selected.

 4. If buy-in has been selected, it will default to the smallest amount selected, given that the amount is
 available within their bankroll. If not, then it will default to the 10% or $2 buy-in tournament.

 * Created by mgiles on 6/13/14.
 * Modified by gislas on 8/20/14.
 */
public class QuickPlay extends AbstractController {

    public static Result create() throws IOException {
        FormData formData = getFormData();
        List<Contest> contests = Ebean.find(Contest.class).findList();
        List<String> contestList = new ArrayList<>();

        for (Contest c : contests) {
            if (matches(c, formData)) {
                contestList.add(c.getUrlId());
            }
        }
        return jok(contestList);
    }

    private static /*final*/ FormData getFormData() { //final keyword may be redundant as the static keyword implies the value hasn't changed.
        try {
            String values = request().body().asFormUrlEncoded().get("data")[0]; //see what the string is showing and if you must adapt to the incoming data.
            return new ObjectMapper().readValue(values, FormData.class);

        } catch (IOException e) {
            throw new IllegalArgumentException("Error creating FormData.", e);
        }
    }

    public static class FormData {
        public String league;
        public List<Integer> entryFee;
        public Range entryFeeSelected;
        public Range numPlayers;
        public String grouping;
        public int salaryCap;
    }

    public static class Range {
        public int minimum;
        public int maximum;
    }

    //TODO: Specify or modify the matches as detailed above without failures, and applying the proper functionality.
    //currently returns false for the given conditions and otherwise returns true. must meet specific conditions to join (true condition).
    private static boolean matches(Contest c, FormData f) {
        if (!f.league.equalsIgnoreCase("ALL") && !f.league.equalsIgnoreCase(c.getLeague().getAbbreviation())) { //removing the 'not' operator for testing
            return false;
        }
        if (!f.entryFee.contains(c.getEntryFee())) {
            return false;
        }
        if (c.getEntryFee() < f.entryFeeSelected.minimum || c.getEntryFee() > f.entryFeeSelected.maximum) {
            return false;
        }
        if (c.getCapacity() < f.numPlayers.minimum || c.getCapacity() > f.numPlayers.maximum) {
            return false;
        }
        if (!f.grouping.equalsIgnoreCase("ALL") && !f.grouping.equalsIgnoreCase(c.getSportEventGrouping().getSportEventGroupingType().getName())) { //removing the 'not operator for testing.
            return false;
        }
        return true;
    }
}
