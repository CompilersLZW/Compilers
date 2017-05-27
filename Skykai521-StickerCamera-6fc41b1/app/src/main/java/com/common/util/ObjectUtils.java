package com.common.util;

/**
 * Object Utils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-10-24
 */
public class ObjectUtils {

    private ObjectUtils() {
        throw new AssertionError();
    }

    /**
     * 比较两个对象
     * 
     * @param actual
     * @param expected
     * @return <ul>
     *         <li>if both are null, return true</li>
     *         如果两个对象都为空则返回true
     *         如果expected为空则返回actual否则返回比较的结果
     *         </ul>
     */
    public static boolean isEquals(Object actual, Object expected) {
        return actual == expected || (actual == null ? expected == null : actual.equals(expected));
    }

    /**
     * null Object to empty string
     * 将空的对象装换为空的字符串
     * <pre>
     * nullStrToEmpty(null) = &quot;&quot;;
     * 如果传入的对象为空则返回”“
     * nullStrToEmpty(&quot;&quot;) = &quot;&quot;;
     * 如果如果判断为字符串型则转换为字符串
     * nullStrToEmpty(&quot;aa&quot;) = &quot;aa&quot;;
     * </pre>
     * 
     * @param str //传入的字符串
     * @return
     */
    public static String nullStrToEmpty(Object str) {
        return (str == null ? "" : (str instanceof String ? (String)str : str.toString()));
    }

    /**
     * convert long array to Long array
     *将长整形数组装换为长整形数组
     * 将输入的参数source按顺序保存在destin中返回
     * @param source
     * @return
     */
    public static Long[] transformLongArray(long[] source) {
        Long[] destin = new Long[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    /**
     * convert Long array to long array
     * 将Long型数组装换为long型数组
     * @param source 输入的参数source
     * @return
     */
    public static long[] transformLongArray(Long[] source) {
        long[] destin = new long[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    /**
     * convert int array to Integer array
     * 将整形数组转换为复杂类型的整形数组
     * 证明一个新的整形数组destin将输入的参数source按顺序保存在destin中返回
     * @param source
     * @return
     */
    public static Integer[] transformIntArray(int[] source) {
        Integer[] destin = new Integer[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    /**
     * convert Integer array to int array
     * 将复杂类型的integer类数组转化为int型数组
     * @param source
     * @return
     */
    public static int[] transformIntArray(Integer[] source) {
        int[] destin = new int[source.length];
        for (int i = 0; i < source.length; i++) {
            destin[i] = source[i];
        }
        return destin;
    }

    /**
     * compare two object
     * <ul>
     * <strong>About result</strong>
     * <li>if v1 > v2, return 1</li>
     * 如果v1>v2返回1
     * <li>if v1 = v2, return 0</li>
     * 如果v1=v2 返回0
     * <li>if v1 < v2, return -1</li>
     * 如果v1<v2 返回-1
     * </ul>
     * <ul>
     * <strong>About rule</strong>
     * <li>if v1 is null, v2 is null, then return 0</li>
     * 如果v1为空,v2为空则返回0
     * <li>if v1 is null, v2 is not null, then return -1</li>
     * 如果v1为空，v2不为空则返回-1
     * <li>if v1 is not null, v2 is null, then return 1</li>
     * 付过v1不为空，v2为空则返回1
     * <li>return v1.{@link Comparable#compareTo(Object)}</li>
     * 否则进入比较函数比较
     * </ul>
     * 
     * @param v1 输入参数1
     * @param v2 输入参数2
     * @return
     */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <V> int compare(V v1, V v2) {
        return v1 == null ? (v2 == null ? 0 : -1) : (v2 == null ? 1 : ((Comparable)v1).compareTo(v2));
    }
}
