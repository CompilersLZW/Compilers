package com.example.aoplibrary;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Created by 文杰 on 2017/5/18.
 */

@Aspect
public class AopClass {
    private static final String ACTIVITY_CREATE_EXECUTION = "execution(* *..onCreate(..))";
    private static final String LOG_TAG="auto";
    @Pointcut(ACTIVITY_CREATE_EXECUTION)
    public void activityCreateExecution(){
    }
    @Around("activityCreateExecution()")
    public void aroundActivityCreateExecution(ProceedingJoinPoint joinPoint) throws  Throwable{
        String logClas = "+++++++++++++++++++++" + joinPoint.getThis().toString() + "++++++++++++++++++++++++++";

        FuncPerformance funcPerformance = new FuncPerformance(joinPoint.getSignature().getName());
        funcPerformance.initFunc();
        new Thread(funcPerformance).start();
        joinPoint.proceed();
        funcPerformance.stopFunc();
        Log.d(LOG_TAG, funcPerformance.toString());

        String logFunc = "+++++++++++++++++++++" + joinPoint.getSignature().getName() + "++++++++++++++++++++++++++";
        Log.d(LOG_TAG,logClas);
        Log.d(LOG_TAG,logFunc);
    }
}

//@Aspect
//public class AopClass {
//    private static final String POINTCUT_METHOD = "execution(* com.hail_hydra.time..*.*(..))";
//    private static int sSerial = 1;									                /* 函数调用顺序 */
//    private static int sTreeNum = 1;                                                /* 图编号 */
//    private static Stack<String> stack = new Stack<String>();	                    /* 函数调用栈 */
//    private static Map<String, Integer> countMap = new HashMap<String, Integer>();  /* 函数调用次数计数 */
//    private static StringBuilder dot = new StringBuilder("\r\n");                   /* 字符串记录图 */
//    private static String sFilePath = "/storage/sdcard/";
//    private static final String FLITER_STR = "com.hail_hydra.time.";            /* 过滤长类名 */
//
//    @Pointcut(POINTCUT_METHOD)
//    public synchronized void methodAnnotatedWithDebugTrace() {
//    }
//    @Around("methodAnnotatedWithDebugTrace()")
//    public synchronized Object weaveJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
//        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
//        String signatureStr ="\"" + methodSignature.toString().replace(FLITER_STR,"")+ "\"";
////        String className = methodSignature.getDeclaringType().getCanonicalName();
////        String methodName = methodSignature.getName();
////        String realName = className +"." + methodName;
//        Log.d("AOP","===before===" + methodSignature + "===before===");
//        if(stack.size() == 0){                                                  /* 若为根节点 */
//            dot.append(signatureStr +"\r\n");
//        }
//        else{                                                                   /* 不为根节点 */
//            dot.append(stack.peek() + "->" + signatureStr);
//            dot.append("[label=\"" + sSerial + "\"]\r\n");                      /* 加标签 */
//            sSerial++;
//        }
//        if(countMap.containsKey(signatureStr)){
//            countMap.put(signatureStr, countMap.get(signatureStr) + 1);         /* 计数加一 */
//        }
//        else{
//            countMap.put(signatureStr,1);
//        }
//        stack.push(signatureStr);
//        Object result = joinPoint.proceed();
//        stack.pop();
//        if (stack.size() == 0) {                                                /* 画完一个根节点 */
//            writeTreeFile(dot.toString());
//            writeTimeFile(countMap);
//            dot = new StringBuilder("\r\n");
//        }
//        Log.d("AOP", "===after====" + methodSignature + "===after====");
//        return result;
//    }
//    synchronized void writeTreeFile(String dotStr) {
//    String dotFile = sFilePath + "AOP.txt";
//        try {
//            File file = new File(dotFile);
//            if (!file.exists()) {
//                Log.d("AOP", "Create the file:" + dotFile);
//                file.createNewFile();
//            }
//            RandomAccessFile raf = new RandomAccessFile(file, "rw");
//            raf.seek(file.length());
//            raf.write(dotStr.getBytes());
//            raf.close();
//        } catch (Exception e) {
//            Log.e("AOP", "Error on write File.");
//        }
//    }
//    synchronized void writeTimeFile(Map<String, Integer> countMap) {
//        String dotFile = sFilePath + "CNT.txt";
//        try {
//            File file = new File(dotFile);
//            if (!file.exists()) {
//                Log.d("AOP", "Create the file:" + dotFile);
//                file.createNewFile();
//            }
//            RandomAccessFile raf = new RandomAccessFile(file, "rw");
//            if(!countMap.isEmpty()){
//                for(Map.Entry<String, Integer> entry : countMap.entrySet()){
//                    raf.write((entry.toString()+ "\r\n").getBytes());
//                }
//            }
//            raf.close();
//        } catch (Exception e) {
//            Log.e("AOP", "Error on write File.");
//        }
//    }
//}

//@Aspect
//public class AopClass {
//    private static  final String POINTCUT_METHOD = "execution(* com.hail_hydra.time..*.*(..))";
//    private static boolean sMutex;                                              /* 互斥锁 */
//    private static int sSerial = 1;									            /* 函数调用顺序 */
//    private static int sTreeNum = 1;                                            /* 图编号 */
//    private static Stack<String> stack = new Stack<String>();		            /* 函数调用栈 */
//    private static StringBuilder dot = new StringBuilder("digraph ");           /* 字符串记录图 */
//    private static String sFilePath = "/storage/sdcard/Movies/";
//    private synchronized static boolean consumeMutex(){
//        if(sMutex){
//            sMutex = false;
//            return true;
//        }
//        else{
//            return false;
//        }
//    }
//    private synchronized static void setsMutex(boolean mutex){
//        sMutex = mutex;
//    }
//    @Pointcut(POINTCUT_METHOD)
//    public synchronized void methodAnnotatedWithDebugTrace() {
//    }
//    @Around("methodAnnotatedWithDebugTrace()")
//    public synchronized void around(ProceedingJoinPoint joinPoint) throws Throwable {
//        String methodSignature = joinPoint.getSignature().toString();
//        String methodName = joinPoint.getSignature().getName().toString();
//        methodSignature = methodSignature.replace(" ", "_");
//        //Log.d("AOP","===before===" + methodSignature + "===before===");
//        if(stack.size() == 0){                                                  /* 若为根节点 */
//            dot.append(methodName + sTreeNum +"{\r\n");
//        }
//        else{                                                                   /* 不为根节点 */
//            dot.append(stack.peek() + "->" + methodSignature);
//            dot.append("\"[label=\"" + sSerial + "\"]\r\n");                    /* 加标签 */
//        }
//        stack.push(methodSignature);
//        joinPoint.proceed();
//        stack.pop();
//        sSerial++;
//        if (stack.size() == 0) {                                                  /* 一幅图结束 */
//            dot.append("}");
//            writeTreeFile(dot.toString(), methodName + sTreeNum);
//            System.out.println(dot.toString());
//            sTreeNum++;
//            dot = new StringBuilder("digraph ");
//        }
//        //Log.d("AOP", "===after====" + methodSignature + "===after====");
//        return;
//    }
//    synchronized void writeTreeFile(String dotStr, String name) {
//        String dotFile = sFilePath + name + ".txt";
//        try {
//            File file = new File(dotFile);
//            if (!file.exists()) {
//                Log.d("AOP", "Create the file:" + dotFile);
//                file.createNewFile();
//            }
//            RandomAccessFile raf = new RandomAccessFile(file, "rw");
//            raf.seek(file.length());
//            raf.write(dotStr.getBytes());
//            raf.close();
//        } catch (Exception e) {
//            Log.e("AOP", "Error on write File.");
//        }
//    }
    //@Before("methodAnnotatedWithDebugTrace()")
//    public synchronized void beforeAround(JoinPoint joinPoint) throws Throwable {
//
//        String methodSignature = joinPoint.getSignature().toString();
//        String methodName = joinPoint.getSignature().getName().toString();
//        methodSignature = methodSignature.replace(" ", "_");
//        Log.d("AOP","===before==="+methodSignature+"===before===");
//        if(stack.size() == 0){                                                  /* 若为根节点 */
//            dot.append(methodName + sTreeNum +"{\r\n");
//        }
//        else{                                                                   /* 不为根节点 */
//            dot.append(stack.peek() + "->" + methodSignature);
//            dot.append("\"[label=\"" + sSerial + "\"]\r\n");                    /* 加标签 */
//        }
//        stack.push(methodSignature);
//        return;
//    }
//        @After("methodAnnotatedWithDebugTrace()")
//        public synchronized void afterAround (JoinPoint joinPoint)throws Throwable {
//            String methodSignature = joinPoint.getSignature().toString();
//            String methodName = joinPoint.getSignature().getName().toString();
//            stack.pop();
//            sSerial++;
//            if (stack.size() == 0) {                                                  /* 一幅图结束 */
//                dot.append("}");
//                writeTreeFile(dot.toString(), methodName + sTreeNum);
//                dot = new StringBuilder("digraph ");
//            }
//            Log.d("AOP", "===after====" + methodSignature + "===after====");
//            return;
//        }
//}

/** //失败的控制流
 @Aspect
 public class AopClass {
 private static final String ACTIVITY_CREATE_EXECUTION = "execution(* com.hail_hydra.time..*.*(..))";
 private static final String ACTIVITY_CREATE_CALL = "call(* com.hail_hydra.time..*.*(..))";

 static final String LOG_TAG = "AOP";
 @Pointcut(ACTIVITY_CREATE_EXECUTION)
 public void activityCreateExecution(){
 }
 @Pointcut(ACTIVITY_CREATE_CALL)
 public void activityCreateCall() {
 }
 @Before("activityCreateExecution()")
 public void beforeActivityCreateExecution(JoinPoint joinPoint)throws Throwable{
 String logClas;
 String logFunc;
 if (joinPoint ==null){
 Log.d(LOG_TAG,"------------------------------------------------null joinPoint error\n-");
 return;
 }
 if(joinPoint.getThis()!=null){
 logClas = "before exec============" + joinPoint.getThis().toString() + "==================";
 logFunc = "before exec============" + joinPoint.getSignature() + "==================";
 }
 else{
 logClas = "before exec============static ";
 logFunc = "before exec============static " + joinPoint.getSignature().toString() + "==================";
 }
 Log.d(LOG_TAG, logClas);
 Log.d(LOG_TAG, logFunc + "\n-");
 }
 @After("activityCreateExecution()")
 public void afterActivityCreateExecution(JoinPoint joinPoint)throws Throwable{
 String logClas;
 String logFunc;
 if (joinPoint ==null){
 Log.d(LOG_TAG,"------------------------------------------------null joinPoint error\n-");
 return;
 }
 if(joinPoint.getThis()!=null){
 logClas = "after  exec++++++++++++" + joinPoint.getThis().toString() + "++++++++++++";
 logFunc = "after  exec++++++++++++" + joinPoint.getSignature() + "++++++++++++";
 }
 else{
 logClas = "after  exec++++++++++++static ";
 logFunc = "after  exec++++++++++++static " + joinPoint.getSignature().toString() + "++++++++++++";
 }
 Log.d(LOG_TAG, logClas);
 Log.d(LOG_TAG, logFunc + "\n-");
 }
 @Before("activityCreateCall()")
 public void beforeActivityCreateCall(JoinPoint joinPoint)throws Throwable{
 String logClas;
 String logFunc;
 if (joinPoint ==null){
 Log.d(LOG_TAG,"------------------------------------------------null joinPoint error\n-");
 return;
 }
 if(joinPoint.getThis()!=null){
 logClas = "before call++++++++++++" + joinPoint.getThis().toString() + "++++++++++++";
 logFunc = "before call++++++++++++" + joinPoint.getSignature() + "++++++++++++";
 }
 else{
 logClas = "before call++++++++++++static ";
 logFunc = "before call++++++++++++static " + joinPoint.getSignature().toString() + "++++++++++++";
 }
 Log.d(LOG_TAG, logClas);
 Log.d(LOG_TAG, logFunc + "\n-");
 }
 @After("activityCreateCall()")
 public void afterActivityCreateCall(JoinPoint joinPoint)throws Throwable{
 String logClas;
 String logFunc;
 if (joinPoint ==null){
 Log.d(LOG_TAG,"------------------------------------------------null joinPoint error\n-");
 return;
 }
 if(joinPoint.getThis()!=null){
 logClas = "after  call++++++++++++" + joinPoint.getThis().toString() + "++++++++++++";
 logFunc = "after  call++++++++++++" + joinPoint.getSignature() + "++++++++++++";
 }
 else{
 logClas = "after  call++++++++++++static ";
 logFunc = "after  call++++++++++++static " + joinPoint.getSignature().toString() + "++++++++++++";
 }
 Log.d(LOG_TAG, logClas);
 Log.d(LOG_TAG, logFunc + "\n-");
 }
 private static final String CFLOW = "cflow(activityCreateExecution()) && !within(AopClass)";
 @Pointcut(CFLOW)
 public void cflow(){
 }
 @Before("cflow() && activityCreateCall()")
 public void beforeCflow(JoinPoint joinPoint)throws Throwable{
 String logClas = "---------------------------test-------------------------------";
 String logFunc = "---------------------------test-------------------------------";
 if (joinPoint ==null){
 Log.d(LOG_TAG,"------------------------------------------------null joinPoint error\n-");
 return;
 }
 if(joinPoint.getThis()!=null){
 logClas = "before flow************" + joinPoint.getThis().toString() + "************";
 logFunc = "before flow************" + joinPoint.getSignature() + "************";
 }
 else{
 logClas = "before flow************static ";
 logFunc = "before flow************static " + joinPoint.getSignature().toString() + "************ss";
 }
 Log.d(LOG_TAG, logClas);
 Log.d(LOG_TAG, logFunc + "\n-");
 }
 @After("cflow()")
 public void afterCflow(JoinPoint joinPoint)throws Throwable{
 String logClas;
 String logFunc;
 if (joinPoint ==null){
 Log.d(LOG_TAG,"------------------------------------------------null joinPoint error\n-");
 return;
 }
 if(joinPoint.getThis()!=null){
 logClas = "after  flow************" + joinPoint.getThis().toString() + "************";
 logFunc = "after  flow************" + joinPoint.getSignature() + "************";
 }
 else{
 logClas = "after  flow************static ";
 logFunc = "after  flow************static " + joinPoint.getSignature().toString() + "************ss";
 }
 Log.d(LOG_TAG, logClas);
 Log.d(LOG_TAG, logFunc + "\n-");
 }
 }
 */