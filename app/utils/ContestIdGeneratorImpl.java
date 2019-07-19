package utils;

import java.util.Random;

public class ContestIdGeneratorImpl implements IContestIdGenerator {
    @Override
    public String generateString(int length, String alphabet, Random totalRando) {
        char[] characters = new char[length];
        int alphabetLength = alphabet.length();
        for (int i = 0; i < length; i++) {
            characters[i] = alphabet.charAt(totalRando.nextInt(alphabetLength));
        }
        return new String(characters);
    }
}
