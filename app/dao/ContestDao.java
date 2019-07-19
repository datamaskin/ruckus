package dao;

import com.avaje.ebean.*;
import com.hazelcast.core.ILock;
import common.GlobalConstants;
import controllers.LineupValidationDuplicateAthleteException;
import controllers.LineupValidationException;
import distributed.DistributedServices;
import distributed.tasks.CachePopulatorTask;
import models.contest.*;
import models.sports.AthleteSportEventInfo;
import models.sports.League;
import models.sports.SportEvent;
import models.sports.SportEventGrouping;
import models.user.User;
import models.wallet.UserWallet;
import models.wallet.UserWalletTxn;
import models.wallet.VictivTxnType;
import play.Logger;
import service.ContestEntriesService;
import service.ContestLiveRanksService;
import service.EdgeCacheService;
import service.IContestListService;
import utils.IContestIdGenerator;
import wallet.WalletException;

import java.math.BigDecimal;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by mwalsh on 6/27/14.
 */
public class ContestDao extends AbstractDao implements IContestDao {

    private IContestIdGenerator contestIdGenerator;

    public ContestDao(IContestIdGenerator contestIdGenerator) {
        this.contestIdGenerator = contestIdGenerator;
    }

    @Override
    public void updateEntry(Entry entry) {
        update(entry);
    }

    @Override
    public void updateEntryFantasyPoints(List<Entry> entries) {
        if(entries.isEmpty()) {
            return;
        }

        StringBuilder inClause = new StringBuilder();
        for(Entry entry: entries) {
            if(inClause.length() > 0) {
                inClause.append(",");
            }
            inClause.append(entry.getId());
        }

        String sql = "update entry e " +
                "inner join ( " +
                    "select e1.id as id, sum(asei.fantasy_points) as total_points " +
                    "from athlete_sport_event_info asei " +
                    "inner join lineup_spot ls on asei.id = ls.athlete_sport_event_info_id " +
                    "inner join entry e1 on e1.lineup_id = ls.lineup_id " +
                    "where e1.id in (" + inClause.toString() + ") " +
                    "group by e1.id " +
                ") b on e.id = b.id " +
                "set e.points = b.total_points";

        SqlUpdate sqlUpdate = Ebean.createSqlUpdate(sql);
        sqlUpdate.execute();
    }

    @Override
    public void saveEntry(Entry entry) {
        save(entry);
    }

    @Override
    public void saveContest(Contest contest) {
        save(contest);
        IContestListService contestService = DistributedServices.getContext().getBean("ContestListManager", IContestListService.class);
        try {
            contestService.notifyOfNewContest(contest.getUrlId());
            DistributedServices.getTaskScheduler().submit(new CachePopulatorTask());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void saveSportEventGrouping(SportEventGrouping sportEventGrouping) {
        Ebean.save(sportEventGrouping);
    }

    @Override
    public void saveContestSuggestion(ContestSuggestion contestSuggestion) {
        save(contestSuggestion);
    }

    @Override
    public void deleteContestSuggestion(ContestSuggestion contestSuggestion) {
        delete(contestSuggestion);
    }

    @Override
    public List<ContestSuggestion> findContestSuggestions() {
        return Ebean.find(ContestSuggestion.class).findList();
    }

    @Override
    public List<ContestState> findContestStates() {
        return Ebean.find(ContestState.class).findList();
    }

    @Override
    public void updateLineup(Lineup lineup) {
        update(lineup);
    }

    @Override
    public void saveLineup(Lineup lineup) {
        save(lineup);
    }

    @Override
    public void saveScoringRule(ScoringRule rule) {
        save(rule);
    }

    @Override
    public void saveContestTemplate(ContestTemplate template) {
        save(template);
    }

    @Override
    public void saveContestSalary(ContestSalary contestSalary) {
        save(contestSalary);
    }

    @Override
    public void deleteContestNumberOfUsers(ContestNumberOfUsers contestNumberOfUsers) {
        delete(contestNumberOfUsers);
    }

    @Override
    public void saveContestNumberOfUsers(ContestNumberOfUsers contestNumberOfUsers) {
        save(contestNumberOfUsers);
    }

    @Override
    public void deleteContestEntryFee(ContestEntryFee contestEntryFee) {
        delete(contestEntryFee);
    }

    @Override
    public void saveContestEntryFee(ContestEntryFee contestEntryFee) {
        save(contestEntryFee);
    }

    @Override
    public void saveLineupTemplate(LineupTemplate template) {
        save(template);
    }

    @Override
    public void deleteLineupTemplate(LineupTemplate template) {
        delete(template);
    }

    @Override
    public void saveContestType(ContestType type) {
        save(type);
    }

    @Override
    public void saveContestGrouping(ContestGrouping grouping) {
        save(grouping);
    }

    @Override
    public Contest findContest(Integer id) {
        return Ebean.find(Contest.class, id);
    }

    @Override
    public List<Contest> findNonTerminalContests() {
        return Ebean.find(Contest.class).where()
                .and(
                        Expr.ne(Contest.CONTEST_STATE, new ContestStateCancelled().getId()),
                        Expr.ne(Contest.CONTEST_STATE, new ContestStateHistory().getId()))
                .findList();
    }

    @Override
    public void updateContest(Contest contest) {
        Ebean.update(contest);
    }

    public int removeFromContest(User user, Contest contest, Lineup lineup, int count) {
        ILock lock = DistributedServices.getInstance().getLock(contest.getUrlId());
        lock.lock();
        Ebean.beginTransaction();
        try {
            // Is contest not open?
            if (!contest.getContestState().equals(ContestState.open)) {
                return GlobalConstants.CONTEST_ENTRY_ERROR_NOT_OPEN;
            }

            // Remove entry from contest
            List<Entry> entries = findEntries(lineup, contest);
            int numDeleted = 0;
            while (numDeleted < count && !entries.isEmpty()) {
                deleteEntry(entries.get(0));
                entries.remove(0);
                numDeleted++;
            }

            // Refund money
            IUserDao userDao = DaoFactory.getUserDao();
            final UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
            final int refund = numDeleted * contest.getEntryFee();
            final UserWalletTxn txn = UserWalletTxn.victivTxn(wallet, refund, (wallet.getUsd() + refund),
                    "Refund from witdrawing " + numDeleted + " entries from contest", VictivTxnType.CONTEST_WITHDRAWAL,
                    contest);
            userDao.plusUsd(user, refund);
            DaoFactory.getWalletDao().save(txn);

            // Update entry count
            contest.setCurrentEntries(contest.getCurrentEntries() - numDeleted);
            save(contest);


            Ebean.commitTransaction();

            // Update caches
            IContestListService contestCache = context.getBean("ContestListManager", IContestListService.class);
            ContestEntriesService entriesCache = (ContestEntriesService) context.getBean("ContestEntriesManager");
            ContestLiveRanksService ranksCache = (ContestLiveRanksService) context.getBean("ContestLiveRanksManager");
            contestCache.updateContestEntries(contest.getUrlId());
            entriesCache.updateContestEntries(contest.getUrlId());
            ranksCache.userJoined(contest.getUrlId(), user);

            return GlobalConstants.CONTEST_ENTRY_SUCCESS;
        } catch (Exception e) {
            Logger.error("Exception: ", e);
            return GlobalConstants.CONTEST_ENTRY_ERROR_OTHER;
        } finally {
            Ebean.endTransaction();
            lock.unlock();
        }
    }

    private int enterLineupIntoContest(User user, Contest contest, Lineup lineup) {
        ILock lock = DistributedServices.getInstance().getLock(contest.getUrlId());
        lock.lock();
        Ebean.beginTransaction();
        try {
            //Money first
            try {
                IUserDao userDao = DaoFactory.getUserDao();
                final int fee = contest.getEntryFee();
                final UserWallet wallet = DaoFactory.getUserDao().getUserWallet(user);
                final UserWalletTxn txn = UserWalletTxn.victivTxn(wallet, fee, (wallet.getUsd() - fee),
                        "Contest entered", VictivTxnType.CONTEST_ENTRY, contest);
                userDao.minusUsd(user, contest.getEntryFee());
                DaoFactory.getWalletDao().save(txn);
            } catch (WalletException e) {
                return GlobalConstants.CONTEST_ENTRY_ERROR_INSUFFICIENT_FUNDS;
            }

            // Is the contest id invalid?
            if (contest == null) {
                return GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_ID;
            }

            // Is the lineup incompatible with the contest?
            if (contest.getSportEventGrouping().getId() != lineup.getSportEventGrouping().getId()) {
                return GlobalConstants.CONTEST_ENTRY_ERROR_INCOMPATIBLE_LINEUP;
            }

            // Is the contest full?
            if (contest.getContestState().equals(ContestState.locked) || contest.getContestState().equals(ContestState.rosterLocked)) {
                return GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_FULL;
            }

            // Is the contest active?
            if (contest.getContestState().equals(ContestState.active)) {
                return GlobalConstants.CONTEST_ENTRY_ERROR_CONTEST_STARTED;
            }

            // Is the contest not open for some other reason?
            if (contest.getContestState().getId() != ContestState.open.getId()) {
                return GlobalConstants.CONTEST_ENTRY_ERROR_NOT_OPEN;
            }

            // Is the user trying to re-enter a single-entry contest they've already entered?
            if (contest.getAllowedEntries() == 1 && !DaoFactory.getContestDao().findEntries(user, contest).isEmpty()) {
                return GlobalConstants.CONTEST_ENTRY_ERROR_SINGLE_ENTRY_DUPE;
            }

            // Is the lineup valid?
            try {
                validateLineup(lineup, contest.getSalaryCap(), lineup.getLineupSpots());

                // If we got here the lineup is valid, so save it.
                saveLineup(lineup);
            } catch (LineupValidationException e) {
                if (e.getMessage().equals(GlobalConstants.MINIMUM_SPORT_EVENTS_ERROR)) {
                    return GlobalConstants.CONTEST_ENTRY_ERROR_NOT_ENOUGH_SPORT_EVENTS;
                } else if (e.getMessage().equals(GlobalConstants.LINEUP_SIZE_INVALID_ERROR)) {
                    return GlobalConstants.CONTEST_ENTRY_ERROR_INVALID_LINEUP_SIZE;
                } else if (e instanceof LineupValidationDuplicateAthleteException) {
                    return GlobalConstants.CONTEST_ENTRY_ERROR_DUPLICATE_ATHLETES;
                } else {
                    Logger.error("Exception: ", e);
                    return GlobalConstants.CONTEST_ENTRY_ERROR_OTHER;
                }
            }

            if (contest.getCurrentEntries() < contest.getCapacity()) {
                contest.setCurrentEntries(contest.getCurrentEntries() + 1);

                //Once we increase the count of the entries, check if we're at capacity
                // and go to Locked.
                if (contest.getCurrentEntries() == contest.getCapacity()
                        && false == contest.getContestType().equals(ContestType.ANONYMOUS_H2H)) {
                    contest.proceedNext();
                    saveContest(contest);
                }

                Entry entry = new Entry(user, contest, lineup);
                if (lineup.getEntries() == null) {
                    lineup.setEntries(new ArrayList<>());
                }
                lineup.getEntries().add(entry);
                save(lineup);
                save(entry);
                update(contest);
                Ebean.commitTransaction();

                /*
                 * Update caches.
                 */
                EdgeCacheService edgeCacheService = new EdgeCacheService();
                IContestListService contestCache = DistributedServices.getContext().getBean("ContestListManager", IContestListService.class);
                ContestEntriesService entriesCache = (ContestEntriesService) DistributedServices.getContext().getBean("ContestEntriesManager");
                ContestLiveRanksService ranksCache = (ContestLiveRanksService) DistributedServices.getContext().getBean("ContestLiveRanksManager");
                if (contest.getContestState().equals(new ContestStateEntriesLocked())) {
                    contestCache.removeContest(contest.getUrlId());
                } else {
                    contestCache.updateContestEntries(contest.getUrlId());
                }
                entriesCache.updateContestEntries(contest.getUrlId());
                ranksCache.userJoined(contest.getUrlId(), user);
                edgeCacheService.evictOnContestEntry(user, entry);

                return GlobalConstants.CONTEST_ENTRY_SUCCESS;

            } else {
                return GlobalConstants.CONTEST_ENTRY_ERROR_NOT_OPEN;
            }

        } catch (Exception e) {
            Logger.error("Exception: ", e);
            Ebean.rollbackTransaction();
            return GlobalConstants.CONTEST_ENTRY_ERROR_OTHER;
        } finally {
            Ebean.endTransaction();
            lock.unlock();
        }
    }

    @Override
    public int joinContest(User user, Contest contest, Lineup lineup) {
        int result = enterLineupIntoContest(user, contest, lineup);

        //user entered a contest and was the first one
        if (result == GlobalConstants.CONTEST_ENTRY_SUCCESS && contest.getCurrentEntries() == 1) {
            //automatically replace the one just entered so there's at least one with entries == 0
            createNewContestFromExisting(contest);

            int max = 0;
            if (contest.getCapacity() == 6) {
                max = 6;
            } else if (contest.getCapacity() == 10) {
                max = 3;
            } else if (contest.getCapacity() == 20) {
                max = 1;
            }

            int existing = Ebean.getServer(GlobalConstants.DEFAULT_DATABASE).find(Contest.class)
                    .where()
                    .eq(Contest.CREATED_FROM, contest.getCreatedFrom().getId())
                    .eq(Contest.CONTEST_STATE, ContestState.open.getId())
                    .eq(Contest.CURRENT_ENTRIES, 0).findRowCount();

            //Add another if we're still under the max
            if (existing < max) {
                createNewContestFromExisting(contest);
            }

        }
        return result;
    }

    private void createNewContestFromExisting(Contest contest) {
        List<ContestPayout> payouts = contest.getContestPayouts().stream()
                .map(payout -> new ContestPayout(
                        payout.getLeadingPosition(),
                        payout.getTrailingPosition(),
                        payout.getPayoutAmount())).collect(Collectors.toList());

        createNewOpenContest(
                contest.getContestType(), contest.getLeague(), "",
                contest.getCapacity(), contest.isPublic(),
                contest.getEntryFee(), contest.getAllowedEntries(),
                contest.getSalaryCap(), contest.getSportEventGrouping(),
                payouts,
                contest.getCreatedFrom());
    }

    @Override
    public void closeContest(Contest contest) {
        try {
            Ebean.beginTransaction();
            updateContest(contest);

            applyPayouts(contest);

            Ebean.commitTransaction();
        } catch (Exception e) {
            Ebean.rollbackTransaction();
        } finally {
            Ebean.endTransaction();
        }
    }

    private void applyPayouts(Contest contest) {
        //Expand payout structure
        List<Integer> payoutsExp = new ArrayList<>();
        List<ContestPayout> payouts = contest.getContestPayouts();
        for (ContestPayout p : payouts) {
            for (int index = p.getLeadingPosition(); index <= p.getTrailingPosition(); index++) {
                payoutsExp.add(p.getPayoutAmount());
            }
        }

        //Sort Entries by points to determine winner
        List<Entry> entries = findEntries(contest);
        //TODO Use BigDecimal, talk with Dan
        Collections.sort(entries, (e1, e2) -> new Double(e2.getPoints()).compareTo(new Double(e1.getPoints())));

        //Bucket entries by points to determine places that have tied
        //Apply unadjusted payouts to each place
        //*Unadjusted payouts are before we apply tie-splitting adjustments
        Map<Double, Map<Entry, Integer>> map = new HashMap<>();
        int index = 0;
        for (Entry entry : entries) {
            Map<Entry, Integer> m1 = map.get(entry.getPoints());
            if (m1 == null) {
                m1 = new HashMap<>();
            }

            int unAdjPayout = 0;
            if (index < payoutsExp.size()) {
                unAdjPayout = payoutsExp.get(index);
            }

            m1.put(entry, unAdjPayout);
            map.put(entry.getPoints(), m1);
            index++;
        }

        //Apply tie-splitting adjustments
        //For each bucket, we need to average the total payout amongst entries in that bucket
        for (Map.Entry<Double, Map<Entry, Integer>> moop : map.entrySet()) {
            Map<Entry, Integer> value = moop.getValue();

            int runningTotal = 0;
            for (Entry entry : value.keySet()) {
                runningTotal += value.get(entry);
            }

            int payment = runningTotal / value.keySet().size();

            for (Entry entry : value.keySet()) {
                final UserWallet wallet = DaoFactory.getUserDao().getUserWallet(entry.getUser());
                final UserWalletTxn txn = UserWalletTxn.victivTxn(wallet, payment, (wallet.getUsd() + payment),
                        "Payout from contest", VictivTxnType.CONTEST_RESULT, contest);
                DaoFactory.getUserDao().plusUsd(entry.getUser(), payment);
                DaoFactory.getWalletDao().save(txn);
                ContestResults contestResults = new ContestResults(entry.getUser(), contest, entry, payment);
                Ebean.save(contestResults);
            }
        }
    }

    @Override
    public void cancelContest(Contest contest) {
        try {
            Ebean.beginTransaction();
            contest.proceedError();
            updateContest(contest);
            refundEntryFees(contest);
            Ebean.commitTransaction();
        } catch (Exception e) {
            Ebean.rollbackTransaction();
        } finally {
            Ebean.endTransaction();
        }
    }

    @Override
    public void createNewOpenContest(ContestType contestType, League league, String displayName, int capacity,
                                     boolean isPublic, int entryFee, int allowedEntries,
                                     int salaryCap, SportEventGrouping sportEventGrouping,
                                     List<ContestPayout> contestPayouts, ContestTemplate contestTemplate) {
        try {
            Contest newContest = new Contest(contestType, getUniqueId(), league, capacity, isPublic,
                    entryFee, allowedEntries, salaryCap, sportEventGrouping, contestPayouts, contestTemplate);
            newContest.setDisplayName(displayName);
            newContest.proceedNext();
            Ebean.save(newContest);
        } catch (IllegalArgumentException e) {
            Logger.error("Could not create contest.", e);
        }
    }

    @Override
    public void createNewContest(ContestType contestType, League league, String displayName, int capacity,
                                 boolean isPublic, int entryFee, int allowedEntries,
                                 int salaryCap, SportEventGrouping sportEventGrouping,
                                 List<ContestPayout> contestPayouts, ContestTemplate contestTemplate) {
        try {
            Contest newContest = new Contest(contestType, getUniqueId(), league, capacity, isPublic,
                    entryFee, allowedEntries, salaryCap, sportEventGrouping, contestPayouts, contestTemplate);
            newContest.setDisplayName(displayName);
            Ebean.save(newContest);
        } catch (IllegalArgumentException e) {
            Logger.error("Could not create contest.", e);
        }
    }

    private String getUniqueId() {
        int retries = 3;
        while (retries > 0) {
            String urlId = contestIdGenerator.generateString(8, IContestIdGenerator.alphaLower + IContestIdGenerator.alphaUpper + IContestIdGenerator.numeric, new SecureRandom());
            if (findContest(urlId) == null) {
                return urlId;
            }
            retries--;
        }
        throw new IllegalArgumentException("Could not retrieve unique contest ID after " + retries + " retries.");
    }

    private void refundEntryFees(Contest contest) {
        List<Entry> entries = findEntries(contest);
        for (Entry entry : entries) {
            User user = entry.getUser();
            final UserWallet wallet = DaoFactory.getUserDao().getUserWallet(entry.getUser());
            final int fee = contest.getEntryFee();
            final UserWalletTxn txn = UserWalletTxn.victivTxn(wallet, fee, (wallet.getUsd() + fee),
                    "Refund from cancelled contest", VictivTxnType.CONTEST_CANCEL, contest);
            IUserDao userDao = DaoFactory.getUserDao();
            userDao.plusUsd(user, fee);
            DaoFactory.getWalletDao().save(txn);
        }
    }

    @Override
    public List<ContestTemplate> findAllContestTemplates() {
        return Ebean.find(ContestTemplate.class)
                .fetch("contestTemplatePayouts")
                .where(Expr.eq(ContestTemplate.AUTO_POPULATE, true)).findList();
    }

    @Override
    public void reserveEntries(User user, Contest contest, int multiplier) {
        if (user.getId() == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        for (int i = 0; i < multiplier; i++) {
            Entry entry = new Entry(user, contest, null);
            Ebean.save(entry);
        }
    }

    @Override
    public void deleteEntry(Entry entry) {
        Ebean.delete(entry);
    }

    @Override
    public void deleteEntries(List<Entry> entries) {
        Ebean.delete(entries);
    }

    @Override
    public List<ContestEntryFee> findContestEntryFees(League league) {
        return Ebean.find(ContestEntryFee.class).where().eq("league", league).findList();
    }

    @Override
    public List<ContestNumberOfUsers> findContestNumberOfUsers(League league) {
        return Ebean.find(ContestNumberOfUsers.class).where().eq("league", league).findList();
    }

    @Override
    public List<ContestGrouping> findContestGroupings(League league) {
        return Ebean.find(ContestGrouping.class).where().eq("league", league).findList();
    }

    @Override
    public List<ContestSalary> findContestSalarys(League league) {
        return Ebean.find(ContestSalary.class).where().eq("league", league).findList();
    }

    @Override
    public List<LineupTemplate> findAllLineupTemplates() {
        return Ebean.find(LineupTemplate.class).findList();
    }

    @Override
    public List<ContestType> findAllContestTypes() {
        return Ebean.find(ContestType.class).findList();
    }

    @Override
    public List<ContestGrouping> findAllContestGroupings() {
        return Ebean.find(ContestGrouping.class).findList();
    }

    @Override
    public void validateLineup(Lineup lineup, int salaryCap, List<LineupSpot> lineupSpots)
            throws LineupValidationException {
        League league = lineup.getLeague();
        List<LineupTemplate> templates = findLineupTemplates(league);
        int totalLineupSpotsRequired = 0;
        for (LineupTemplate template : templates) {
            totalLineupSpotsRequired += template.getNumberOfAthletes();
        }
        if (lineupSpots.size() != totalLineupSpotsRequired) {
            throw new LineupValidationException(GlobalConstants.LINEUP_SIZE_INVALID_ERROR);
        }

        int totalSalary = 0;
        Set<Integer> sportEvents = new HashSet<>();
        List<Integer> athleteIds = new ArrayList<>();
        for (LineupSpot spot : lineupSpots) {
            // Check for athlete duplicates
            if (athleteIds.contains(spot.getAthlete().getId())) {
                throw new LineupValidationDuplicateAthleteException("Athlete " + spot.getAthlete().getFirstName() + " " + spot.getAthlete().getLastName() +
                        " was detected in multiple lineup spots.");
            }
            athleteIds.add(spot.getAthlete().getId());

            sportEvents.add(spot.getAthleteSportEventInfo().getSportEvent().getId());
            totalSalary += DaoFactory.getSportsDao().findAthleteSalary(spot.getAthlete(), lineup.getSportEventGrouping()).salary;

            for (LineupTemplate template : templates) {
                if (template.getPosition().equals(spot.getPosition())) {
                    template.setNumberOfAthletes(template.getNumberOfAthletes() - 1);
                    if (template.getNumberOfAthletes() < 0) {
                        throw new LineupValidationException("Too many " + spot.getPosition().getName());
                    }
                }
            }

            if (!league.equals(spot.getAthlete().getTeam().getLeague())) {
                throw new LineupValidationException("Athlete "
                        + spot.getAthlete().getFirstName() + " " + spot.getAthlete().getLastName()
                        + " does not play in league " + league.getAbbreviation());
            }
        }

        if (totalSalary > salaryCap) {
            throw new LineupValidationException(GlobalConstants.SALARY_CAP_EXCEEDED_ERROR);
        }

        if (!league.equals(League.NFL) && sportEvents.size() < 2) {
            throw new LineupValidationException(GlobalConstants.MINIMUM_SPORT_EVENTS_ERROR);
        }
    }

    @Override
    public Contest findContest(String urlId) {
        return Ebean.find(Contest.class).where().eq(Contest.URL_ID, urlId).findUnique();
    }

    /**
     * Retrieve all contests that a user has entered.
     *
     * @param user   The user whose contests we want to retrieve.
     * @param states
     * @return
     */
    @Override
    public List<Contest> findContests(User user, List<ContestState> states) {
        StringBuilder sb = new StringBuilder();
        for (ContestState s : states) sb.append((sb.length() == 0) ? s.getId() : ("," + s.getId()));
        String sql = "select c.id, c.url_id, c.contest_type_id, c.league_id, c.current_entries, c.capacity, c.is_public," +
                "c.entry_fee, c.guaranteed, c.allowed_entries, c.sport_event_grouping_id, c.salary_cap, c.start_time " +
                "from contest c inner join entry e on c.id = e.contest_id " +
                "where e.user_id = " + user.getId() + " and " +
                "c.contest_state_id in (" + sb.toString() + ")";

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("c.id", "id")
                .columnMapping("c.url_id", "urlId")
                .columnMapping("c.contest_type_id", "contestType.id")
                .columnMapping("c.league_id", "league.id")
                .columnMapping("c.current_entries", "currentEntries")
                .columnMapping("c.capacity", "capacity")
                .columnMapping("c.is_public", "isPublic")
                .columnMapping("c.entry_fee", "entryFee")
                .columnMapping("c.allowed_entries", "allowedEntries")
                .columnMapping("c.sport_event_grouping_id", "sportEventGrouping.id")
                .columnMapping("c.salary_cap", "salaryCap")
                .columnMapping("c.start_time", "startTime")
                .create();

        com.avaje.ebean.Query<Contest> query = Ebean.find(Contest.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    @Override
    public List<Contest> findContests(ContestState state) {
        return Ebean.find(Contest.class).where().eq(Contest.CONTEST_STATE, state.getId()).findList();
    }

    @Override
    public List<Contest> findContests(SportEventGrouping sportEventGrouping, List<ContestState> states) {
        return Ebean.find(Contest.class).where().eq("sportEventGrouping", sportEventGrouping).in("contestState", states).findList();
    }

    @Override
    public List<Contest> findContests(SportEventGrouping sportEventGrouping, List<ContestState> states, League league, int entryFee, ContestType contestType, int allowedEntries) {
        return Ebean.find(Contest.class).where()
                .eq("sportEventGrouping", sportEventGrouping)
                .in("contestState", states)
                .eq("league", league)
                .eq("entryFee", entryFee)
                .eq("contestType", contestType)
                .eq("allowedEntries", allowedEntries)
                .findList();
    }

    /**
     * Determine the amount of time units remaining for all athletes across the lineup for this entry.
     *
     * @return The sum of the time units remaining for this entry.
     */
    @Override
    public int calculateUnitsRemaining(Entry entry) {
        String sql = "select se.id, se.stat_provider_id, se.league_id, se.start_time, se.description, se.short_description, se.units_remaining " +
                "from entry e inner join lineup_spot ls on ls.lineup_id = e.lineup_id " +
                "inner join athlete_sport_event_info asei on asei.athlete_id = ls.athlete_id " +
                "inner join sport_event se on asei.sport_event_id = se.id " +
                "where ls.athlete_sport_event_info_id = asei.id and e.id = " + entry.getId();

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("se.id", "id")
                .columnMapping("se.stat_provider_id", "statProviderId")
                .columnMapping("se.league_id", "league.id")
                .columnMapping("se.start_time", "startTime")
                .columnMapping("se.description", "description")
                .columnMapping("se.short_description", "shortDescription")
                .columnMapping("se.units_remaining", "unitsRemaining")
                .create();

        com.avaje.ebean.Query<SportEvent> query = Ebean.find(SportEvent.class);
        query.setRawSql(rawSql);
        List<SportEvent> sportEvents = query.findList();

        int unitsRemaining = 0;
        for (SportEvent sportEvent : sportEvents) {
            unitsRemaining += sportEvent.getUnitsRemaining();
        }

        return unitsRemaining;
    }

    /**
     * Convenience method for finding the contest entries for a particular user.
     *
     * @param user The user whose entries we want.
     * @return A list of the entries for the provided user.
     */
    @Override
    public List<Entry> findEntries(User user, List<ContestState> states) {
        if (states != null) {
            StringBuilder sb = new StringBuilder();
            for (ContestState s : states) sb.append((sb.length() == 0) ? s.getId() : ("," + s.getId()));
            String sql = "select e.id, e.user_id, e.contest_id, e.points " +
                    "from entry e inner join contest c on e.contest_id = c.id " +
                    "where e.user_id = " + user.getId() + " and " +
                    "c.contest_state_id in (" + sb.toString() + ")";

            RawSql rawSql = RawSqlBuilder.parse(sql)
                    .columnMapping("e.id", "id")
                    .columnMapping("e.user_id", "user.id")
                    .columnMapping("e.contest_id", "contest.id")
                    .columnMapping("e.points", "points")
                    .create();

            com.avaje.ebean.Query<Entry> query = Ebean.find(Entry.class);
            query.setRawSql(rawSql);
            return query.findList();
        } else {
            return Ebean.find(Entry.class).where().eq("user", user).findList();
        }
    }

    /**
     * Convenience method for finding the contest entries for a particular user.
     *
     * @param user The user whose entries we want.
     * @return A list of the entries for the provided user.
     */
    @Override
    public List<Entry> findHistoricalEntries(User user, ContestState state, Date earliestStart) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String sql = String.format("select e.id, e.user_id, e.contest_id, e.points " +
                        "from entry e inner join contest c on e.contest_id = c.id " +
                        "where e.user_id = %s and c.contest_state_id = %s and c.start_time >= '%s'",
                user.getId(), state.getId(), simpleDateFormat.format(earliestStart));

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("e.id", "id")
                .columnMapping("e.user_id", "user.id")
                .columnMapping("e.contest_id", "contest.id")
                .columnMapping("e.points", "points")
                .create();

        com.avaje.ebean.Query<Entry> query = Ebean.find(Entry.class);
        query.setRawSql(rawSql);
        return query.findList();
    }


    /**
     * Convenience method for finding the contest entries that contain a particular athlete.
     *
     * @param athleteSportEventInfo The AthleteSportEventInfo object representing the Athlete for the SportEvent we're interested in.
     * @return A list of Entry objects whose lineups contain the specified athlete.
     */
    @Override
    public List<Entry> findEntries(AthleteSportEventInfo athleteSportEventInfo) {
        String sql = "select e.id, e.user_id, e.contest_id, e.points " +
                "from entry e inner join lineup_spot ls on e.lineup_id = ls.lineup_id " +
                "where ls.athlete_id = " + athleteSportEventInfo.getAthlete().getId();

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("e.id", "id")
                .columnMapping("e.user_id", "user.id")
                .columnMapping("e.contest_id", "contest.id")
                .columnMapping("e.points", "points")
                .create();

        com.avaje.ebean.Query<Entry> query = Ebean.find(Entry.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    @Override
    public List<Entry> findEntries(Lineup lineup, Contest contest) {
//        String sql = "select e.id, e.user_id, e.contest_id, e.points " +
//                "from entry e inner join entry_x_lineup exl on e.id = exl.entry_id " +
//                "where exl.lineup_id = " + lineup.id + " and " +
//                "e.contest_id = " + contest.getId() + " order by e.points desc";
//
//        RawSql rawSql = RawSqlBuilder.parse(sql)
//                .columnMapping("e.id", "id")
//                .columnMapping("e.user_id", "user.id")
//                .columnMapping("e.contest_id", "contest.id")
//                .columnMapping("e.points", "points")
//                .create();
//
//        com.avaje.ebean.Query<Entry> query = Ebean.find(Entry.class);
//        query.setRawSql(rawSql);
//        return query.findList();
        return Ebean.find(Entry.class).where().eq("lineup", lineup).eq("contest", contest).findList();
    }

    @Override
    public List<Entry> findEntries(User user, Contest contest) {
        return Ebean.find(Entry.class).where()
                .eq(Entry.USER_ID, user.getId())
                .eq(Entry.CONTEST_ID, contest.getId()).findList();
    }

    @Override
    public List<Entry> findEntries(User user) {
        return Ebean.find(Entry.class).where()
                .eq(Entry.USER_ID, user.getId()).findList();
    }

    @Override
    public List<Entry> findEntries(Lineup lineup) {
        return Ebean.find(Entry.class).where().eq("lineup", lineup).findList();
    }

    @Override
    public List<Entry> findEntries(List<Integer> entryIds) {
        return Ebean.find(Entry.class).where().in("id", entryIds).findList();
    }

    @Override
    public Entry findEntry(int id) {
        return Ebean.find(Entry.class).where().eq("id", id).findUnique();
    }

    /**
     * Convenience method for finding all entries for a contest.
     *
     * @param contest The contest we want all entries for.
     * @return A list of entries associated with a contest.
     */
    @Override
    public List<Entry> findEntriesAndSort(Contest contest) {
        return Ebean.find(Entry.class).where().eq("contest", contest).orderBy("points desc").findList();
    }

    @Override
    public List<Entry> findEntries(Contest contest) {
        return Ebean.find(Entry.class).where().eq("contest_id", contest.getId()).findList();
    }

    @Override
    public Lineup findLineup(int id) {
        return Ebean.find(Lineup.class).where().eq("id", id).findUnique();
    }

    @Override
    public List<Lineup> findLineups(User user, List<ContestState> contestStates) {
        return findLineups(user, contestStates, false);
    }

    @Override
    public List<Lineup> findLineups(User user, List<ContestState> contestStates, boolean distinct) {
        if (contestStates != null) {
            StringBuilder sb = new StringBuilder();
            for (ContestState s : contestStates) sb.append((sb.length() == 0) ? s.getId() : ("," + s.getId()));
            String sql = "select <DISTINCT> l.id, l.name, l.user_id, l.league_id, l.performance_data, l.sport_event_grouping_id " +
                    "from lineup l inner join entry e on l.id = e.lineup_id " +
                    "inner join contest c on e.contest_id = c.id " +
                    "where e.user_id = " + user.getId() + " and " +
                    "c.contest_state_id in (" + sb.toString() + ")";

            sql = sql.replace("<DISTINCT>", distinct ? "distinct" : "");

            RawSql rawSql = RawSqlBuilder.parse(sql)
                    .columnMapping("l.id", "id")
                    .columnMapping("l.name", "name")
                    .columnMapping("l.user_id", "user.id")
                    .columnMapping("l.league_id", "league.id")
                    .columnMapping("l.performance_data", "performanceData")
                    .columnMapping("l.sport_event_grouping_id", "sportEventGrouping.id")
                    .create();

            com.avaje.ebean.Query<Lineup> query = Ebean.find(Lineup.class);
            query.setRawSql(rawSql);
            return query.findList();
        } else {
            return Ebean.find(Lineup.class).where().eq("user", user).findList();
        }
    }

    @Override
    public List<Lineup> findHistoricalLineups(User user, ContestState state, Date earliestStart) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");

        String sql = String.format("select l.id, l.name, l.user_id, l.league_id, l.performance_data, l.sport_event_grouping_id " +
                "from lineup l inner join entry e on l.id = e.lineup_id " +
                "inner join contest c on e.contest_id = c.id " +
                "where e.user_id = %s and " +
                "c.contest_state_id = %s and c.start_time >= '%s'", user.getId(), state.getId(), simpleDateFormat.format(earliestStart));

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("l.id", "id")
                .columnMapping("l.name", "name")
                .columnMapping("l.user_id", "user.id")
                .columnMapping("l.league_id", "league.id")
                .columnMapping("l.performance_data", "performanceData")
                .columnMapping("l.sport_event_grouping_id", "sportEventGrouping.id")
                .create();

        com.avaje.ebean.Query<Lineup> query = Ebean.find(Lineup.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    @Override
    public List<Lineup> findLineups(User user, Contest contest) {
        String sql = "select l.id, l.name, l.user_id, l.league_id, l.performance_data, l.sport_event_grouping_id " +
                "from lineup l inner join entry e on l.id = e.lineup_id " +
                "inner join contest c on e.contest_id = c.id " +
                "where l.user_id = " + user.getId() + " and " +
                "c.id = " + contest.getId();

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("l.id", "id")
                .columnMapping("l.name", "name")
                .columnMapping("l.user_id", "user.id")
                .columnMapping("l.league_id", "league.id")
                .columnMapping("l.performance_data", "performanceData")
                .columnMapping("l.sport_event_grouping_id", "sportEventGrouping.id")
                .create();

        com.avaje.ebean.Query<Lineup> query = Ebean.find(Lineup.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    /**
     * Returns lineups belonging to the specified user that are compatible with the specified contest.
     *
     * @param user     The user whose lineups we want.
     * @param grouping The contest we are determining compatibility by (based on SportEventGrouping).
     * @return A list of Lineups compatible with the provided contest.
     */
    @Override
    public List<Lineup> findLineups(User user, SportEventGrouping grouping) {
        return Ebean.find(Lineup.class).where().eq("user", user).eq("sportEventGrouping", grouping).findList();
    }

    @Override
    public void removeLineupSpots(Lineup lineup) {
        Ebean.delete(lineup.getLineupSpots());
    }

    /**
     * Determine the amount of time units remaining for all athletes across the lineup for this entry.
     *
     * @return The sum of the time units remaining for this entry.
     */
    @Override
    public int calculateUnitsRemaining(Lineup lineup) {
        String sql = "select se.id, se.stat_provider_id, se.league_id, se.start_time, se.description, se.short_description, se.units_remaining " +
                "from lineup l inner join lineup_spot ls on ls.lineup_id = l.id " +
                "inner join athlete_sport_event_info asei on asei.athlete_id = ls.athlete_id " +
                "inner join sport_event se on asei.sport_event_id = se.id " +
                "where ls.athlete_sport_event_info_id = asei.id and l.id = " + lineup.getId();

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("se.id", "id")
                .columnMapping("se.stat_provider_id", "statProviderId")
                .columnMapping("se.league_id", "league.id")
                .columnMapping("se.start_time", "startTime")
                .columnMapping("se.description", "description")
                .columnMapping("se.short_description", "shortDescription")
                .columnMapping("se.units_remaining", "unitsRemaining")
                .create();

        com.avaje.ebean.Query<SportEvent> query = Ebean.find(SportEvent.class);
        query.setRawSql(rawSql);
        List<SportEvent> sportEvents = query.findList();

        int unitsRemaining = 0;
        for (SportEvent sportEvent : sportEvents) {
            unitsRemaining += sportEvent.getUnitsRemaining();
        }

        return unitsRemaining;
    }

    @Override
    public List<Contest> findContests(Lineup lineup, List<ContestState> contestStates) {
        String sql = "select c.id, c.url_id, c.contest_type_id, c.league_id, c.current_entries, c.capacity, c.is_public," +
                "c.entry_fee, c.guaranteed, c.allowed_entries, c.sport_event_grouping_id, c.salary_cap, c.start_time " +
                "from lineup l inner join entry e on l.id = e.lineup_id " +
                "inner join contest c on c.id = e.contest_id " +
                "where l.id = " + lineup.getId();

        if (contestStates != null) {
            StringBuilder sb = new StringBuilder();
            for (ContestState s : contestStates) sb.append((sb.length() == 0) ? s.getId() : ("," + s.getId()));
            sql += " and c.contest_state_id in (" + sb.toString() + ")";
        }

        RawSql rawSql = RawSqlBuilder.parse(sql)
                .columnMapping("c.id", "id")
                .columnMapping("c.url_id", "urlId")
                .columnMapping("c.contest_type_id", "contestType.id")
                .columnMapping("c.league_id", "league.id")
                .columnMapping("c.current_entries", "currentEntries")
                .columnMapping("c.capacity", "capacity")
                .columnMapping("c.is_public", "isPublic")
                .columnMapping("c.entry_fee", "entryFee")
                .columnMapping("c.allowed_entries", "allowedEntries")
                .columnMapping("c.sport_event_grouping_id", "sportEventGrouping.id")
                .columnMapping("c.salary_cap", "salaryCap")
                .columnMapping("c.start_time", "startTime")
                .create();

        com.avaje.ebean.Query<Contest> query = Ebean.find(Contest.class);
        query.setRawSql(rawSql);
        return query.findList();
    }

    /**
     * Aggregate the individual projections of the lineup's AthleteSportEventInfo objects into a
     * single value that gets returned to the caller.
     *
     * @return A value representing the sum of AthleteSportEventInfo projections.
     */
    @Override
    public BigDecimal findProjection(Lineup lineup) {
        String sql = "SELECT sum(projection) " +
                "FROM stats_projection p inner join athlete_sport_event_info asei on p.athlete_sport_event_info_id = asei.id " +
                "inner join lineup_spot ls on ls.athlete_sport_event_info_id " +
                "inner join lineup l on l.id = ls.lineup_id " +
                "where ls.athlete_sport_event_info_id = asei.id and l.id  = :lineup_id";

        SqlQuery sqlQuery = Ebean.createSqlQuery(sql);
        sqlQuery.setParameter("lineup_id", lineup.getId());

        SqlRow sqlRow = sqlQuery.findUnique();
        return sqlRow.getBigDecimal("sum(projection)");
    }

    @Override
    public List<LineupTemplate> findLineupTemplates(League league) {
        return Ebean.find(LineupTemplate.class).where(Expr.eq(LineupTemplate.LEAGUE_ID, league.getId())).findList();
    }

    @Override
    public List<ScoringRule> findScoringRules(League league) {
        return Ebean.find(ScoringRule.class).where().eq("league", league).findList();
    }
}
