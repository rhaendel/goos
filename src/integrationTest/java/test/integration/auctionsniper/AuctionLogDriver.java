package test.integration.auctionsniper;

import org.hamcrest.Matcher;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.LogManager;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.MatcherAssert.assertThat;

public class AuctionLogDriver {

    private static final String LOG_FILE_NAME = "auction-sniper.log";
    private final File logFile = new File(LOG_FILE_NAME);

    public void hasEntry(Matcher<String> matcher) throws IOException {
        assertThat(readFileToString(logFile, Charset.forName("utf-8")), matcher);
    }

    public void clearLog() {
        logFile.delete();
        LogManager.getLogManager().reset();
    }
}
