
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.List;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class Downloader {
    public void download(String dir){
        File folder = (dir == null) ? new File("G:\\Dev\\Prices\\links") : new File(dir);
        File[] stores = folder.listFiles();
        assert stores != null;
        for(File store: stores){
            List<StoreNameLinks> table = createTable(store);
            if(table == null){
                System.out.println("Couldn't read: " + store.toString());
                System.out.println("Skipping file...");
                continue;
            }
            downloadCSV(table);
            System.out.println("\u001b[32mDOWNLOAD COMPLETE\u001b[37m: " + store.toString());
        }
    }

    private List<StoreNameLinks> createTable(File store) {
        Scanner scanner = null;
        try{
            scanner = new Scanner(store);
        }catch (FileNotFoundException e){
            System.err.println("Links directory corrupted: " + e);
            return null;
        }
        List<StoreNameLinks> table = new ArrayList<>();

        while (scanner.hasNextLine()){
            String row = scanner.nextLine();
            String[] columns = row.split(",");
            table.add(new StoreNameLinks(columns[0], columns[1].replace(" ", "%20")));
        }

        return table;

    }

    private void downloadCSV(List<StoreNameLinks> store) {

        StringBuilder dumpsterWriter = new StringBuilder();
        dumpsterWriter.append("G:\\Dev\\Prices\\dumpster\\"); // Enter path to dumpster dir
        int resetPath = dumpsterWriter.length();

        List<String> duplicate = new ArrayList<>();
        List<String> downloaded = new ArrayList<>();

        int i = 0;
        for( StoreNameLinks info : store){
            i++;
            dumpsterWriter.append(info.getName());
            File currentFile = new File(dumpsterWriter.toString());
            if(currentFile.isFile()){
                duplicate.add(info.getName());
                dumpsterWriter.setLength(resetPath);
                continue;
            }

            try{
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(info.getLink())).build();
                client.send(request, HttpResponse.BodyHandlers.ofFile(currentFile.toPath()));
                client.close();
                downloaded.add(info.getName());
                System.out.printf("Downloading %d/%d \r", i, store.size());
                System.out.flush();

            }catch (IOException | InterruptedException e){
                System.err.println("Couldn't create file: " + currentFile.getAbsolutePath());
                System.err.println("File was skipped");
            }
            dumpsterWriter.setLength(resetPath);
        }

        File duplicateLog = new File("G:\\Dev\\Prices\\logs\\existing_files.txt");
        File downloadedLog = new File("G:\\Dev\\Prices\\logs\\downloaded_files.txt");

        try(FileWriter duplicateWriter = new FileWriter(duplicateLog);
            FileWriter downloadedWriter = new FileWriter(downloadedLog)){

            for (String fileName : duplicate){
                duplicateWriter.write(fileName + "\n");
            }

            for(String fileName : downloaded){
                downloadedWriter.write(fileName + "\n");
            }
        } catch ( IOException e) {
            System.err.println("Couldn't write dumpster file logs.");
        }

        System.out.printf("Downloaded files: %d\n", downloaded.size());
        System.out.printf("Existing files: %d\n", duplicate.size());

    }
}
