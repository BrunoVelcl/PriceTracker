package com.brunovelcl.pricetracker.DataFetcher;

import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.ScrapedLink;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

@Service
public class PythonDownloader {

    private static final String SCRIPT_PATH = "./pythonScripts/catalogDownloader.py";
    private static final String LOG_FILENAME = "catalogDownloader.txt";

    private final ObjectMapper objectMapper;

    public PythonDownloader(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public List<Long> download(List<ScrapedLink> scrapedLinkList){

        List<Long> scrapedLinksTableIds = new ArrayList<>();

        List<DownloadInfo> downloadInfoList = new ArrayList<>();
        scrapedLinkList.forEach(scrapedLink -> {downloadInfoList.add(new DownloadInfo(scrapedLink.getId(), scrapedLink.getLink(), scrapedLink.getFilename()));});

        final String urlAndFilenameJson = objectMapper.writeValueAsString(downloadInfoList);

        final String chain = scrapedLinkList.getFirst().getChain().getName();
        final String downloadDir = Text.Directories.TEMP + chain + "/";

        try {
            ProcessBuilder pb = new ProcessBuilder(
                    "python3",
                    SCRIPT_PATH,
                    downloadDir,
                    urlAndFilenameJson
            );

            try (Process process = pb.start()) {
                if(process.waitFor() != 0){
                    findErrorIds(downloadDir, scrapedLinksTableIds);
                    throw new RuntimeException("Python script exited with errors.");
                }
            }
        } catch (Exception e) {
            System.err.printf(Text.ErrorMessages.PYTHON_SCRIPT_FAILED, SCRIPT_PATH, downloadDir);
            System.err.println(e.getMessage());
        }

        Unzipper.unzipAllInDir(downloadDir);

        System.out.printf(Text.Messages.DOWNLOAD_COMPLETE, chain);
        return scrapedLinksTableIds;
    }

    private void findErrorIds(String logPath, List<Long> callerOwnedList){

        final Long control = 0L;

        try(BufferedReader br = Files.newBufferedReader(Path.of(logPath + LOG_FILENAME))){
            List<String> lines = br.readAllLines().reversed();
            for(String line : lines){
                String[] columns = line.split("\\|");
                // [2] - id
                Long id = Long.parseLong(columns[2].strip());
                if(id.equals(control)) return;
                callerOwnedList.add(id);
            }
        } catch (Exception e) {
            System.err.printf(Text.ErrorMessages.FILE_READ_ERROR, logPath + LOG_FILENAME);
            System.err.println(e.getMessage());
        }
    }
}
