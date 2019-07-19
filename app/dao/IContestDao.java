package dao;

import controllers.LineupValidationException;
import models.contest.*;
import models.sports.AthleteSportEventInfo;
import models.sports.League;
import models.sports.SportEventGrouping;
import models.user.User;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Created by mwalsh on 6/27/14.
 */
public interface IContestDao {

    // Save, Update, and Delete methods for hooking dependent actions into /////////////////////////////////////////////

    /**
     * Saves LineupTemplate to the DB
     *
     * @param template
     */
    public void saveLineupTemplate(LineupTemplate template);

    /**
     * Saves Lineup to the DB
     *
     * @param lineup
     */
    public void saveLineup(Lineup lineup);

    /**
     * Updates Lineup to the DB
     *
     * @param lineup
     */
    public void updateLineup(Lineup lineup);

    /**
     * Saves Entry to the DB
     *
     * @param entry
     */
    public void saveEntry(Entry entry);

    /**
     * Updates Entry to the DB
     *
     * @param entry
     */
    public void updateEntry(Entry entry);

    /**
     * Performs a query to update the fantasy points for all entries that have the provided AthleteSportEventInfo
     * in their lineup.
     *
     * @param entries     The entries to update.
     */
    void updateEntryFantasyPoints(List<Entry> entries);

    /**
     * Saves a LineupTemplate to the DB
     *
     * @param template
     */
    public void deleteLineupTemplate(LineupTemplate template);

    /**
     * Saves a ContestType to the DB
     *
     * @param type
     */
    public void saveContestType(ContestType type);

    /**
     * Saves ContestGrouping to the DB
     *
     * @param grouping
     */
    public void saveContestGrouping(ContestGrouping grouping);

    /**
     * Saves ScoringRule to the DB
     *
     * @param rule
     */
    public void saveScoringRule(ScoringRule rule);

    /**
     * Saves a ContestTemplate to the DB
     *
     * @param template
     */
    public void saveContestTemplate(ContestTemplate template);

    /**
     * Saves ContestEntryFee to the DB
     *
     * @param contestEntryFee
     */
    public void saveContestEntryFee(ContestEntryFee contestEntryFee);

    /**
     * Deletes ContestEntryFee from the DB
     *
     * @param contestEntryFee
     */
    public void deleteContestEntryFee(ContestEntryFee contestEntryFee);

    /**
     * Saves ContestSalary to the DB
     *
     * @param contestSalary
     */
    public void saveContestSalary(ContestSalary contestSalary);

    /**
     * Saves ContestNumberOfUsers to the DB
     *
     * @param contestNumberOfUsers
     */
    public void saveContestNumberOfUsers(ContestNumberOfUsers contestNumberOfUsers);

    /**
     * Deletes ContestNumberOfUsers from the DB
     *
     * @param contestNumberOfUsers
     */
    public void deleteContestNumberOfUsers(ContestNumberOfUsers contestNumberOfUsers);

    /**
     * @param contest
     */
    public void updateContest(Contest contest);

    /**
     * @param user
     * @param contest
     * @param lineup
     * @return Status code associated with success/failure of joining a contest
     */
    public int joinContest(User user, Contest contest, Lineup lineup);

    /**
     * @param contest
     */
    public void closeContest(Contest contest);

    /**
     * Removes an entry associated with the provided lineup from the contest.
     *
     * @param user    The user whose entries we want to remove.
     * @param contest The contest to remove the entries from.
     * @param lineup  The lineup that the entries belong to.
     * @param count   The number of entries to remove.
     * @return
     */
    int removeFromContest(User user, Contest contest, Lineup lineup, int count);

    /**
     * Reserves an Entry within a contest
     *
     * @param user
     * @param contest
     * @param multiplier
     */
    public void reserveEntries(User user, Contest contest, int multiplier);

    /**
     * Deletes and entry from a contest
     *
     * @param entry
     */
    void deleteEntry(Entry entry);

    /**
     * Deletes a list of entries from a contest.
     *
     * @param entries
     */
    void deleteEntries(List<Entry> entries);

    // find helpers ////////////////////////////////////////////////////////////////////////////////////////////////////

    /**
     * @param id
     * @return Contest by internal ID
     */
    public Contest findContest(Integer id);

    /**
     * @return List of Contests that are not canceled or completed
     */
    public List<Contest> findNonTerminalContests();

    /**
     * @return List of all ContestTemplates
     */
    public List<ContestTemplate> findAllContestTemplates();

    /**
     * @param user
     * @param contest
     * @return List of Entry objects based on AppUser and Contest
     */
    public List<Entry> findEntries(User user, Contest contest);

    public List<Entry> findEntries(User user);

    /**
     * Find all entries belonging to the provided lineup.
     *
     * @param lineup The lineup that the entries belong to.
     * @return
     */
    public List<Entry> findEntries(Lineup lineup);

    /**
     * Find a list of entries by their ids.
     *
     * @param entryIds The ids of entries we want.
     * @return
     */
    public List<Entry> findEntries(List<Integer> entryIds);

    /**
     * @param league
     * @return List of ContestEntryFee by Leage
     */
    public List<ContestEntryFee> findContestEntryFees(League league);

    /**
     * @param league
     * @return List of ContestNumberOfUsers by League
     */
    public List<ContestNumberOfUsers> findContestNumberOfUsers(League league);

    /**
     * @param league
     * @return List of ContestGrouping by League
     */
    public List<ContestGrouping> findContestGroupings(League league);

    /**
     * @param league
     * @return List of ContestSalary by League
     */
    public List<ContestSalary> findContestSalarys(League league);

    /**
     * @return List of all LineupTemplates
     */
    public List<LineupTemplate> findAllLineupTemplates();

    /**
     * @return List of all ContestTypes
     */
    public List<ContestType> findAllContestTypes();

    /**
     * @return List of all ContestGroupings
     */
    public List<ContestGrouping> findAllContestGroupings();

    public void validateLineup(Lineup lineup, int salaryCap, List<LineupSpot> lineupSpots) throws LineupValidationException;

    public Lineup findLineup(int id);

    public Contest findContest(String urlId);

    public List<Contest> findContests(User user, List<ContestState> states);

    public List<Contest> findContests(ContestState state);

    public List<Contest> findContests(SportEventGrouping sportEventGrouping, List<ContestState> states);

    public List<Contest> findContests(SportEventGrouping sportEventGrouping, List<ContestState> states, League league, int entryFee, ContestType contestType, int allowedEntries);

    public List<Entry> findEntries(Contest contest);

    public int calculateUnitsRemaining(Entry entry);

    public List<Entry> findEntries(User user, List<ContestState> states);

    public List<Entry> findHistoricalEntries(User user, ContestState state, Date date);

    public List<Entry> findEntries(AthleteSportEventInfo athleteSportEventInfo);

    public List<Entry> findEntries(Lineup lineup, Contest contest);

    public Entry findEntry(int id);

    public List<Entry> findEntriesAndSort(Contest contest);

    public List<Lineup> findLineups(User user, List<ContestState> contestStates);

    public List<Lineup> findLineups(User user, List<ContestState> contestStates, boolean distinct);

    public List<Lineup> findHistoricalLineups(User user, ContestState state, Date date);

    public List<Lineup> findLineups(User user, Contest contest);

    public List<Lineup> findLineups(User user, SportEventGrouping grouping);

    void removeLineupSpots(Lineup lineup);

    public int calculateUnitsRemaining(Lineup lineup);

    public List<Contest> findContests(Lineup lineup, List<ContestState> contestStates);

    public BigDecimal findProjection(Lineup lineup);

    public List<LineupTemplate> findLineupTemplates(League league);

    public List<ScoringRule> findScoringRules(League league);

    public void saveContestSuggestion(ContestSuggestion contestSuggestion);

    public void deleteContestSuggestion(ContestSuggestion contestSuggestion);

    public List<ContestSuggestion> findContestSuggestions();

    public List<ContestState> findContestStates();

    void saveContest(Contest contest);

    void saveSportEventGrouping(SportEventGrouping sportEventGrouping);

    void cancelContest(Contest contest);

    void createNewOpenContest(ContestType contestType, League league, String displayName, int capacity,
                              boolean isPublic, int entryFee, int allowedEntries,
                              int salaryCap, SportEventGrouping sportEventGrouping,
                              List<ContestPayout> contestPayouts, ContestTemplate contestTemplate);

    void createNewContest(ContestType contestType, League league, String displayName, int capacity,
                          boolean isPublic, int entryFee, int allowedEntries,
                          int salaryCap, SportEventGrouping sportEventGrouping,
                          List<ContestPayout> contestPayouts, ContestTemplate o);
}
