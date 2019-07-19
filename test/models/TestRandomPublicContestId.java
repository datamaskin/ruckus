package models;

import utils.ContestIdGeneratorImpl;
import utils.IContestIdGenerator;

import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

public class TestRandomPublicContestId {

  public static void main(String[] args) throws NoSuchAlgorithmException {
    Set<String> ids = new HashSet<>();
    Set<String> dupes = new HashSet<>();

    String alphabet = IContestIdGenerator.numeric + IContestIdGenerator.alphaLower + IContestIdGenerator.alphaUpper;
    for (int i = 0; i < 1000000; i++) {
      String id = new ContestIdGeneratorImpl().generateString(8, alphabet, new SecureRandom());
      System.out.println(i + ":" + id);
      if (ids.contains(id)) {
        dupes.add(id);
      } else {
        ids.add(id);
      }
    }
    System.out.println("dupes:" + dupes);
  }

}
