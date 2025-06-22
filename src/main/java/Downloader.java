import java.io.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;


public class Downloader {

    @SuppressWarnings("unchecked")
    public void download(String destinationDir){
        List<StoreNameLinks> linksList;
        try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("links.bin"))){
            linksList = (List<StoreNameLinks>) inputStream.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("ERROR: Can't find link file.");
            return;
        }

            downloadFromLinks(linksList, destinationDir);
            System.out.println("\u001b[32mDOWNLOAD COMPLETE\u001b[37m: ");
    }

    //TODO: DELETE, this bullshit shouldn't exist outside of my PC
    public void download(){
        download("G:\\Dev\\Prices\\dumpster\\");
    }

    private void downloadFromLinks(List<StoreNameLinks> store, String destinationDir) {

        StringBuilder dumpsterWriter = new StringBuilder();
        dumpsterWriter.append(destinationDir);
        int resetPath = dumpsterWriter.length();

        List<String> duplicate = new ArrayList<>();
        List<String> downloaded = new ArrayList<>();

        int i = 0;
        for( StoreNameLinks info : store){
            i++;

            File storeDir = new File(destinationDir+info.getStore().toString());
            if(!storeDir.exists()){
                if(!storeDir.mkdir()) {
                    System.err.println("Couldn't create:" + storeDir);
                }
            }

            dumpsterWriter.append(info.getStore().toString()).append("\\").append(info.getName());
            File currentFile = new File(dumpsterWriter.toString());
            if(currentFile.isFile()){
                duplicate.add(info.getName());
                dumpsterWriter.delete(dumpsterWriter.lastIndexOf("\\"), dumpsterWriter.length());
                dumpsterWriter.setLength(resetPath);
                continue;
            }

            try{
                if(!currentFile.createNewFile()){
                    System.err.println("Couldn't create: " + currentFile);
                }
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(info.getLink())).build();
                client.send(request, HttpResponse.BodyHandlers.ofFile(currentFile.toPath()));
                client.close();
                if (dumpsterWriter.substring(dumpsterWriter.length() - 4, dumpsterWriter.length()).equals(".zip")){
                    dumpsterWriter.setLength(dumpsterWriter.lastIndexOf("\\")+1);
                    Unzipper.unzipAllInDir(dumpsterWriter.toString());
                }
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
