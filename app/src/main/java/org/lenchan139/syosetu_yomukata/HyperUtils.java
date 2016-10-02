package org.lenchan139.syosetu_yomukata;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by len on 10/2/16.
 */

public class HyperUtils {
    public Map<String,String> convertCookiesToMapArray(String in){
        Map<String,String> map = new HashMap<>();
        int endCount = -1;
        String shorter  = in;
        ArrayList<String>  list = new ArrayList<>();
        while(shorter.indexOf(";") != -1){
            String insert = shorter.substring(0,shorter.indexOf(";"));
            shorter = shorter.substring(shorter.indexOf(";")+1);
            String first = insert.substring(0,insert.indexOf("="));
            String last = insert.substring(insert.indexOf("="));
            map.put(first,last);
            endCount++;
        }
        if(map.size() >= 1){
            return map;
        }else{
            return null;
        }
    }
}
