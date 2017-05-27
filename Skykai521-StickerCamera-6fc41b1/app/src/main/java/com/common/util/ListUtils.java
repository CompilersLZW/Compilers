package com.common.util;

import java.util.ArrayList;
import java.util.List;

import android.text.TextUtils;

/**
 * List Utils
 *
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-7-22
 */
public class ListUtils {

    /**
     * 默认加入头文件当中
     */
    public static final String DEFAULT_JOIN_SEPARATOR = ",";

    private ListUtils() {
        throw new AssertionError();
    }

    /**
     * get size of list
     * 得到list的大小
     *
     * <pre>
     * getSize函数（参数是null）(null)   =   默认值是0;
     * getSize函数（不含参数）({})     =   默认值0;
     * getSize函数（整数1参数）({1})    =   默认值1;
     * </pre>
     *
     * @param <V>
     * @param sourceList
     * @return if list is null or empty, return 0, else return {@link List#size()}.
     * 如果默认值是null或者empty,返回0 或者返回list的大小
     */
    public static <V> int getSize(List<V> sourceList) {
        return sourceList == null ? 0 : sourceList.size();
    }

    /**
     * 判断是否为空函数：
     *
     * <pre>
     * isEmpty函数（参数为null）(null)   =   true;
     * isEmpty函数（从那数为空）({})     =   true;
     * isEmpty函数（参数是1）({1})    =   false;
     * </pre>
     *
     * @param <V>
     * @param sourceList
     * 如果list是null或者他的size是0，返回true,否则返回false
     */
    public static <V> boolean isEmpty(List<V> sourceList) {
        return (sourceList == null || sourceList.size() == 0);
    }

    /**
     * 判断两个list是否相等
     *
     * <pre>
     * isEquals(null, null) = true;如果两个函数都是null，返回true
     * isEquals(new ArrayList&lt;String&gt;(), null) = false;一个list有值，一个list是空，返回false。
     * isEquals(null, new ArrayList&lt;String&gt;()) = false;一个list是null,一个list有值，返回false
     * isEquals(new ArrayList&lt;String&gt;(), new ArrayList&lt;String&gt;()) = true;两个list相等，返回true
     * </pre>
     *
     * @param <V>
     * @param actual
     * @param expected
     * @return
     */
    public static <V> boolean isEquals(ArrayList<V> actual, ArrayList<V> expected) {
        if (actual == null) {
            return expected == null;
        }
        if (expected == null) {
            return false;
        }
        if (actual.size() != expected.size()) {
            return false;
        }

        for (int i = 0; i < actual.size(); i++) {
            if (!ObjectUtils.isEquals(actual.get(i), expected.get(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * join list to string, separator is ","
     *
     * <pre>
     * join连接值（属性是null）(null)      =   "";
     * join连接值（参数空）({})        =   "";
     * join连接值（参数a,b）({a,b})     =   "a,b";
     * </pre>
     *
     * @param list
     * @return join list to string, separator is ",". if list is empty, return ""
     */
    public static String join(List<String> list) {
        return join(list, DEFAULT_JOIN_SEPARATOR);
    }

    /**
     * join list to string
     *
     * <pre>
     * join(null, '#')     =   "";连接null和# 输出“”
     * join({}, '#')       =   "";连接｛｝和# 输出“”
     * join({a,b,c}, ' ')  =   "abc";连接abc和空，输出abc
     * join({a,b,c}, '#')  =   "a#b#c";连接abc和#,输出a#b#c#
     * </pre>
     *
     * @param list
     * @param separator
     * @return join list to string. if list is empty, return ""
     */
    public static String join(List<String> list, char separator) {
        return join(list, new String(new char[] {separator}));
    }

    /**
     * join list to string. if separator is null, use {@link #DEFAULT_JOIN_SEPARATOR}
     *
     * <pre>
     * join(null, "#")     =   "";连接null和#,输出空。
     * join({}, "#$")      =   "";连接空和#$，输出空
     * join({a,b,c}, null) =   "a,b,c";连接a,b,c和null输出a,b,c
     * join({a,b,c}, "")   =   "abc";连接a,b,c和空，输出abc
     * join({a,b,c}, "#")  =   "a#b#c";连接abc和#输出a#b#c
     * join({a,b,c}, "#$") =   "a#$b#$c";连接a,b,c和#$输出a#$b#$c
     * </pre>
     *
     * @param list
     * @param separator
     * @return join list to string with separator. if list is empty, return ""
     */
    public static String join(List<String> list, String separator) {
        return list == null ? "" : TextUtils.join(separator, list);
    }

    /**
     * add distinct entry to list
     *
     * @param <V>
     * @param sourceList
     * @param entry
     * @return if entry already exist in sourceList, return false, else add it and return true.
     */
    public static <V> boolean addDistinctEntry(List<V> sourceList, V entry) {
        return (sourceList != null && !sourceList.contains(entry)) ? sourceList.add(entry) : false;
    }

    /**
     * 把所有不同的条目从清单1传到清单2
     *
     * @param <V>
     * @param sourceList
     * @param entryList
     * @return the count of entries be added
     */
    public static <V> int addDistinctList(List<V> sourceList, List<V> entryList) {
        if (sourceList == null || isEmpty(entryList)) {
            return 0;
        }

        int sourceCount = sourceList.size();
        for (V entry : entryList) {
            if (!sourceList.contains(entry)) {
                sourceList.add(entry);
            }
        }
        return sourceList.size() - sourceCount;
    }

    /**
     * 删除列表中的重复条目
     *
     * @param <V>
     * @param sourceList
     * @return the count of entries be removed
     */
    public static <V> int distinctList(List<V> sourceList) {
        if (isEmpty(sourceList)) {
            return 0;
        }

        int sourceCount = sourceList.size();
        int sourceListSize = sourceList.size();
        for (int i = 0; i < sourceListSize; i++) {
            for (int j = (i + 1); j < sourceListSize; j++) {
                if (sourceList.get(i).equals(sourceList.get(j))) {
                    sourceList.remove(j);
                    sourceListSize = sourceList.size();
                    j--;
                }
            }
        }
        return sourceCount - sourceList.size();
    }

    /**
     * 添加不空条目列表
     *
     * @param sourceList
     * @param value
     * @return <ul>
     *         <li>if sourceList is null, return false</li>
     *         <li>if value is null, return false</li>
     *         <li>return {@link List#add(Object)}</li>
     *         </ul>
     */
    public static <V> boolean addListNotNullValue(List<V> sourceList, V value) {
        return (sourceList != null && value != null) ? sourceList.add(value) : false;
    }

    /**
     * @see {@link ArrayUtils#getLast(Object[], Object, Object, boolean)} 默认值是null isCircle 是true
     */
    @SuppressWarnings("unchecked")
    public static <V> V getLast(List<V> sourceList, V value) {
        return (sourceList == null) ? null : (V)ArrayUtils.getLast(sourceList.toArray(), value, true);
    }

    /**
     * @see {@link ArrayUtils#getNext(Object[], Object, Object, boolean)} 默认值是null, isCircle 是 true
     */
    @SuppressWarnings("unchecked")
    public static <V> V getNext(List<V> sourceList, V value) {
        return (sourceList == null) ? null : (V)ArrayUtils.getNext(sourceList.toArray(), value, true);
    }

    /**
     * invert list
     *
     * @param <V>
     * @param sourceList
     * @return
     */
    public static <V> List<V> invertList(List<V> sourceList) {
        if (isEmpty(sourceList)) {
            return sourceList;
        }

        List<V> invertList = new ArrayList<V>(sourceList.size());
        for (int i = sourceList.size() - 1; i >= 0; i--) {
            invertList.add(sourceList.get(i));
        }
        return invertList;
    }
}
