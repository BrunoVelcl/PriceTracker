package FileFetcher;

import java.io.*;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Downloader {

    private static final File LINKSBIN = new File("links.bin");
    
    public boolean download(String destinationDir){
        
        if(!LINKSBIN.exists()){
            try {
                if(!LINKSBIN.createNewFile()){
                    System.out.println("File system problem, couldn't create files.");
                }
            }catch (IOException e){
                System.out.println("File system problem, couldn't create files.");
                return false;
            }
        }

        List<StoreNameLinks> existingLinks = loadLinks(LINKSBIN);

        //Get store links
            List<StoreNameLinks>toDownload = LinkFetcher.getAllStores(existingLinks);

            if(toDownload.isEmpty()){
                System.out.println("\u001b[32mDATA IS ALREADY UP TO DATE.\u001b[37m");
                return false;
            }

            downloadFromLinks(toDownload,existingLinks, destinationDir);

        saveLinks(LINKSBIN, existingLinks);
        System.out.println("\u001b[32mDOWNLOAD COMPLETE\u001b[37m");
        return true;
    }

    private static void saveLinks(File LINKSBIN, List<StoreNameLinks> existingLinks) {
        try(DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(LINKSBIN)))){
            dos.writeInt(existingLinks.size());
            for(StoreNameLinks link : existingLinks){
                dos.writeUTF(link.getFileName());
                dos.writeUTF(link.getLink());
                dos.writeInt(link.getStore().ordinal());
                dos.writeInt(link.getTimestamp());
            }
        }catch (IOException e){
            System.err.println("FAILED TO WRITE LINK DATA TO DISK" + e.getMessage());
        }
    }

    private static List<StoreNameLinks> loadLinks(File LINKSBIN) {
        List<StoreNameLinks> links = new ArrayList<>();
        try(DataInputStream dis = new DataInputStream(new BufferedInputStream(new FileInputStream(LINKSBIN)))){
            String fileName;
            String link;
            Store store;
            int timestamp;
            int entryCnt = dis.readInt(); // First entry is an int with len()
            for(int i = 0; i < entryCnt; i++){
                fileName = dis.readUTF();
                link = dis.readUTF();
                store = Store.fromOrdinal(dis.readInt());
                timestamp = dis.readInt();
                links.add(new StoreNameLinks(fileName, link, store, timestamp));
            }
        }catch (IOException e){
            System.err.println("FAILED TO READ LINK DATA TO DISK" + e.getMessage());
        }
        return links;
    }

    //TODO: DELETE, this bullshit shouldn't exist
    public boolean download(){
        return download("G:\\Dev\\Prices\\dumpster\\");
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

    /**Use for removing all files from the downloaded list for a given day*/
    public void resetDay(int year, int month, int day ){
        LocalDate enteredDate = LocalDate.of(year, month, day);
        List<StoreNameLinks> list = loadLinks(LINKSBIN);
        int dayToDelete = (year << 16) + enteredDate.getDayOfYear();
        list.removeIf(link -> link.getTimestamp() == dayToDelete);
        saveLinks(LINKSBIN, list);
    }
}
