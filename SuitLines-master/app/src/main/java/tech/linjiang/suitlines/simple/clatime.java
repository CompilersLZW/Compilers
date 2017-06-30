package tech.linjiang.suitlines.simple;

/**
 * Created by apple on 2017/5/24.
 */
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;
import java.util.Map.Entry;
/**
 * Created by apple on 2017/5/18.
 */

class strings{

    String fucname;
    String startorend;
    long nowtime;
    strings(String a,String b,long c)
    {

        this.fucname=a;
        this.startorend=b;
        this.nowtime=c;
    }
}



public class clatime {

    Map<String, Long> testmap = new HashMap<String , Long>();

    Stack<strings> stack = new Stack<strings>();

    public String[]  split(String a)
    {
        String[] sourceStrArray = a.split(":");
        return sourceStrArray;
    }

    public void getTime(ArrayList<String> aList) throws IOException {
//		FileReader reader = new FileReader(("temp\\text.txt"));
//		BufferedReader br = new BufferedReader(reader);
        //String str = null;
        //	clatime this= new clatime();
        long time=0;
//        for (Iterator it2 = aList.iterator(); it2.hasNext();) {
//            System.out.println(it2.next());
//        }
        for(String str:aList)
        {

            String[] splits = this.split(str);

            long nowtime = Long.parseLong(splits[2]);

            boolean flag= this.testmap.containsKey(splits[0]);//检查函数是否存在

            if(flag==false)//不存在，必然是start,直接压入栈
            {

                if(this.stack.size()>0)
                {
                    long bettime=nowtime-time;

                    this.testmap.put(this.stack.lastElement().fucname, bettime);
                }
                time=nowtime;
                this.stack.add(new strings(splits[0],splits[1],nowtime));
                this.testmap.put(splits[0],nowtime-time);

            }
            else	//已经存在
            {
                if(splits[1].equals("start"))
                {
                    if(this.stack.size()>0)
                    {
                        long bettime=nowtime-time;

                        this.testmap.put(this.stack.lastElement().fucname, this.testmap.get(this.stack.lastElement().fucname)+bettime);
                        // this.testmap.put(this.stack.lastElement().fucname, bettime);
                    }
                    time=nowtime;

                    this.stack.add(new strings(splits[0],splits[1],nowtime));
                }

                else	//函数结束
                {
                    long bettime=nowtime-time;

                    this.testmap.put(splits[0], this.testmap.get(splits[0])+bettime);

                    this.stack.pop();

                    time=nowtime;
                }
            }

        }
//		br.close();
//		reader.close();
        String TAG="ZhangJiaRui";
        String rs=null;
        Log.d(TAG,"funcnum "+ this.testmap.size());

        for (Entry<String, Long> entry : this.testmap.entrySet()) {
            rs= entry.getKey() + "  " + entry.getValue()/1000000;
            Log.d(TAG,rs);
        }
    }

}

