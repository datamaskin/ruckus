package controllers;

import common.GlobalConstants;
import models.contest.ContestTemplate;
import models.contest.ContestTemplatePayout;
import models.contest.ContestType;

import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by mwalsh on 6/22/14.
 */
public class ContestTemplatePopulator {

    private static final int NICKEL = 5;
    private static final int DIME = 10;
    private static final int DOLLAR = 100;

    private static int SALARY_CAP = GlobalConstants.DEFAULT_SALARY_CAP;

    public List<ContestTemplate> populate() {
        List<ContestTemplate> templates = new ArrayList<>();

        templates.addAll(createHeadsUpContestTemplates(ContestType.H2H));

        templates.addAll(create6ManDoubleUp());
        templates.addAll(create10ManDoubleUp());
        templates.addAll(create20ManDoubleUp());

        templates.addAll(create6ManContestTemplates(ContestType.NORMAL));
        templates.addAll(create10ManContestTemplates(ContestType.NORMAL));
        templates.addAll(create20ManContestTemplates(ContestType.NORMAL));

        return templates;
    }

    private List<ContestTemplate> createHeadsUpContestTemplates(ContestType type) {
        return Arrays.asList(
                new ContestTemplate(
                        type, 2, true,
                        200, 1, SALARY_CAP, 5.0f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 100.0f, RoundingMode.FLOOR))),
                new ContestTemplate(
                        type, 2, true,
                        500, 1, SALARY_CAP, 6.0f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 100.0f, RoundingMode.FLOOR))),
                new ContestTemplate(
                        type, 2, true,
                        1000, 1, SALARY_CAP, 6.0f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 100.0f, RoundingMode.FLOOR))),
                new ContestTemplate(
                        type, 2, true,
                        2000, 1, SALARY_CAP, 6.0f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 100.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 2, true,
                        5000, 1, SALARY_CAP, 6.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 100.0f, RoundingMode.FLOOR))),
                new ContestTemplate(
                        type, 2, true,
                        10000, 1, SALARY_CAP, 6.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 100.0f, RoundingMode.FLOOR))),
                new ContestTemplate(
                        type, 2, true,
                        20000, 1, SALARY_CAP, 6.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 100.0f, RoundingMode.FLOOR)))
        );
    }

    private List<ContestTemplate> create6ManDoubleUp() {
        return Arrays.asList(
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 6, true,
                        200, 1, SALARY_CAP, 7.5f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 3, 33.3f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 6, true,
                        500, 1, SALARY_CAP, 7.5f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 3, 33.3f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 6, true,
                        1000, 1, SALARY_CAP, 7.5f, DIME,
                        true, Arrays.asList(new ContestTemplatePayout(1, 3, 33.3f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 6, true,
                        2000, 1, SALARY_CAP, 8.0f, DIME,
                        true, Arrays.asList(new ContestTemplatePayout(1, 3, 33.3f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 6, true,
                        5000, 1, SALARY_CAP, 8.0f, DOLLAR,
                        true, Arrays.asList(new ContestTemplatePayout(1, 3, 33.3f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 6, true,
                        10000, 1, SALARY_CAP, 8.0f, DOLLAR,
                        true, Arrays.asList(new ContestTemplatePayout(1, 3, 33.3f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 6, true,
                        20000, 1, SALARY_CAP, 8.0f, DOLLAR,
                        true, Arrays.asList(new ContestTemplatePayout(1, 3, 33.3f, RoundingMode.CEILING))));
    }

    private List<ContestTemplate> create10ManDoubleUp() {
        return Arrays.asList(
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 10, true,
                        200, 1, SALARY_CAP, 7.5f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 5, 20.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 10, true,
                        500, 1, SALARY_CAP, 7.5f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 5, 20.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 10, true,
                        1000, 1, SALARY_CAP, 7.5f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 5, 20.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 10, true,
                        2000, 1, SALARY_CAP, 8.0f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 5, 20.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 10, true,
                        5000, 1, SALARY_CAP, 8.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 5, 20.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 10, true,
                        10000, 1, SALARY_CAP, 8.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 5, 20.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 10, true,
                        20000, 1, SALARY_CAP, 8.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 5, 20.0f, RoundingMode.CEILING))));
    }

    private List<ContestTemplate> create20ManDoubleUp() {
        return Arrays.asList(new ContestTemplate(
                        ContestType.DOUBLE_UP, 20, true,
                        200, 1, SALARY_CAP, 7.5f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 10, 10.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 20, true,
                        500, 1, SALARY_CAP, 7.5f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 10, 10.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 20, true,
                        1000, 1, SALARY_CAP, 7.5f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 10, 10.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        ContestType.DOUBLE_UP, 20, true,
                        2000, 1, SALARY_CAP, 8.0f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 10, 10.0f, RoundingMode.CEILING))));
    }

    private List<ContestTemplate> create6ManContestTemplates(ContestType type) {
        return Arrays.asList(
                new ContestTemplate(
                        type, 6, true,
                        200, 1, SALARY_CAP, 7.5f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 65.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 35.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 6, true,
                        500, 1, SALARY_CAP, 8.0f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 65.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 35.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 6, true,
                        1000, 1, SALARY_CAP, 8.5f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 65.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 35.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 6, true,
                        2000, 1, SALARY_CAP, 8.5f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 65.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 35.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 6, true,
                        5000, 1, SALARY_CAP, 9.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 65.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 35.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 6, true,
                        10000, 1, SALARY_CAP, 9.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 65.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 35.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 6, true,
                        20000, 1, SALARY_CAP, 9.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 65.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 35.0f, RoundingMode.CEILING))));
    }

    private List<ContestTemplate> create10ManContestTemplates(ContestType type) {
        return Arrays.asList(
                new ContestTemplate(
                        type, 10, true,
                        200, 1, SALARY_CAP, 7.5f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 55.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 30.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 15.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 10, true,
                        500, 1, SALARY_CAP, 8.0f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 55.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 30.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 15.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 10, true,
                        1000, 1, SALARY_CAP, 8.5f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 55.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 30.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 15.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 10, true,
                        2000, 1, SALARY_CAP, 8.5f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 55.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 30.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 15.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 10, true,
                        5000, 1, SALARY_CAP, 9.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 55.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 30.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 15.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 10, true,
                        10000, 1, SALARY_CAP, 9.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 55.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 30.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 15.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 10, true,
                        20000, 1, SALARY_CAP, 9.0f, DOLLAR,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 55.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 30.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 15.0f, RoundingMode.CEILING))));
    }

    private List<ContestTemplate> create20ManContestTemplates(ContestType type) {
        return Arrays.asList(
                new ContestTemplate(
                        type, 20, true,
                        200, 1, SALARY_CAP, 7.5f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 40.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 23.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 17.5f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(4, 4, 12.5f, RoundingMode.CEILING),
                        new ContestTemplatePayout(5, 5, 7.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 20, true,
                        500, 1, SALARY_CAP, 8.0f, NICKEL,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 40.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 23.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 17.5f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(4, 4, 12.5f, RoundingMode.CEILING),
                        new ContestTemplatePayout(5, 5, 7.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 20, true,
                        1000, 1, SALARY_CAP, 8.5f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 40.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 23.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 17.5f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(4, 4, 12.5f, RoundingMode.CEILING),
                        new ContestTemplatePayout(5, 5, 7.0f, RoundingMode.CEILING))),
                new ContestTemplate(
                        type, 20, true,
                        2000, 1, SALARY_CAP, 8.5f, DIME,
                        true, Arrays.asList(
                        new ContestTemplatePayout(1, 1, 40.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(2, 2, 23.0f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(3, 3, 17.5f, RoundingMode.FLOOR),
                        new ContestTemplatePayout(4, 4, 12.5f, RoundingMode.CEILING),
                        new ContestTemplatePayout(5, 5, 7.0f, RoundingMode.CEILING))));
    }

}
