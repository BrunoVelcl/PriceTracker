package FileFetcher;

import java.io.Serializable;
import java.util.List;
import java.time.LocalDate;

public class StoreNameLinks implements Serializable {
    private final String fileName;
    private final String link;
    private final Store store;
    private final int timestamp;


    public StoreNameLinks(String fileName, String link, Store store){
        this.fileName = fileName;
        this.link = link;
        this.store = store;
        LocalDate now = LocalDate.now();
        this.timestamp = (now.getYear() << 16) + now.getDayOfYear();

    }

    public String getName(){
        return fileName;
    }

    public String getLink(){
        return link;
    }

    public Store getStore() { return store;}

    public int getDay(){ return timestamp;}

    public boolean isEqual(StoreNameLinks other){
        return (this.fileName.equals(other.fileName) & this.link.equals(other.link));
    }

    public boolean inList(List<StoreNameLinks> list){
        if (list.isEmpty()){
            return false;
        }
        for(int i = list.size() - 1; i >= 0; i--){  // hits more likely towards the end
            if(isEqual(list.get(i))){
                return true;
            }
        }
        return false;
    }
}

