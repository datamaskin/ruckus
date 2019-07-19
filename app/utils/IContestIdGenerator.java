package utils;

import java.util.Random;

/**
 * Created by mwalsh on 8/22/14.
 */
public interface IContestIdGenerator {

    public static final String numeric = "0123456789";
    public static final String alphaLower = "abcdefghijklmnopqrstuvwxyz";
    public static final String alphaUpper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    String generateString(int length, String alphabet, Random totalRando);

}
