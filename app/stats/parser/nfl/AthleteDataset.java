package stats.parser.nfl;

import java.math.BigDecimal;
import java.util.LinkedList;

/**
 * Created by mgiles on 8/8/14.
 */
public class AthleteDataset {
    private LinkedList<BigDecimal> fppStack = new LinkedList<>(); // FPP
    private LinkedList<BigDecimal> tdStack = new LinkedList<>(); // TDs
    private LinkedList<BigDecimal> paStack = new LinkedList<>(); // Passing attempts
    private LinkedList<BigDecimal> pyStack = new LinkedList<>(); // Passing yards
    private LinkedList<BigDecimal> ruaStack = new LinkedList<>(); // Rushing attempts
    private LinkedList<BigDecimal> ruyStack = new LinkedList<>(); // Rushing yards
    private LinkedList<BigDecimal> reyStack = new LinkedList<>(); // Receiving yards
    private LinkedList<BigDecimal> retStack = new LinkedList<>(); // Receiving targets
    private LinkedList<BigDecimal> parStack = new LinkedList<>(); // Passing rating

    public LinkedList<BigDecimal> getParStack() {
        return parStack;
    }

    public void setParStack(LinkedList<BigDecimal> parStack) {
        this.parStack = parStack;
    }

    public LinkedList<BigDecimal> getFppStack() {
        return fppStack;
    }

    public void setFppStack(LinkedList<BigDecimal> fppStack) {
        this.fppStack = fppStack;
    }

    public LinkedList<BigDecimal> getTdStack() {
        return tdStack;
    }

    public void setTdStack(LinkedList<BigDecimal> tdStack) {
        this.tdStack = tdStack;
    }

    public LinkedList<BigDecimal> getPaStack() {
        return paStack;
    }

    public void setPaStack(LinkedList<BigDecimal> paStack) {
        this.paStack = paStack;
    }

    public LinkedList<BigDecimal> getPyStack() {
        return pyStack;
    }

    public void setPyStack(LinkedList<BigDecimal> pyStack) {
        this.pyStack = pyStack;
    }

    public LinkedList<BigDecimal> getRuaStack() {
        return ruaStack;
    }

    public void setRuaStack(LinkedList<BigDecimal> ruaStack) {
        this.ruaStack = ruaStack;
    }

    public LinkedList<BigDecimal> getRuyStack() {
        return ruyStack;
    }

    public void setRuyStack(LinkedList<BigDecimal> ruyStack) {
        this.ruyStack = ruyStack;
    }

    public LinkedList<BigDecimal> getReyStack() {
        return reyStack;
    }

    public void setReyStack(LinkedList<BigDecimal> reyStack) {
        this.reyStack = reyStack;
    }

    public LinkedList<BigDecimal> getRetStack() {
        return retStack;
    }

    public void setRetStack(LinkedList<BigDecimal> retStack) {
        this.retStack = retStack;
    }
}
