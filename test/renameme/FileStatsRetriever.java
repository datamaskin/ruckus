package renameme;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class FileStatsRetriever {

  private String url;

  public FileStatsRetriever(String url) {
    this.url = url;
  }

  public String getResults() {
    try {
      return FileUtils.readFileToString(new File(url));
    } catch (IOException e) {
      throw new RuntimeException("Could not load file " + url);
    }
  }

}
