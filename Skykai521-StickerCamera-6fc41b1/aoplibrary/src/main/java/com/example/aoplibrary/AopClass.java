package com.example.aoplibrary;

import android.util.Log;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class AopClass {
    private static final String ACTIVITY_CREATE_EXECUTION = "execution(* com.stickercamera.app.model.*.*(..))";
    private static final String LOG_TAG="AOP";
    @Pointcut(ACTIVITY_CREATE_EXECUTION)
    public void activityCreateExecution(){
    }
    @Around("activityCreateExecution()")
    public void aroundActivityCreateExecution(ProceedingJoinPoint joinPoint) throws  Throwable{
        String logClas = "+++++++++++++++++++++" + joinPoint.getThis().toString() + "++++++++++++++++++++++++++";

        FuncPerformance funcPerformance = new FuncPerformance(joinPoint.getThis().toString() +".."+ joinPoint.getSignature().getName());
        funcPerformance.initFunc();
        new Thread(funcPerformance).start();
        joinPoint.proceed();
        funcPerformance.stopFunc();
        Log.d(LOG_TAG, funcPerformance.toString());

        String logFunc = "+++++++++++++++++++++" + joinPoint.getSignature().getName() + "++++++++++++++++++++++++++";
    }

    private static final String ACTIVITY_CREATE_EXECUTION1 = "execution(* com.stickercamera.base.*.*(..))";
    @Pointcut(ACTIVITY_CREATE_EXECUTION1)
    public void activityCreateExecution1(){
    }
    @Around("activityCreateExecution1()")
    public void aroundActivityCreateExecution1(ProceedingJoinPoint joinPoint) throws  Throwable{
        String logClas = "+++++++++++++++++++++" + joinPoint.getThis().toString() + "++++++++++++++++++++++++++";

        FuncPerformance funcPerformance = new FuncPerformance(joinPoint.getThis().toString() +".."+ joinPoint.getSignature().getName());
        funcPerformance.initFunc();
        new Thread(funcPerformance).start();
        joinPoint.proceed();
        funcPerformance.stopFunc();
        Log.d(LOG_TAG, funcPerformance.toString());

        String logFunc = "+++++++++++++++++++++" + joinPoint.getSignature().getName() + "++++++++++++++++++++++++++";
    }

    private static final String ACTIVITY_CREATE_EXECUTION2 = "execution(* com.imagezoom.*.*(..))";
    @Pointcut(ACTIVITY_CREATE_EXECUTION2)
    public void activityCreateExecution2(){
    }
    @Around("activityCreateExecution2()")
    public void aroundActivityCreateExecution2(ProceedingJoinPoint joinPoint) throws  Throwable{
        String logClas = "+++++++++++++++++++++" + joinPoint.getThis().toString() + "++++++++++++++++++++++++++";

        FuncPerformance funcPerformance = new FuncPerformance(joinPoint.getThis().toString() +".."+ joinPoint.getSignature().getName());
        funcPerformance.initFunc();
        new Thread(funcPerformance).start();
        joinPoint.proceed();
        funcPerformance.stopFunc();
        Log.d(LOG_TAG, funcPerformance.toString());

        String logFunc = "+++++++++++++++++++++" + joinPoint.getSignature().getName() + "++++++++++++++++++++++++++";
    }
}

//@Aspect
//public class AopClass {
//    private static final String POINTCUT_METHOD = "execution(* com.stickercamera.app.model.*.*(..))";
//    private static final String POINTCUT_METHOD1 = "execution(* com.stickercamera.base.*.*(..))";
//    private static final String POINTCUT_METHOD2 = "execution(* com.imagezoom.*.*(..))";
//    private static int sSerial = 1;									                /* 函数调用顺序 */
//    private static Stack<String> stack = new Stack<String>();	                    /* 函数调用栈 */
//    private static Map<String, Integer> countMap = new HashMap<String, Integer>();  /* 函数调用次数计数 */
//    private static StringBuilder dot = new StringBuilder("\r\n");                   /* 字符串记录图 */
//    private static String sFilePath = "/storage/sdcard0/";
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
//
//    @Pointcut(POINTCUT_METHOD1)
//    public synchronized void methodAnnotatedWithDebugTrace1() {
//    }
//    @Around("methodAnnotatedWithDebugTrace1()")
//    public synchronized Object weaveJoinPoint1(ProceedingJoinPoint joinPoint) throws Throwable {
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
//
//    @Pointcut(POINTCUT_METHOD2)
//    public synchronized void methodAnnotatedWithDebugTrace2() {
//    }
//    @Around("methodAnnotatedWithDebugTrace2()")
//    public synchronized Object weaveJoinPoint2(ProceedingJoinPoint joinPoint) throws Throwable {
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
//
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