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
        List<StoreNameLinks> existingLinks;
        File linksBin = new File("links.bin");
        if(!linksBin.exists()){
            try {
                if(!linksBin.createNewFile()){
                    System.out.println("File system problem, couldn't create files.");
                }
            }catch (IOException e){
                System.out.println("File system problem, couldn't create files.");
                return;
            }
        }

        try(ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(linksBin))){
            existingLinks = (List<StoreNameLinks>) inputStream.readObject();
        }catch (IOException | ClassNotFoundException e){
            System.err.println("WARNING: required file not found attempting to generate new one.");
           existingLinks = new ArrayList<>();
        }
            //Get store links
            List<StoreNameLinks>toDownload = LinkFetcher.getAllStores(existingLinks);
            downloadFromLinks(toDownload,existingLinks, destinationDir);

        try(ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(linksBin))){
            outputStream.writeObject(existingLinks);
        }catch(IOException e){
            System.err.println("FAILED TO WRITE LINK DATA TO DISK");
        }

        System.out.println("\u001b[32mDOWNLOAD COMPLETE\u001b[37m");
    }

    //TODO: DELETE, this bullshit shouldn't exist
    public void download(){
        download("G:\\Dev\\Prices\\dumpster\\");
    }

    private void downloadFromLinks(List<StoreNameLinks> store, List<StoreNameLinks>existingLinks, String destinationDir) {

        StringBuilder dumpsterWriter = new StringBuilder();
        dumpsterWriter.append(destinationDir);
        int resetPath = dumpsterWriter.length();

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
                dumpsterWriter.delete(dumpsterWriter.lastIndexOf("\\"), dumpsterWriter.length());
                dumpsterWriter.setLength(resetPath);
                continue;
            }

            try{    //File download
                if(!currentFile.createNewFile()){
                    System.err.println("Couldn't create: " + currentFile);
                }
                HttpClient client = HttpClient.newHttpClient();
                HttpRequest request = HttpRequest.newBuilder().uri(URI.create(info.getLink())).build();
                System.out.printf("Downloading %d/%d \r", i, store.size());
                System.out.flush();
                client.send(request, HttpResponse.BodyHandlers.ofFile(currentFile.toPath()));
                client.close();
                if (dumpsterWriter.substring(dumpsterWriter.length() - 4, dumpsterWriter.length()).equals(".zip")){
                    dumpsterWriter.setLength(dumpsterWriter.lastIndexOf("\\")+1);
                    Unzipper.unzipAllInDir(dumpsterWriter.toString());
                }
                downloaded.add(info.getName());

            }catch (IOException | InterruptedException e){
                System.err.println("Couldn't create file: " + currentFile.getAbsolutePath());
                System.err.println("File was skipped");
            }
            dumpsterWriter.setLength(resetPath);
            existingLinks.add(info);
        }

        File downloadedLog = new File("G:\\Dev\\Prices\\logs\\downloaded_files.txt");

        try(FileWriter downloadedWriter = new FileWriter(downloadedLog)){

            for(String fileName : downloaded){
                downloadedWriter.append(fileName).append("\n");
            }
        } catch ( IOException e) {
            System.err.println("Couldn't write dumpster file logs.");
        }

        System.out.printf("Downloaded files: %d\n", downloaded.size());
    }
}
