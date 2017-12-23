package galiazat.btprivatetransferlibrary.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Azat on 12.11.17.
 */

public class StaticIdGenerator {

    private static StaticIdGenerator sInstance;
    private List<Integer> ids = new ArrayList<>();

    public static StaticIdGenerator getInstance(){
        if (sInstance == null){
            sInstance = new StaticIdGenerator();
        }
        return sInstance;
    }

    private StaticIdGenerator(){}

    public int generateId(){
        if (ids.size() == 0){
            ids.add(0);
            return 0;
        } else {
            int value = ids.get(ids.size() - 1);
            value++;
            ids.add(value);
            return value;
        }
    }

}
