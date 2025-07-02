package Parser;

import FileFetcher.Store;

import java.io.File;
import java.sql.Connection;
import java.util.List;
import java.util.Scanner;

public abstract class Parser {

    protected File[] fileList;
    protected final Store chain;
    StoreInfo storeInfo;

    protected Parser(File[] fileList , Store chain) {
        this.fileList = fileList;
        this.chain = chain;
    }
}

