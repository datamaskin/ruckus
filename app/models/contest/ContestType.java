package models.contest;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class ContestType {

    public static final ContestType H2H = new ContestType(1, "Head to Head", "H2H");
    public static final ContestType GPP = new ContestType(2, "Guaranteed", "GPP");
    public static final ContestType DOUBLE_UP = new ContestType(3, "Double-Up", "DU");
    public static final ContestType NORMAL = new ContestType(4, "Standard", "NRM");
    public static final ContestType SATELLITE = new ContestType(5, "Satellite", "SAT");
    public static final ContestType PROMO = new ContestType(6, "Promotion", "PRO");
    public static final ContestType ANONYMOUS_H2H = new ContestType(7, "Anonymous Head to Head", "ANON");

    public static final ContestType[] ALL = new ContestType[]{H2H, GPP, DOUBLE_UP, NORMAL, SATELLITE, PROMO, ANONYMOUS_H2H};

    @Id
    private int id;

    private String name;
    private String abbr;

    public ContestType(int id, String name, String abbr) {
        this.id = id;
        this.name = name;
        this.abbr = abbr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbr() {
        return abbr;
    }

    public void setAbbr(String abbr) {
        this.abbr = abbr;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((abbr == null) ? 0 : abbr.hashCode());
        result = prime * result + id;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if(!(obj instanceof ContestType))
            return false;
        ContestType other = (ContestType) obj;
        if (abbr == null) {
            if (other.abbr != null)
                return false;
        } else if (!abbr.equals(other.abbr))
            return false;
        if (id != other.id)
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }
}
