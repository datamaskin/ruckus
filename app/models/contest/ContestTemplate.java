package models.contest;

import javax.persistence.*;
import java.util.List;

/**
 * Created by mwalsh on 6/20/14.
 */

@Entity
public class ContestTemplate {

    public static final String AUTO_POPULATE = "auto_populate";

    @Id
    private int id;

    @ManyToOne
    private ContestType contestType;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private boolean isPublic;

    @Column(nullable = false)
    private int entryFee;

    @Column(nullable = false)
    private int allowedEntries;

//    @Column(nullable = false)
//    @ManyToOne
//    private SportEventGroupingType sportEventGroupingType;

    @Column(nullable = false)
    private int salaryCap;

    @Column(nullable = false)
    private float rakePercentage;

    @Column(nullable = false)
    private int payoutRounding;

    @Column(nullable = false, name = AUTO_POPULATE)
    private boolean autoPopulate;

    @OneToMany(cascade = CascadeType.ALL)
    private List<ContestTemplatePayout> contestTemplatePayouts;

    public ContestTemplate(ContestType contestType, int capacity, boolean isPublic,
                           int entryFee, int allowedEntries, //SportEventGroupingType sportEventGroupingType,
                           int salaryCap, float rakePercentage, int payoutRounding, boolean autoPopulate,
                           List<ContestTemplatePayout> contestTemplatePayouts) {
        this.contestType = contestType;
//        this.league = league;
        this.capacity = capacity;
        this.isPublic = isPublic;
        this.entryFee = entryFee;
        this.allowedEntries = allowedEntries;
//        this.sportEventGroupingType = sportEventGroupingType;
        this.salaryCap = salaryCap;
        this.rakePercentage = rakePercentage;
        this.payoutRounding = payoutRounding;
        this.autoPopulate = autoPopulate;
        this.contestTemplatePayouts = contestTemplatePayouts;
    }

    public List<ContestTemplatePayout> getContestTemplatePayouts() {
        return contestTemplatePayouts;
    }

    public void setContestTemplatePayouts(List<ContestTemplatePayout> contestTemplatePayouts) {
        this.contestTemplatePayouts = contestTemplatePayouts;
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

    public int getAllowedEntries() {
        return allowedEntries;
    }

    public void setAllowedEntries(int allowedEntries) {
        this.allowedEntries = allowedEntries;
    }

//    public SportEventGroupingType getSportEventGroupingType() {
//        return sportEventGroupingType;
//    }
//
//    public void setSportEventGroupingType(SportEventGroupingType sportEventGroupingType) {
//        this.sportEventGroupingType = sportEventGroupingType;
//    }

    public int getSalaryCap() {
        return salaryCap;
    }

    public void setSalaryCap(int salaryCap) {
        this.salaryCap = salaryCap;
    }

    public float getRakePercentage() {
        return rakePercentage;
    }

    public int getPayoutRounding() {
        return payoutRounding;
    }

    public boolean isAutoPopulate() {
        return autoPopulate;
    }

    public void setAutoPopulate(boolean autoPopulate) {
        this.autoPopulate = autoPopulate;
    }
}
