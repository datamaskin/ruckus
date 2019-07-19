package distributed.tasks;

import com.avaje.ebean.Ebean;
import dao.DaoFactory;
import dao.IContestDao;
import dao.ISportsDao;
import models.contest.ContestPayout;
import models.contest.ContestTemplate;
import models.contest.ContestTemplatePayout;
import models.sports.*;
import models.stats.nfl.StatsNflProjection;
import models.stats.nfl.StatsNflProjectionDefense;
import play.Logger;
import play.Play;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by mwalsh on 6/18/14.
 */
public class ContestCreatorTask {

    private IContestDao contestDao;
    private ISportsDao sportsDao;

    public ContestCreatorTask() {
        this.contestDao = DaoFactory.getContestDao();
        this.sportsDao = DaoFactory.getSportsDao();
    }

    public void createContests(SportEventGrouping sportEventGrouping) {
        for (ContestTemplate template : contestDao.findAllContestTemplates()) {
            createContest(sportEventGrouping, template);
        }
    }

    private void createContest(SportEventGrouping sportEventGrouping, ContestTemplate template) {
        associateAthletes(sportEventGrouping.getSportEvents());

        if (sportEventGrouping.getSportEventGroupingType().getLeague().equals(League.NFL)) {
            if (!createNflSalaries(sportEventGrouping)) {
                Logger.error("Cannot create salaries for these contests. No prediction data");
                createRandomSalaries(sportEventGrouping);
            }
            if (!createNflSalariesForDefense(sportEventGrouping)) {
                Logger.error("Cannot create salaries for these contests. No prediction data");
                createRandomSalaries(sportEventGrouping);
            }
        } else {
            createRandomSalaries(sportEventGrouping);
        }

        contestDao.createNewOpenContest(template.getContestType(),
                sportEventGrouping.getSportEventGroupingType().getLeague(),
                "",
                template.getCapacity(), template.isPublic(),
                template.getEntryFee(), template.getAllowedEntries(),
                template.getSalaryCap(),
                sportEventGrouping,
                getContestPayouts(template),
                template);
    }

    private List<ContestPayout> getContestPayouts(ContestTemplate template) {
        int entryFee = template.getEntryFee();
        int entrants = template.getCapacity();
        int total = entryFee * entrants;
        int rake = (int) (total * (template.getRakePercentage() / 100));
        int totalPrizePool = (total - rake);

        List<ContestPayout> payouts = new ArrayList<>();
        for (ContestTemplatePayout payout : template.getContestTemplatePayouts()) {
            int payoutUnrounded = (int) (totalPrizePool * (payout.getPayoutPercentage() / 100));

            int payoutRounded = 0;
            if (payout.getRoundingMode().equals(RoundingMode.CEILING)) {
                payoutRounded = (int) (Math.ceil(payoutUnrounded / template.getPayoutRounding()) * template.getPayoutRounding());
            } else if (payout.getRoundingMode().equals(RoundingMode.FLOOR)) {
                payoutRounded = (int) (Math.floor(payoutUnrounded / template.getPayoutRounding()) * template.getPayoutRounding());
            }
            payouts.add(new ContestPayout(payout.getLeadingPosition(), payout.getTrailingPosition(), payoutRounded));

        }
        return payouts;
    }

    public void createRandomSalaries(SportEventGrouping sportEventGrouping) {
        for (SportEvent sportEvent : sportEventGrouping.getSportEvents()) {
            for (Team team : sportEvent.getTeams()) {
                List<Athlete> athletes = Ebean.find(Athlete.class).where().eq(Athlete.TEAM_ID, team.getId()).findList();
                for (Athlete athlete : athletes) {

                    // only generate salaries in local mode or for defenses right now
                    //TODO: remove kicker
                    if (Play.isDev() || athlete.getPositions().contains(Position.FB_KICKER)) {
                        if (Ebean.find(AthleteSalary.class).where()
                                .eq(AthleteSalary.ATHLETE_ID, athlete.getId())
                                .eq(AthleteSalary.SPORT_EVENT_GROUP_ID, sportEventGrouping.getId())
                                .findUnique() == null) {

                            int startingSalary = 4700;
                            //TODO: remove kicker
                            if (athlete.getPositions().contains(Position.FB_KICKER)) {
                                startingSalary = 2700;
                            }

                            int salary = (startingSalary + ((int) (Math.random() * 9)) * 100) * 100;
                            salary += 100;

                            AthleteSalary athleteSalary = DaoFactory.getSportsDao().findAthleteSalary(athlete, sportEventGrouping);
                            if (athleteSalary == null) {
                                athleteSalary = new AthleteSalary(athlete, sportEventGrouping, Math.round(salary));
                                Ebean.save(athleteSalary);
                            }
                        }
                    }
                }
            }
        }
    }

    public boolean createNflSalaries(SportEventGrouping sportEventGrouping) {
        List<StatsNflProjection> predictions = new ArrayList<>();
        // get all of the predictions
        for (SportEvent sportEvent : sportEventGrouping.getSportEvents()) {
            List<StatsNflProjection> newList = DaoFactory.getStatsDao().findNflPredictions(sportEvent);
            if (newList != null) {
                predictions.addAll(newList);
            }
        }
        if (predictions.size() == 0) {
            return false;
            //throw new RuntimeException("Cannot create salaries for these contests. No prediction data");
        }
        // get the max
        float max = 0;
        for (StatsNflProjection prediction : predictions) {
            if (max == 0 || max < prediction.getProjectedFpp()) {
                max = prediction.getProjectedFppMod();
            }
        }
        // get the total
        float total = 0f;
        int count = 0;
        for (; count < predictions.size(); count++) {
            total += predictions.get(count).getProjectedFppMod();
        }

        for (SportEvent sportEvent : sportEventGrouping.getSportEvents()) {
            for (Team team : sportEvent.getTeams()) {
                List<Athlete> athletes = DaoFactory.getSportsDao().findAthletes(team);
                for (Athlete athlete : athletes) {
                    for (StatsNflProjection prediction : predictions) {
                        if (athlete.equals(prediction.getAthlete())) {
                            float projection = prediction.getProjectedFppMod();
                            float salary = projection / (.0018f / (1f - (max / total)));
                            if (salary > 0) {
                                salary = Math.max(salary, 2000);
                            }
                            //TODO: remove kicker
                            if (athlete.getPositions().contains(Position.FB_KICKER)) {
                                salary = 2000;
                            }
                            long iSalary = Math.round(100f * Math.floor((salary + 50f) / 100f));
                            iSalary = iSalary * 100;
                            if (projection <= 1.5f) {
                                iSalary = 0;
                            }
                            AthleteSalary athleteSalary = DaoFactory.getSportsDao().findAthleteSalary(athlete, sportEventGrouping);
                            if (athleteSalary == null) {
                                athleteSalary = new AthleteSalary(athlete, sportEventGrouping, (int) iSalary);
                                Ebean.save(athleteSalary);
                            }
                        }
                    }
                }
            }
        }


        // create random salaries for athletes that don't have them
        // this should only happen in dev mode
        createRandomSalaries(sportEventGrouping);
        return true;
    }

    public boolean createNflSalariesForDefense(SportEventGrouping sportEventGrouping) {
        List<StatsNflProjectionDefense> predictions = new ArrayList<>();
        // get all of the predictions
        for (SportEvent sportEvent : sportEventGrouping.getSportEvents()) {
            List<StatsNflProjectionDefense> newList = DaoFactory.getStatsDao().findNflPredictionDefense(sportEvent);
            if (newList != null) {
                predictions.addAll(newList);
            }
        }
        if (predictions.size() == 0) {
            return false;
            //throw new RuntimeException("Cannot create salaries for these contests. No prediction data");
        }
        // get the max
        float max = 0;
        for (StatsNflProjectionDefense prediction : predictions) {
            if (max == 0 || max < prediction.getProjectedFpp()) {
                max = prediction.getProjectedFppMod();
            }
        }
        // get the total
        float total = 0f;
        int count = 0;
        for (; count < predictions.size(); count++) {
            total += predictions.get(count).getProjectedFppMod();
        }

        for (SportEvent sportEvent : sportEventGrouping.getSportEvents()) {
            for (Team team : sportEvent.getTeams()) {
                List<Athlete> athletes = DaoFactory.getSportsDao().findAthletes(team);
                for (Athlete athlete : athletes) {
                    for (StatsNflProjectionDefense prediction : predictions) {
                        if (athlete.equals(prediction.getAthlete())) {
                            float projection = prediction.getProjectedFppMod();
                            float salary = projection / (.0015f / (1f - (max / total)));
                            if (salary > 0) {
                                salary = Math.max(salary, 2000);
                            }
                            long iSalary = Math.round(100f * Math.floor((salary + 50f) / 100f));
                            iSalary = iSalary * 100;
                            AthleteSalary athleteSalary = DaoFactory.getSportsDao().findAthleteSalary(athlete, sportEventGrouping);
                            if (athleteSalary == null) {
                                athleteSalary = new AthleteSalary(athlete, sportEventGrouping, (int) iSalary);
                                Ebean.save(athleteSalary);
                            }
                        }
                    }
                }
            }
        }


        // create random salaries for athletes that don't have them
        // this should only happen in dev mode
        createRandomSalaries(sportEventGrouping);
        return true;
    }

    public void associateAthletes(List<SportEvent> sportEvents) {
        for (SportEvent sportEvent : sportEvents) {
            List<Athlete> athletes = sportsDao.findAthletes(sportEvent);
            for (Athlete athlete : athletes) {
                AthleteSportEventInfo athleteSportEventInfo =
                        Ebean.find(AthleteSportEventInfo.class)
                                .where().eq("sportEvent", sportEvent)
                                .eq("athlete", athlete).findUnique();
                if (athleteSportEventInfo == null) {
                    // Set up the initial box score string.
                    String boxscore = "[]";
                    if (sportEvent.getLeague().getAbbreviation().equals(League.MLB.getAbbreviation()) ||
                            sportEvent.getLeague().getAbbreviation().equals(League.NFL.getAbbreviation())) {
                        boxscore = sportsDao.createInitialJsonForAthleteBoxscore(athlete.getPositions().get(0));
                    }

                    athleteSportEventInfo = new AthleteSportEventInfo(
                            sportEvent, athlete, new BigDecimal("0.00"), boxscore, "[]");

                    DaoFactory.getSportsDao().saveAthleteSportEventInfo(athleteSportEventInfo);
                }
            }
        }
    }

}
