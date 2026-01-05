package Text;

import org.openqa.selenium.devtools.v135.audits.model.SRIMessageSignatureError;

public abstract class Text {

    public static class Messages{
        private static final String NEWLINE = System.lineSeparator();
        public static final String WAITING_FOR_WEBPAGE =
                ANSI.Color.basicString("Waiting on: ", ANSI.BasicColor.BLUE) + "%s" + NEWLINE;
        public static final String FINISHED_SCRAPING =
                ANSI.Color.basicString("Finished scraping: ", ANSI.BasicColor.YELLOW) + "%s" + NEWLINE;
        public static final String DOWNLOAD_COMPLETE =
                ANSI.Color.basicString("Download Complete: ", ANSI.BasicColor.GREEN) + "%s" + NEWLINE;

    }

    public static class Constants{
        public static final String ZIP_EXTENSION = ".zip";
    }

    public static class ErrorMessagess{
        public static final String UNZIPPER_STREAM_CREATION_FAIL = "Unzipper: Failed to create input stream";
        public static final String UNZIPPER_FILE_DELETE_FAIL = "Unzipper: Failed to delete file: %s";
        public static final String DOWNLOAD_FAILED = "Download failed: %s";
    }


}
