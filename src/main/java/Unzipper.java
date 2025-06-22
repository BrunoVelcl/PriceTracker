
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.*;

public class Unzipper {
    //scans the target dir unpacks all .zip files, and deletes the archives
    public static void unzipAllInDir(String dir){

        File cwd = new File(dir);
        File[] names = cwd.listFiles();
        if(names == null){
            return;
        }

        for(File file : names){
            String filename = file.getName();
            if (file.getName().contains(".zip")){
                try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Path.of(dir + filename)))){
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null){
                        Files.copy(zis, Path.of(dir + entry.getName()), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (Exception e) {
                    System.err.println("Failed to create input stream");
                    return;
                }
//                if(!file.delete()){
//                    System.err.println("Unzipping process couldn't delete file: " + filename);
//                }
                System.out.println("\u001b[32mUNZIPED\u001b[37m: " + filename);
            }
        }
    }
    public static void unzipAllInDir(){
        unzipAllInDir("G:\\Dev\\Prices\\dumpster\\");
    }
}
