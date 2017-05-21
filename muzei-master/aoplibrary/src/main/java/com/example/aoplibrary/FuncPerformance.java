package com.example.aoplibrary;

import android.util.Log;

import java.text.DecimalFormat;

/**
 * Created by 文杰 on 2017/5/20.
 */

public class FuncPerformance implements Runnable{
    private static final String packageName = "net.nurik.roman.muzei";
    private String FuncName;
    private String memoryInfo;
    private double cpuInfo;
    private long cpuTime;
    private long startTime;
    private long endTime;
    private boolean mutex;      //当函数执行结束时，判断线程是否执行结束

    FuncPerformance(String FuncName)
    {
        this.FuncName = FuncName;
    }

    public void initFunc() {
        cpuTime = 0;
        startTime = 0;
        endTime = 0;
        mutex = false;
    }

    public void stopFunc() {
        endTime = System.nanoTime();
    }

    @Override
    public void run() {
        try{
            startTime = System.nanoTime();
            double maxCpu = 0;
            double maxVirtualMemory = 0;
            double maxRealMemory = 0;
            double maxPss = 0;
            double maxUss = 0;
            while(endTime == 0)
            {
                Log.d("aopTest",String.valueOf(endTime));
                double[] performanceInfo = getPerformanceInfo(packageName);
                if(performanceInfo[0] > maxCpu){
                    maxCpu = performanceInfo[0];
                }
                if(performanceInfo[1] > maxVirtualMemory){
                    maxVirtualMemory = performanceInfo[1];
                }
                if(performanceInfo[2] > maxRealMemory){
                    maxRealMemory = performanceInfo[2];
                }
                if(performanceInfo[3] > maxPss){
                    maxPss = performanceInfo[3];
                }
                if(performanceInfo[4] > maxUss){
                    maxUss = performanceInfo[4];
                }
                Thread.sleep(0,100);
            }
            this.setCpuInfo(maxCpu);
            this.setMemoryInfo(maxVirtualMemory,maxRealMemory,maxPss,maxUss);
            this.setCpuTime();
            mutex = true;
        }catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    private void setCpuInfo(double maxCpu)
    {
        this.cpuInfo = maxCpu;
    }

    private void setMemoryInfo(double virtualMemory,double realMemory,double pss,double uss)
    {
        this.memoryInfo = formatByte(virtualMemory,realMemory,pss,uss);
    }

    private void setCpuTime()
    {
        this.cpuTime = endTime - startTime;
    }

    public String toString() {
        while(mutex == false)
        {
            try{
                Thread.sleep(1);
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
        String retString = "";
        retString = retString + "FuncName: " + FuncName + " --> ";
        retString = retString + "memoryInfo: " + memoryInfo + " --> ";
        retString = retString + "cpuInfo: " + cpuInfo + " --> ";
        retString = retString + "cpuTime: " + cpuTime + ";\n";
        return retString;
    }

    /**
     * 格式化数据
     * @param virtualMemory,realMemory
     * @return
     */
    private String formatByte(double virtualMemory, double realMemory,double pss,double uss){
        DecimalFormat format = new DecimalFormat("##.##");
        String memorySize;
        memorySize = "virtualMemory: ";
        if(virtualMemory < 1024){
            memorySize = memorySize + virtualMemory+"KB";
        }else if(virtualMemory < 1024 * 1024){
            memorySize = memorySize + format.format(virtualMemory/1024.0) +"MB";
        }else if(virtualMemory < 1024 * 1024 * 1024){
            memorySize = memorySize + format.format(virtualMemory/1024.0/1024.0) +"GB";
        }else{
            return "超出统计范围";
        }

        memorySize = memorySize + ";  realMemory: ";
        if(realMemory < 1024){
            memorySize = memorySize + realMemory+"KB";
        }else if(realMemory < 1024 * 1024){
            memorySize = memorySize + format.format(realMemory/1024.0) +"MB";
        }else if(realMemory < 1024 * 1024 * 1024){
            memorySize = memorySize + format.format(realMemory/1024.0/1024.0) +"GB";
        }else{
            return "超出统计范围";
        }

        memorySize = memorySize + ";  pss: ";
        if(pss < 1024){
            memorySize = memorySize + pss+"KB";
        }else if(pss < 1024 * 1024){
            memorySize = memorySize + format.format(pss/1024.0) +"MB";
        }else if(pss < 1024 * 1024 * 1024){
            memorySize = memorySize + format.format(pss/1024.0/1024.0) +"GB";
        }else{
            return "超出统计范围";
        }

        memorySize = memorySize + ";  uss: ";
        if(uss < 1024){
            memorySize = memorySize + uss+"KB";
        }else if(uss < 1024 * 1024){
            memorySize = memorySize + format.format(uss/1024.0) +"MB";
        }else if(uss < 1024 * 1024 * 1024){
            memorySize = memorySize + format.format(uss/1024.0/1024.0) +"GB";
        }else{
            return "超出统计范围";
        }

        return memorySize;
    }

    public static double[] getPerformanceInfo(String PackageName){
//        double cpuDouble = 0;
//        double virtualMemory = 0;
//        double realMemory = 0;
//        double pss = 0;
//        double uss = 0;
        //依次为以上信息
        double performanceInfo[] = {0.0, 0.0, 0.0,0.0,0.0};

        String cmd="top -n 1| grep "+PackageName;
        String info = new ExeCommand().run(cmd, 10000).getResult();

        String  cpu=info.substring(8,12);
        cpu=cpu.trim();
        performanceInfo[0] = Double.parseDouble(cpu);

        String virtualMemory_tmp=info.substring(21,29);
        virtualMemory_tmp = virtualMemory_tmp.trim();
        performanceInfo[1] = Double.parseDouble(virtualMemory_tmp);

        String realMemory_tmp= info.substring(30,37);
        realMemory_tmp=realMemory_tmp.trim();
        performanceInfo[2] = Double.parseDouble(realMemory_tmp);

        cmd="procrank | grep "+PackageName;
        info = new ExeCommand().run(cmd, 10000).getResult();
        Log.i("auto",info);
        int index = info.indexOf(packageName);
        String pss_tmp=info.substring(index - 20,index - 12);
        pss_tmp = pss_tmp.trim();

        performanceInfo[3] = Double.parseDouble(pss_tmp);

        String uss_tmp= info.substring(index - 11,index - 3);
        uss_tmp=uss_tmp.trim();
        performanceInfo[4] = Double.parseDouble(uss_tmp);

        return performanceInfo;
    }
}

    /**
     * 获取系统进程信息列表
     * @param context
     * @return
     */
//    public static long getMemoryInfo(Context context,String packageName) throws IOException {
//        long totalPrivateDirty = 0;
//        PackageManager pm = context.getPackageManager();
//        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
//        List<ActivityManager.RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
//        for(ActivityManager.RunningAppProcessInfo info : runningAppProcesses){
//            //进程名称
//            if(packageName == info.processName)
//            {
//                //获取进程占用的内存
//                int pid = info.pid;
//                android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{pid});
//                android.os.Debug.MemoryInfo memoryInfo  = processMemoryInfo[0];
//                totalPrivateDirty = memoryInfo.getTotalPrivateDirty(); //KB
//                break;
//            }
//        }
//        return totalPrivateDirty;
//    }