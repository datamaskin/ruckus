package models.contest;

import com.fasterxml.jackson.annotation.JsonIgnore;
import common.GlobalConstants;
import dao.DaoFactory;
import dao.IContestDao;
import models.sports.League;
import models.sports.SportEventGrouping;
import models.user.User;
import org.springframework.context.ApplicationContext;
import stats.translator.IFantasyPointTranslator;

import javax.persistence.*;
import java.util.*;

/**
 * Created by dan on 4/10/14.
 */
@Entity
@SuppressWarnings("serial")
public class Contest {

    public static final String CONTEST_ID = "id";
    public static final String URL_ID = "url_id";
    public static final String TYPE_ID = "contest_type_id";
    public static final String LEAGUE_ID = "league_id";
    public static final String CURRENT_ENTRIES = "current_entries";
    public static final String CAPACITY = "capacity";
    public static final String IS_PUBLIC = "is_public";
    public static final String ENTRY_FEE = "entry_fee";
    public static final String GUARANTEED = "guaranteed";
    public static final String ALLOWED_ENTRIES = "allowed_entries";
    public static final String SPORT_EVENT_GROUPING = "sport_event_grouping_id";
    public static final String SALARY_CAP = "salary_cap";
    public static final String START_TIME = "start_time";
    public static final String RECONCILED_TIME = "reconciled_time";
    public static final String CREATED_FROM = "created_from_id";
    public static final String CONTEST_STATE = "contest_state_id";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = CONTEST_ID)
    private int id;

    @Column(unique = true, nullable = false, name = URL_ID)
    private String urlId;

    @Column(nullable = false, name = "display_name")
    private String displayName;

    @ManyToOne
    @Column(nullable = false, name = TYPE_ID)
    private ContestType contestType;

    @ManyToOne
    @Column(nullable = false, name = LEAGUE_ID)
    private League league;

    @Column(columnDefinition = "integer not null default 0", name = CURRENT_ENTRIES)
    private int currentEntries;

    @Column(columnDefinition = "integer not null default 0", name = CAPACITY)
    private int capacity;

    @Column(nullable = false, name = IS_PUBLIC)
    private boolean isPublic;

    @Column(columnDefinition = "integer not null default 0", name = ENTRY_FEE)
    private int entryFee;

    @Column(nullable = false, name = GUARANTEED)
    private boolean guaranteed;

    @Column(nullable = false, name = ALLOWED_ENTRIES)
    private int allowedEntries;

    @Column(nullable = false, name = SPORT_EVENT_GROUPING)
    @ManyToOne
    private SportEventGrouping sportEventGrouping;

    @Column(nullable = false, name = SALARY_CAP)
    private int salaryCap;

    @Column(nullable = false, name = START_TIME)
    private Date startTime;

    @Column(nullable = true, name = RECONCILED_TIME)
    private Date reconciledTime;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ContestPayout> contestPayouts;

    @ManyToOne
    @Column(name = CREATED_FROM, nullable = true)
    private ContestTemplate createdFrom;

    @ManyToOne
    @Column(name = CONTEST_STATE, nullable = true)
    @JsonIgnore
    private ContestState contestState;

    /**
     * Constructor with all parameters.
     *
     * @param contestType
     * @param capacity
     * @param isPublic
     * @param entryFee
     * @param allowedEntries
     */
    public Contest(ContestType contestType, String urlId, League league,
                   int capacity, boolean isPublic,
                   int entryFee, int allowedEntries,
                   int salaryCap,
                   SportEventGrouping sportEventGrouping,
                   List<ContestPayout> contestPayouts,
                   ContestTemplate createdFrom) {
        this.currentEntries = 0;
        this.contestType = contestType;
        this.urlId = urlId;
        this.displayName = "";
        this.league = league;
        this.capacity = capacity;
        this.isPublic = isPublic;
        this.entryFee = entryFee;
        this.allowedEntries = allowedEntries;
        this.salaryCap = salaryCap;
        this.sportEventGrouping = sportEventGrouping;
        this.contestPayouts = contestPayouts;
        this.createdFrom = createdFrom;
        this.startTime = sportEventGrouping.getEventDate();
        this.contestState = new ContestStateUninitialized();
    }

    /**
     * Convenience method for calculating the total prize pool for a contest.
     *
     * @return The sum of the prizes for the contest.
     */
    public int calculatePrizePool() {
        int payout = 0;
        for (ContestPayout cp : contestPayouts) {
            for(int index = cp.getLeadingPosition(); index <= cp.getTrailingPosition(); index++){
                payout += cp.getPayoutAmount();
            }
        }

        return payout;
    }

    public Date getReconciledTime() {
        return reconciledTime;
    }

    public void setReconciledTime(Date reconciledTime) {
        this.reconciledTime = reconciledTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getUrlId() {
        return urlId;
    }

    public void setUrlId(String urlId) {
        this.urlId = urlId;
    }

    public ContestType getContestType() {
        return contestType;
    }

    public void setContestType(ContestType contestType) {
        this.contestType = contestType;
    }

    public League getLeague() {
        return league;
    }

    public void setLeague(League league) {
        this.league = league;
    }

    public int getCurrentEntries() {
        return currentEntries;
    }

    public void setCurrentEntries(int currentEntries) {
        this.currentEntries = currentEntries;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
    }

    public int getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(int entryFee) {
        this.entryFee = entryFee;
    }

    public boolean isGuaranteed() {
        return guaranteed;
    }

    public void setGuaranteed(boolean guaranteed) {
        this.guaranteed = guaranteed;
    }

    public int getAllowedEntries() {
        return allowedEntries;
    }

    public void setAllowedEntries(int allowedEntries) {
        this.allowedEntries = allowedEntries;
    }

    public int getSalaryCap() {
        return salaryCap;
    }

    public void setSalaryCap(int salaryCap) {
        this.salaryCap = salaryCap;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public SportEventGrouping getSportEventGrouping() {
        return sportEventGrouping;
    }

    public void setSportEventGrouping(SportEventGrouping sportEventGrouping) {
        this.sportEventGrouping = sportEventGrouping;
    }

    public ContestTemplate getCreatedFrom() {
        return createdFrom;
    }

    public List<ContestPayout> getContestPayouts() {
        return contestPayouts;
    }

    public void setContestPayouts(List<ContestPayout> contestPayouts) {
        this.contestPayouts = contestPayouts;
    }

    public ContestState getContestState() {
        return contestState;
    }

    public void setContestState(ContestState contestState) {
        this.contestState = contestState;
    }

    public void proceedNext() {
        contestState = contestState.proceed();
    }

    public void proceedError() {
        contestState = contestState.error();
    }

    /**
     * Determine a list of contests to suggest to a user based on the contest they attempted to join (successfully or unsuccessfully).
     *
     * @param user      The user making the request.
     * @param status The status code of a previous lineup submission attempt.
     * @param lineup    The lineup that would be joining the contest.
     * @return
     */
    public Map<String, Object> getSuggestedContests(User user, int status, Lineup lineup) {
        List<Contest> alreadyEntered = DaoFactory.getContestDao().findContests(user, Arrays.asList(ContestState.open));
        boolean success = status == GlobalConstants.CONTEST_ENTRY_SUCCESS;
        List<Contest> suggestions = new ArrayList<>();
        IContestDao contestDao = DaoFactory.getContestDao();
        List<Contest> openContests = contestDao.findContests(sportEventGrouping, Arrays.asList(ContestState.open));
        List<ContestSuggestion> contestSuggestions = contestDao.findContestSuggestions();

        Map<String, Object> suggestionsMap = new HashMap<>();
        suggestionsMap.put("additionalContests", suggestions);

        // If the entry succeeded, and the contest is a multi entry, always put an option to re-enter the contest first
        if (success && allowedEntries > 1 && contestState.equals(ContestState.open)) {
            suggestions.add(this);
        }

        // If the entry failed because it's a single entry tournament, or the contest filled, always put Same Sport,
        // Same Buy-in, Same Format (Double-Up / Standard / H2H) vs. Same #
        else if (status == GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_FULL || status == GlobalConstants.CONTEST_ENTRY_ERROR_SINGLE_ENTRY_DUPE) {
            addContestSuggestionByCriteria(openContests, suggestions, alreadyEntered, league, entryFee, contestType, capacity, lineup, user);
            if (!suggestions.isEmpty()) {
                suggestionsMap.put("duplicateContest", suggestions.get(0));
                suggestions.clear();
            }
        }

        for (ContestSuggestion contestSuggestion : contestSuggestions) {
            if (contestSuggestion.getContestType().equals(contestType)) {
                /*
                 * Double-ups and Normal need to match on entrants, others don't.
                 */
                if (((contestType.equals(ContestType.DOUBLE_UP) || contestType.equals(ContestType.NORMAL)) &&
                        contestSuggestion.getCapacity() == capacity) ||
                        (!contestType.equals(ContestType.DOUBLE_UP) && !contestType.equals(ContestType.NORMAL))) {
                    addContestSuggestionByCriteria(openContests, suggestions, alreadyEntered, league, entryFee,
                            contestSuggestion.getSuggestionContestType(), contestSuggestion.getSuggestionCapacity(), lineup, user);
                }
            }
        }

        /*
         * We don't want to have nothing to suggest to a user.  If the suggestions are empty
         * then grab three random contests from the lobby that are open.
         */
        if(suggestions.isEmpty()) {
            for(Contest contest: openContests) {
                if(contest.getContestType().equals(ContestType.NORMAL) || contest.getContestType().equals(ContestType.DOUBLE_UP)) {
                    suggestions.add(contest);
                }

                if(suggestions.size() == 3) {
                    break;
                }
            }
        }

        return suggestionsMap;
    }

    /**
     * Finds a contest from the provided list that satisfies the passed-in criteria and adds it to the suggestions list.
     *
     * @param contests    The list of contests to search.
     * @param suggestions The list of contests to add to if a suggestion is found.
     * @param alreadyEntered    The list of contests that have already been entered by this user.
     * @param league      The league type of the desired suggestion.
     * @param entryFee    The entry fee of the desired suggestion.
     * @param contestType The contest type of the desired suggestion.
     * @param capacity    The number of entries allowed for the desired suggestion.
     * @param lineup      The lineup that would be joining the contest.
     * @param user        The user making the request.
     */
    private void addContestSuggestionByCriteria(List<Contest> contests, List<Contest> suggestions, List<Contest> alreadyEntered, League league,
                                                int entryFee, ContestType contestType, int capacity, Lineup lineup, User user) {

        Contest bestGpp = null;
        for (Contest contest : contests) {
            /*
             * Something besides a GPP
             */
            if (!contestType.equals(ContestType.GPP)) {
                if (contest.getLeague().equals(league) &&
                        contest.getEntryFee() == entryFee &&
                        contest.getContestType().equals(contestType) &&
                        contest.getCapacity() == capacity &&
                        ((contest.getAllowedEntries() > 1) || !alreadyEntered.contains(contest))) {
                    suggestions.add(contest);
                    return;
                }
            /*
             * Make sure the contest suggestion (contestType) is a GPP and the open contest we're evaluating (contest) is also a GPP.
             */
            } else if (contestType.equals(ContestType.GPP) && contest.getContestType().equals(ContestType.GPP)) {
                /*
                 * To determine the best GPP, we want to find the one closest to the entered contest by entry fee (less than entered contest).
                 * We also need to ensure that the contest has been entered fewer times than is allowed.  We can't be suggesting contests that
                 * the user can't enter.
                 */
                if ((bestGpp == null || Math.abs(contest.getEntryFee() - entryFee) < Math.abs(bestGpp.entryFee - entryFee)) &&
                                (contest.getAllowedEntries() > DaoFactory.getContestDao().findEntries(user, contest).size()) ) {
                    bestGpp = contest;
                }
            }
        }

        if (bestGpp != null) {
            suggestions.add(bestGpp);
        }
    }

    /**
     * Determines how many entries this user can still enter into the provided contest.
     *
     * @param user The user trying to enter the contest.
     * @return The difference between the max allowable entries for each user,
     * and the number of entries the user currently has in the contest.
     */
    public int calculateRemainingAllowedEntries(User user) {
        List<Entry> entries = DaoFactory.getContestDao().findEntries(user, this);
        return allowedEntries - entries.size();
    }

    /**
     * Determines the appropriate IStatsFantasyPointTranslator instance to use based on the League.
     *
     * @param context       The application context containing the injectable beans.
     * @return              An instance of IStatsFantasyPointTranslator, or null if there is no League match.
     */
    @JsonIgnore
    public IFantasyPointTranslator getStatsFantasyPointTranslator(ApplicationContext context) {
        if(league.equals(League.NFL)) {
            return context.getBean("NFLFantasyPointTranslator", IFantasyPointTranslator.class);
        }
        else if(league.equals(League.MLB)) {
            return context.getBean("MLBFantasyPointTranslator", IFantasyPointTranslator.class);
        }

        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Contest contest = (Contest) o;

        if (id != contest.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
