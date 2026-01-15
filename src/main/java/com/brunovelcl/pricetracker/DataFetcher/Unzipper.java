package com.brunovelcl.pricetracker.DataFetcher;

import com.brunovelcl.pricetracker.Text.Text;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.*;

public class Unzipper {

    //scans the target dir unpacks all .zip files, and deletes the archives
    public static void unzipAllInDir(Path dir){

        File cwd = new File(dir.toString());
        File[] names = cwd.listFiles();
        if(names == null){
            return;
        }

        for(File file : names){
            String filename = file.getName();
            if (file.getName().contains(Text.Constants.ZIP_EXTENSION)){
                try (ZipInputStream zis = new ZipInputStream(Files.newInputStream(Paths.get(dir.toString(), filename)))){
                    ZipEntry entry;
                    while ((entry = zis.getNextEntry()) != null){
                        Files.copy(zis, Paths.get(dir.toString(), entry.getName()), StandardCopyOption.REPLACE_EXISTING);
                    }
                } catch (Exception e) {
                    System.err.println(Text.ErrorMessages.UNZIPPER_STREAM_CREATION_FAIL);
                    return;
                }
                if(!file.delete()){
                    System.err.printf( Text.ErrorMessages.UNZIPPER_FILE_DELETE_FAIL ,filename);
                }
            }
        }
    }
}
