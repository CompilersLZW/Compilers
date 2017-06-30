package tech.linjiang.suitlines.simple;

/**
 * Created by apple on 2017/5/24.
 */

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fqth on 2017/5/23.
 */

public class clacounts {

    public void getCounts(HashMap<String , Long> countList) throws IOException {

        String TAG = "ZhangJiaRui";
        String rs = null;
        for (HashMap.Entry<String, Long> entry : countList.entrySet()) {
            rs = entry.getKey() + "  " + entry.getValue();
            Log.d(TAG, rs);
        }

    }


}
