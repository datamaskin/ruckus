package stats.statsinc;

import org.apache.commons.io.FileUtils;
import stats.provider.IStatProvider;

import java.io.File;
import java.util.Map;

/**
 * Created by dmaclean on 7/14/14.
 */
public class FileStatProvider implements IStatProvider {
    private String filename;

    @Override
    public String getStats(Map<String, String> params) throws Exception {
        return FileUtils.readFileToString(new File(filename));
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}
