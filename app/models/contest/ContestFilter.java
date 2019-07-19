package models.contest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Class representing the data that gets sent over to the client side for contest filters.
 */
public class ContestFilter implements Serializable {
    /**
     * Label for the filter that represents the union of all other filters.
     */
    public static final String FILTER_TYPE_ALL = "ALL";

    private String name;

    private List<Integer> entryFee;

    private List<ContestNumberOfUsers> numPlayers;

    private List<ContestGrouping> grouping;

    private List<Integer> salaryCap;

    public ContestFilter() {
        entryFee = new ArrayList<>();
        numPlayers = new ArrayList<>();
        grouping = new ArrayList<>();
        salaryCap = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Integer> getEntryFee() {
        return entryFee;
    }

    public void setEntryFee(List<Integer> entryFee) {
        this.entryFee = entryFee;
    }

    public List<ContestNumberOfUsers> getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(List<ContestNumberOfUsers> numPlayers) {
        this.numPlayers = numPlayers;
    }

    public List<ContestGrouping> getGrouping() {
        return grouping;
    }

    public void setGrouping(List<ContestGrouping> grouping) {
        this.grouping = grouping;
    }

    public List<Integer> getSalaryCap() {
        return salaryCap;
    }

    public void setSalaryCap(List<Integer> salaryCap) {
        this.salaryCap = salaryCap;
    }
}
