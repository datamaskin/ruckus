package models.contest;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

/**
 * Created by dmaclean on 7/19/14.
 */
@Entity
public class ContestSuggestion {
    @Id
    private int id;

    @ManyToOne
    private ContestType contestType;

    private int capacity;

    @ManyToOne
    @JoinColumn(name = "suggestion_contest_type_id", referencedColumnName = "id")
    private ContestType suggestionContestType;

    private int suggestionCapacity;

    public ContestSuggestion(ContestType contestType, int capacity, ContestType suggestionContestType, int suggestionCapacity) {
        this.contestType = contestType;
        this.capacity = capacity;
        this.suggestionContestType = suggestionContestType;
        this.suggestionCapacity = suggestionCapacity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContestType getContestType() {
        return contestType;
    }

    public void setContestType(ContestType contestType) {
        this.contestType = contestType;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public ContestType getSuggestionContestType() {
        return suggestionContestType;
    }

    public void setSuggestionContestType(ContestType suggestionContestType) {
        this.suggestionContestType = suggestionContestType;
    }

    public int getSuggestionCapacity() {
        return suggestionCapacity;
    }

    public void setSuggestionCapacity(int suggestionCapacity) {
        this.suggestionCapacity = suggestionCapacity;
    }
}
