package service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.DaoFactory;
import models.contest.*;
import models.sports.League;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by dan on 6/3/14.
 */
public class ContestFilterService extends AbstractCachingService {
    /**
     * Retrieves a JSON representation of the ContestFilter objects.
     *
     * @return
     */
    @Cacheable(value = "contestFilters")
    public String retrieveFiltersAsJson() throws JsonProcessingException {
        List<ContestFilter> contestFilters = generateFilters();
        return new ObjectMapper().writeValueAsString(contestFilters);
    }

    /**
     * Generates a list of ContestFilter objects based on the filtering criteria that lives in the database..
     *
     * @return A List of ContestFilter objects.
     */
    public List<ContestFilter> generateFilters() {
        ArrayList<ContestFilter> filters = new ArrayList<>();

        for (League league : League.ALL_LEAGUES) {
            if(!league.isActive()) {
                continue;
            }

            ContestFilter filter = new ContestFilter();
            filter.setName(league.getAbbreviation());

            List<ContestEntryFee> contestEntryFees = DaoFactory.getContestDao().findContestEntryFees(league);
            List<Integer> contestEntryFeeInts = contestEntryFees.stream().map(contestEntryFee -> contestEntryFee.getEntryFee()).collect(Collectors.toList());
            filter.setEntryFee(contestEntryFeeInts);

            // Number of users
            List<ContestNumberOfUsers> numberOfUsersList = DaoFactory.getContestDao().findContestNumberOfUsers(league);
            filter.setNumPlayers(numberOfUsersList);

            // Grouping
            List<ContestGrouping> contestGroupingList = DaoFactory.getContestDao().findContestGroupings(league);
            filter.setGrouping(contestGroupingList);

            // Salary caps
            List<ContestSalary> contestSalaryList = DaoFactory.getContestDao().findContestSalarys(league);
            List<Integer> contestSalaryIntList = contestSalaryList.stream().map(contestSalary -> contestSalary.getSalary()).collect(Collectors.toList());
            filter.setSalaryCap(contestSalaryIntList);

            filters.add(filter);
        }

        /*
         * Add the "ALL" filter, which is just a union of the others.
         */
        ContestFilter allFilter = new ContestFilter();
        allFilter.setName(ContestFilter.FILTER_TYPE_ALL);

        Set<Integer> contestEntryFeeSet = new HashSet<>();
        Set<ContestGrouping> contestGroupingSet = new HashSet<>();
        Set<Integer> contestSalarySet = new HashSet<>();
        List<ContestNumberOfUsers> contestNumberOfUsersList = new ArrayList<>();

        for (ContestFilter contestFilter : filters) {
            contestEntryFeeSet.addAll(contestFilter.getEntryFee().stream().collect(Collectors.toList()));

            /*
             * Iterate through the ContestNumberOfUsers list for this filter and check to make sure it doesn't
             * already exist in our master list of ContestNumberOfUsers.  We can't take the Set approach like the
             * other entities because they are either Strings or Integers, whereas this is a ContestNumberOfUsers
             * object and the equals() method takes League into account.  Therefore, we could get duplicates if
             * the same min/max values span multiple leagues.
             */
            for (ContestNumberOfUsers contestNumberOfUsers : contestFilter.getNumPlayers()) {
                boolean exists = false;
                for (ContestNumberOfUsers contestNumberOfUsers1 : contestNumberOfUsersList) {
                    if (contestNumberOfUsers.getMinimum() == contestNumberOfUsers1.getMinimum()
                            && contestNumberOfUsers.getMaximum() == contestNumberOfUsers1.getMaximum()) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    contestNumberOfUsersList.add(contestNumberOfUsers);
                }
            }

            contestGroupingSet.addAll(contestFilter.getGrouping());
            contestSalarySet.addAll(contestFilter.getSalaryCap().stream().collect(Collectors.toList()));
        }

        allFilter.getEntryFee().addAll(contestEntryFeeSet);
        Collections.sort(allFilter.getEntryFee());
        allFilter.getGrouping().addAll(contestGroupingSet);
        allFilter.getNumPlayers().addAll(contestNumberOfUsersList);
        allFilter.getSalaryCap().addAll(contestSalarySet);

        filters.add(0, allFilter);

        return filters;
    }

    /**
     * Generates the JSON representation of the contest filters that the client is expecting.
     *
     * @return A string representation of the contest filters.
     */
    @Cacheable(value = "generateContestFilters")
    public String generateFiltersAsJson() {
        JSONArray array = new JSONArray();

        try {
            for (League league : League.ALL_LEAGUES) {
                JSONObject leagueFilter = new JSONObject();
                leagueFilter.put("name", league.getAbbreviation());
                leagueFilter.put("selected", "false");
                leagueFilter.put("active", "false");

                // Entry fees
                List<ContestEntryFee> entryFeeList = DaoFactory.getContestDao().findContestEntryFees(league);
                JSONArray feesArray = new JSONArray();
                for (ContestEntryFee contestEntryFee : entryFeeList) {
                    feesArray.put(contestEntryFee.getEntryFee());
                }
                leagueFilter.put("entryFee", feesArray);

                // Number of users
                List<ContestNumberOfUsers> numberOfUsersList = DaoFactory.getContestDao().findContestNumberOfUsers(league);
                JSONArray numUsersArray = new JSONArray();
                for (ContestNumberOfUsers numberOfUsers : numberOfUsersList) {
                    JSONObject numUsersObject = new JSONObject()
                            .put("min", numberOfUsers.getMinimum())
                            .put("max", numberOfUsers.getMaximum());
                    numUsersArray.put(numUsersObject);
                }
                leagueFilter.put("numPlayers", numUsersArray);

                // Grouping
                List<ContestGrouping> contestGroupingList = DaoFactory.getContestDao().findContestGroupings(league);
                JSONArray groupingArray = new JSONArray();
                for (ContestGrouping contestGrouping : contestGroupingList) {
                    groupingArray.put(contestGrouping.getName());
                }
                leagueFilter.put("grouping", groupingArray);

                // Salary caps
                List<ContestSalary> contestSalaryList = DaoFactory.getContestDao().findContestSalarys(league);
                JSONArray salaryArray = new JSONArray();
                for (ContestSalary contestSalary : contestSalaryList) {
                    salaryArray.put(contestSalary.getSalary());
                }
                leagueFilter.put("salaryCap", salaryArray);

                array.put(leagueFilter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return array.toString();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "contestFilters", allEntries = true),
            @CacheEvict(value = "generateContestFilters", allEntries = true)})
    public void flushAllCaches() {}
}
