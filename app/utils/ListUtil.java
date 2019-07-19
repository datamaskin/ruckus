package utils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Created by mgiles on 7/29/14.
 */
@SuppressWarnings(value = "unchecked")
public class ListUtil {
    public static LinkedList<BigDecimal> findAveragesAndReverse(LinkedList<BigDecimal> list, int limit) {
        list = list == null ? new LinkedList<>() : list;
        if (list.size() > limit) {
            list.removeFirst();
        }
        LinkedList<BigDecimal> reversed = (LinkedList<BigDecimal>) list.clone();
        Collections.reverse(reversed);
        LinkedList<BigDecimal> results = new LinkedList<>();
        float sum = 0;

        for (int index = 0; index < reversed.size(); index++) {
            float fpp = reversed.get(index).floatValue();
            sum += fpp;
            Float avg = sum / (index + 1);
            if (avg.isInfinite() || avg.isNaN()) {
                results.add(new BigDecimal(0));
            } else {
                BigDecimal bd = new BigDecimal(avg).setScale(3, RoundingMode.CEILING);
                results.add(bd);
            }
        }
        return results;
    }

    public static LinkedList<Float> findAverages(LinkedList<Float> list, int limit) {
        list = list == null ? new LinkedList<>() : list;
        if (list.size() > limit) {
            list.removeFirst();
        }
        LinkedList<Float> clone = (LinkedList<Float>) list.clone();
        Collections.reverse(clone);
        LinkedList<Float> results = new LinkedList<>();
        float sum = 0;

        for (int index = 0; index < clone.size(); index++) {
            float fpp = clone.get(index);
            sum += fpp;
            Float avg = sum / (index + 1);
            if (avg.isInfinite() || avg.isNaN()) {
                results.add(0f);
            } else {
                BigDecimal bd = new BigDecimal(avg).setScale(3, RoundingMode.CEILING);
                results.add(bd.floatValue());
            }
        }
        return results;
    }

    public static LinkedList<BigDecimal> trimAndReverse(LinkedList<BigDecimal> list, int limit) {
        list = list == null ? new LinkedList<>() : list;
        if (list.size() > limit) {
            list.removeFirst();
        }
        LinkedList<BigDecimal> reversed = (LinkedList<BigDecimal>) list.clone();
        Collections.reverse(reversed);
        return reversed;
    }

    public static LinkedList<Float> trim(LinkedList<Float> list, int limit) {
        list = list == null ? new LinkedList<>() : list;
        if (list.size() > limit) {
            list.removeFirst();
        }
        LinkedList<Float> clone = (LinkedList<Float>) list.clone();
        Collections.reverse(clone);
        return clone;
    }
}
