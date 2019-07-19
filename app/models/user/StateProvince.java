package models.user;

import com.avaje.ebean.annotation.EnumValue;

/**
 * Created by mwalsh on 7/29/14.
 */
public enum StateProvince {
    @EnumValue("AL") US_AL("AL", "Alabama"),
    @EnumValue("AK") US_AK("AK", "Alaska"),
    @EnumValue("AZ") US_AZ("AZ", "Arizona"),
    @EnumValue("AR") US_AR("AR", "Arkansas"),
    @EnumValue("CA") US_CA("CA", "California"),
    @EnumValue("CO") US_CO("CO", "Colorado"),
    @EnumValue("CT") US_CT("CT", "Connecticut"),
    @EnumValue("DE") US_DE("DE", "Delaware"),
    @EnumValue("DC") US_DC("DC", "District of Columbia"),
    @EnumValue("FL") US_FL("FL", "Florida"),
    @EnumValue("GA") US_GA("GA", "Georgia"),
    @EnumValue("HI") US_HI("HI", "Hawaii"),
    @EnumValue("ID") US_ID("ID", "Idaho"),
    @EnumValue("IL") US_IL("IL", "Illinois"),
    @EnumValue("IN") US_IN("IN", "Indiana"),
    @EnumValue("IA") US_IA("IA", "Iowa"),
    @EnumValue("KS") US_KS("KS", "Kansas"),
    @EnumValue("KY") US_KY("KY", "Kentucky"),
    @EnumValue("LA") US_LA("LA", "Louisiana"),
    @EnumValue("ME") US_ME("ME", "Maine"),
    @EnumValue("MD") US_MD("MD", "Maryland"),
    @EnumValue("MA") US_MA("MA", "Massachusetts"),
    @EnumValue("MI") US_MI("MI", "Michigan"),
    @EnumValue("MN") US_MN("MN", "Minnesota"),
    @EnumValue("MS") US_MS("MS", "Mississippi"),
    @EnumValue("MO") US_MO("MO", "Missouri"),
    @EnumValue("MT") US_MT("MT", "Montana"),
    @EnumValue("NE") US_NE("NE", "Nebraska"),
    @EnumValue("NV") US_NV("NV", "Nevada"),
    @EnumValue("NH") US_NH("NH", "New Hampshire"),
    @EnumValue("NJ") US_NJ("NJ", "New Jersey"),
    @EnumValue("NM") US_NM("NM", "New Mexico"),
    @EnumValue("NY") US_NY("NY", "New York"),
    @EnumValue("NC") US_NC("NC", "North Carolina"),
    @EnumValue("ND") US_ND("ND", "North Dakota"),
    @EnumValue("OH") US_OH("OH", "Ohio"),
    @EnumValue("OK") US_OK("OK", "Oklahoma"),
    @EnumValue("OR") US_OR("OR", "Oregon"),
    @EnumValue("PA") US_PA("PA", "Pennsylvania"),
    @EnumValue("RI") US_RI("RI", "Rhode Island"),
    @EnumValue("SC") US_SC("SC", "South Carolina"),
    @EnumValue("SD") US_SD("SD", "South Dakota"),
    @EnumValue("TN") US_TN("TN", "Tennessee"),
    @EnumValue("TX") US_TX("TX", "Texas"),
    @EnumValue("UT") US_UT("UT", "Utah"),
    @EnumValue("VT") US_VT("VT", "Vermont"),
    @EnumValue("VA") US_VA("VA", "Virginia"),
    @EnumValue("WA") US_WA("WA", "Washington"),
    @EnumValue("WV") US_WV("WV", "West Virginia"),
    @EnumValue("WI") US_WI("WI", "Wisconsin"),
    @EnumValue("WY") US_WY("WY", "Wyoming"),

    @EnumValue("WY") CA_AB("AB", "Alberta"),
    @EnumValue("BC") CA_BC("BC", "British Columbia"),
    @EnumValue("MB") CA_MB("MB", "Manitoba"),
    @EnumValue("NB") CA_NB("NB", "New Brunswick"),
    @EnumValue("NL") CA_NL("NL", "Newfoundland and Labrador"),
    @EnumValue("NT") CA_NT("NT", "Northwest Territories"),
    @EnumValue("NS") CA_NS("NS", "Nova Scotia"),
    @EnumValue("NU") CA_NU("NU", "Nunavut"),
    @EnumValue("ON") CA_ON("ON", "Ontario"),
    @EnumValue("PE") CA_PE("PE", "Prince Edward Island"),
    @EnumValue("QC") CA_QC("QC", "Quebec"),
    @EnumValue("SK") CA_SK("SK", "Saskatchewan"),
    @EnumValue("YT") CA_YT("YT", "Yukon");

    public static final StateProvince[] ALL_US = new StateProvince[]{
            US_AL, US_AK, US_AZ, US_AR, US_CA, US_CO, US_CT, US_DE, US_DC, US_FL,
            US_GA, US_HI, US_ID, US_IL, US_IN, US_IA, US_KS, US_KY, US_LA, US_ME,
            US_MD, US_MA, US_MI, US_MN, US_MS, US_MO, US_MT, US_NE, US_NV, US_NH,
            US_NJ, US_NM, US_NY, US_NC, US_ND, US_OH, US_OK, US_OR, US_PA, US_RI,
            US_SC, US_SD, US_TN, US_TX, US_UT, US_VT, US_VA, US_WA, US_WV, US_WI,
            US_WY};

    public static final StateProvince[] ALL_CANADA = new StateProvince[]{
            CA_AB, CA_BC, CA_MB, CA_NB, CA_NL, CA_NT, CA_NS, CA_NU, CA_ON, CA_PE,
            CA_QC, CA_SK, CA_YT
    };

    private String abbreviation;

    private String name;

    private StateProvince(String abbreviation, String name) {
        this.abbreviation = abbreviation;
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name + "-" + abbreviation;
    }

    public static StateProvince getStateProvince(String stateProvince){
        for(StateProvince sp: StateProvince.values()){
            if(sp.getAbbreviation().equalsIgnoreCase(stateProvince)){
                return sp;
            }
        }
        throw new IllegalArgumentException("could not find state or province: " + stateProvince);
    }

}
