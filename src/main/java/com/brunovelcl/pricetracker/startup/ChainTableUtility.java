package com.brunovelcl.pricetracker.startup;
import com.brunovelcl.pricetracker.Text.Text;
import com.brunovelcl.pricetracker.database.entities.Chain;
import com.brunovelcl.pricetracker.database.repositories.ChainsRepository;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ChainTableUtility implements CommandLineRunner {

    private static final String BACKUP_FILENAME = "chainTableConstants.csv";
    private static final String FILEPATH = Text.Directories.DATA + BACKUP_FILENAME;

    private final ChainsRepository chainsRepository;

    public ChainTableUtility(ChainsRepository chainsRepository) {
        this.chainsRepository = chainsRepository;
    }

    private boolean tableEntriesExist(){
        return (chainsRepository.count() > 0);
    }

    private void writeTable() {
        System.out.print(Text.Messages.INIT_CHAIN_TABLE);
        try (BufferedReader br = Files.newBufferedReader(Path.of(FILEPATH))){
            String line = br.readLine();
            while (line != null){
                // [0] - chain [1] - webAddress [2] - downloadLink
                String[] columns = line.split(",");
                chainsRepository.save(new Chain(columns[0], columns[1], columns[2]));
                line = br.readLine();
            }
            if(tableEntriesExist()){
                System.out.print(Text.Messages.INIT_CHAIN_TABLE_OK);
            }else {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            System.err.printf(Text.ErrorMessages.WRITING_CHAIN_TABLE_FAILED, e);
        }
    }

    @Override
    public void run(String @NonNull ...args){
        if(tableEntriesExist()) return;
        writeTable();
    }
}
