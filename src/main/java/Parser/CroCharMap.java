package Parser;

import java.util.HashMap;
import java.util.Map;

public class CroCharMap {
    private final Map<Character, String> map;

    public CroCharMap() {
        this.map = new HashMap<>();
        this.map.put((char)0x8a,"Š");
        this.map.put((char)0x9a,"š");
        this.map.put((char)0x8e,"Ž");
        this.map.put((char)0x9e,"ž");
        this.map.put((char)0xc8,"Č");
        this.map.put((char)0xe8,"č");
        this.map.put((char)0xc6,"Ć");
        this.map.put((char)0xe6,"ć");
        this.map.put((char)0xe0,"Đ");
        this.map.put((char)0xf0,"đ");
    }

    public String replaceString(StringBuilder sb){

        for (int i = 0; i < sb.length(); i++){
            if(this.map.containsKey(sb.charAt(i))){
                sb.replace(i, i + 1, map.get(sb.charAt(i)));
            }
        }
        return sb.toString();
    }
}
