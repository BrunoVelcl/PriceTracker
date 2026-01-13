package Text;

import static Text.Text.Constants.NEWLINE;

public abstract class Text {

    public static class Messages{
        public static final String WAITING_FOR_WEBPAGE =
                ANSI.Color.basicString("Waiting on: ", ANSI.BasicColor.BLUE) + "%s" + NEWLINE;
        public static final String FINISHED_SCRAPING =
                ANSI.Color.basicString("Finished scraping, ", ANSI.BasicColor.YELLOW) +
                ANSI.Color.basicString("starting download: ", ANSI.BasicColor.BLUE) + "%s" + NEWLINE;
        public static final String DOWNLOAD_COMPLETE =
                ANSI.Color.basicString("Download Complete: ", ANSI.BasicColor.GREEN) + "%s" + NEWLINE;
        public static final String NO_NEW_DATA =
                ANSI.Color.basicString("Finished scraping: ", ANSI.BasicColor.YELLOW) + "%s" +
                ANSI.Color.basicString(" no new files found.", ANSI.BasicColor.YELLOW) + NEWLINE;
        public static final String SCRAPING_FAILED =
                ANSI.Color.basicString("Scraping failed: ", ANSI.BasicColor.RED) + "%s" + NEWLINE;
        public static final String COMPLETED =
                ANSI.Color.basicString("Completed: ", ANSI.BasicColor.GREEN) + "%s" + NEWLINE;
        public static final String CONFLICTING_DATA_ENTRY =
                ANSI.Color.basicString(" ---Found conflicting prices---", ANSI.BasicColor.YELLOW) + NEWLINE +
                        "%s // %s // %s // %s // %s"  + NEWLINE +
                        "%s // %s // %s // %s // %s"  + NEWLINE +
                        ANSI.Color.basicString("*****************************", ANSI.BasicColor.YELLOW) + NEWLINE;
    }

    public static class Constants{
        public static final String NEWLINE = System.lineSeparator();
        public static final String COMA_DELIMITER = ",";
        public static final String ZIP_EXTENSION = ".zip";
    }

    public static class ErrorMessagess{
        public static final String UNZIPPER_STREAM_CREATION_FAIL = "Unzipper: Failed to create input stream";
        public static final String UNZIPPER_FILE_DELETE_FAIL = "Unzipper: Failed to delete file: %s";
        public static final String DOWNLOAD_FAILED = "Download failed: %s";
        public static final String STORE_FILE_OPEN_FAIL = "Failed to open store file.";
        public static final String DATA_FOR_PARSING_NOT_FOUND = "Data for parsing not found.";
        public static final String SAVE_FILE_WRITE_ERROR = "Couldn't write savefile.";
        public static final String SAVE_FILE_READ_ERROR = "Couldn't read savefile.";
        public static final String IO_EXCEPTION_BUFF_WRITER = "Savefile: couldn't write following string: %s";
        public static final String PARSING_RETURNED_NOTHING = "Parsing error for: %s" + NEWLINE;

    }


}
