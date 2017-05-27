package com.common.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Map Utils
 * 
 * @author <a href="http://www.trinea.cn" target="_blank">Trinea</a> 2011-7-22
 */
public class MapUtils {

    /**
     * 设置一个值和建分离的标志DEFAULT_KEY_AND_VALUE_SEPARATOR 为“：”
     */
    public static final String DEFAULT_KEY_AND_VALUE_SEPARATOR      = ":";
    /**
     * 设置一个两个键值之间区分的字符 DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR = “，”
     */
    public static final String DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR = ",";
    
    private MapUtils() {
        throw new AssertionError();
    }
    /**
     * is null or its size is 0
     * <pre>
     * isEmpty(null)   =   true;
     * isEmpty({})     =   true;
     * isEmpty({1, 2})    =   false;
     * </pre>
     * 
     * @param sourceMap
     * @return if map is null or its size is 0, return true, else return false.
     */
    /**
     *声明isEmpty函数
     * 返回函数不存在或者map为空
     */
    public static <K, V> boolean isEmpty(Map<K, V> sourceMap) {
        return (sourceMap == null || sourceMap.size() == 0);
    }

    /**
     * add key-value pair to map, and key need not null or empty
     * 在增加键值时，键值必须不为空
     * @param map
     * @param key
     * @param value
     * @return <ul>
     *         <li>if map is null, return false</li> 如果map不存在则返回false
     *         <li>if key is null or empty, return false</li>如果键不存在或者为空则返回false
     *         <li>return {@link Map#put(Object, Object)}</li>
     *         </ul>
     */
    public static boolean putMapNotEmptyKey(Map<String, String> map, String key, String value) {
        if (map == null || StringUtils.isEmpty(key)) {
            return false;
        }

        map.put(key, value);
        return true;
    }

    /**
     * add key-value pair to map, both key and value need not null or empty
     * 在增加map数据时map的键和数值都必须不为空
     * @param map
     * @param key
     * @param value
     * @return <ul>
     *         <li>if map is null, return false</li>如果map不存在则返回false
     *         <li>if key is null or empty, return false</li>如果键为空或者不存在返回false
     *         <li>if value is null or empty, return false</li>如果值不存在或者为空则返回false
     *         <li>return {@link Map#put(Object, Object)}</li>
     *         </ul>
     */
    public static boolean putMapNotEmptyKeyAndValue(Map<String, String> map, String key, String value) {
        if (map == null || StringUtils.isEmpty(key) || StringUtils.isEmpty(value)) {
            return false;
        }

        map.put(key, value);
        return true;
    }

    /**
     * add key-value pair to map, key need not null or empty
     * 在增加键和值时键必须不为空
     * @param map
     * @param key
     * @param value
     * @param defaultValue
     * @return <ul>
     *         <li>if map is null, return false</li>如果map不存在则返回false
     *         <li>if key is null or empty, return false</li>如果键不存在或者为空则返回false
     *         <li>if value is null or empty, put defaultValue, return true</li>如果值不存在或者为空放入违规值返回true
     *         <li>if value is neither null nor empty，put value, return true</li>如果值不为空则加入值返回true
     *         </ul>
     */
    public static boolean putMapNotEmptyKeyAndValue(Map<String, String> map, String key, String value,
            String defaultValue) {
        if (map == null || StringUtils.isEmpty(key)) {
            return false;
        }

        map.put(key, StringUtils.isEmpty(value) ? defaultValue : value);
        return true;
    }

    /**
     * add key-value pair to map, key need not null
     * 在增加键和值时，键必须存在
     * @param map
     * @param key
     * @param value
     * @return <ul>
     *         <li>if map is null, return false</li>如果map不存在则返回false
     *         <li>if key is null, return false</li>如果不存在则返回false
     *         <li>return {@link Map#put(Object, Object)}</li>
     *         </ul>
     */
    public static <K, V> boolean putMapNotNullKey(Map<K, V> map, K key, V value) {
        if (map == null || key == null) {
            return false;
        }

        map.put(key, value);
        return true;
    }

    /**
     * add key-value pair to map, both key and value need not null
     * 在增加键和值时建和值必须不为null
     * @param map
     * @param key
     * @param value
     * @return <ul>
     *         <li>if map is null, return false</li>如果map不存在则返回false
     *         <li>if key is null, return false</li>如果key不存在则返回false
     *         <li>if value is null, return false</li>如果值不存在则返回false
     *         <li>return {@link Map#put(Object, Object)}</li>
     *         </ul>
     */
    public static <K, V> boolean putMapNotNullKeyAndValue(Map<K, V> map, K key, V value) {
        if (map == null || key == null || value == null) {
            return false;
        }

        map.put(key, value);
        return true;
    }

    /**
     * get key by value, match the first entry front to back
     * 在查找找到第一个比配的值
     * <ul>
     * <strong>Attentions:</strong>
     * <li>for HashMap, the order of entry not same to put order, so you may need to use TreeMap</li>、
     *哈希表的循序和存入哈希表的顺序不同，所以你可以使用TreeMap
     * </ul>
     * 
     * @param <V>
     * @param map
     * @param value
     * @return <ul>
     *         <li>if map is null, return null</li>如果map为null则返回null
     *         <li>if value exist, return key</li>如果值为null则返回键值
     *         <li>return null</li>否则返回null
     *         </ul>
     */
    public static <K, V> K getKeyByValue(Map<K, V> map, V value) {
        if (isEmpty(map)) {
            return null;
        }

        for (Entry<K, V> entry : map.entrySet()) {
            if (ObjectUtils.isEquals(entry.getValue(), value)) {
                return entry.getKey();
            }
        }
        return null;
    }

    /**
     * parse key-value pairs to map, ignore empty key
     * 解析键值对忽略空的键
     * <pre>
     * parseKeyAndValueToMap("","","",true)=null
     * parseKeyAndValueToMap(null,"","",true)=null
     * parseKeyAndValueToMap("a:b,:","","",true)={(a,b)}
     * parseKeyAndValueToMap("a:b,:d","","",true)={(a,b)}
     * parseKeyAndValueToMap("a:b,c:d","","",true)={(a,b),(c,d)}
     * parseKeyAndValueToMap("a=b, c = d","=",",",true)={(a,b),(c,d)}
     * parseKeyAndValueToMap("a=b, c = d","=",",",false)={(a, b),( c , d)}
     * parseKeyAndValueToMap("a=b, c=d","=", ",", false)={(a,b),( c,d)}
     * parseKeyAndValueToMap("a=b; c=d","=", ";", false)={(a,b),( c,d)}
     * parseKeyAndValueToMap("a=b, c=d", ",", ";", false)={(a=b, c=d)}
     * </pre>
     * 
     * @param source  键值对
     * @param keyAndValueSeparator 分离建和值的符号
     * @param keyAndValuePairSeparator  分离键值对的符号
     * @param ignoreSpace 是否要忽略开始的空格和最后一个建和值
     * @return
     */
    public static Map<String, String> parseKeyAndValueToMap(String source, String keyAndValueSeparator,
            String keyAndValuePairSeparator, boolean ignoreSpace) {
        if (StringUtils.isEmpty(source)) {
            return null;
        }

        if (StringUtils.isEmpty(keyAndValueSeparator)) {
            keyAndValueSeparator = DEFAULT_KEY_AND_VALUE_SEPARATOR;
        }
        if (StringUtils.isEmpty(keyAndValuePairSeparator)) {
            keyAndValuePairSeparator = DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR;
        }
        Map<String, String> keyAndValueMap = new HashMap<String, String>();
        String[] keyAndValueArray = source.split(keyAndValuePairSeparator);
        if (keyAndValueArray == null) {
            return null;
        }

        int seperator;
        for (String valueEntity : keyAndValueArray) {
            if (!StringUtils.isEmpty(valueEntity)) {
                seperator = valueEntity.indexOf(keyAndValueSeparator);
                if (seperator != -1) {
                    if (ignoreSpace) {
                        MapUtils.putMapNotEmptyKey(keyAndValueMap, valueEntity.substring(0, seperator).trim(),
                                valueEntity.substring(seperator + 1).trim());
                    } else {
                        MapUtils.putMapNotEmptyKey(keyAndValueMap, valueEntity.substring(0, seperator),
                                valueEntity.substring(seperator + 1));
                    }
                }
            }
        }
        return keyAndValueMap;
    }

    /**
     * parse key-value pairs to map, ignore empty key
     *
     * 解析键值对存入map，忽略空的键
     * @param source 键值对字符串
     * @param ignoreSpace 是否要忽略开始的空格和最后一个建和值
     * @return
     * @see {@link MapUtils#parseKeyAndValueToMap(String, String, String, boolean)}, keyAndValueSeparator is
     *      {@link #DEFAULT_KEY_AND_VALUE_SEPARATOR}, keyAndValuePairSeparator is
     *      {@link #DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR}
     */
    public static Map<String, String> parseKeyAndValueToMap(String source, boolean ignoreSpace) {
        return parseKeyAndValueToMap(source, DEFAULT_KEY_AND_VALUE_SEPARATOR, DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR,
                ignoreSpace);
    }

    /**
     * parse key-value pairs to map, ignore empty key, ignore space at the begging or end of key and value
     * 解析键值对字符串存入map中，忽略空的键和开始的空格和最后的建和值
     * @param source key-value pairs
     * @return
     * @see {@link MapUtils#parseKeyAndValueToMap(String, String, String, boolean)}, keyAndValueSeparator is
     *      {@link #DEFAULT_KEY_AND_VALUE_SEPARATOR}, keyAndValuePairSeparator is
     *      {@link #DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR}, ignoreSpace is true
     */
    public static Map<String, String> parseKeyAndValueToMap(String source) {
        return parseKeyAndValueToMap(source, DEFAULT_KEY_AND_VALUE_SEPARATOR, DEFAULT_KEY_AND_VALUE_PAIR_SEPARATOR,
                true);
    }

    /**
     * join map
     * 
     * @param map
     * @return
     */
    public static String toJson(Map<String, String> map) {
        if (map == null || map.size() == 0) {
            return null;
        }

        StringBuilder paras = new StringBuilder();
        paras.append("{");
        Iterator<Entry<String, String>> ite = map.entrySet().iterator();
        while (ite.hasNext()) {
            Entry<String, String> entry = (Entry<String, String>)ite.next();
            paras.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            if (ite.hasNext()) {
                paras.append(",");
            }
        }
        paras.append("}");
        return paras.toString();
    }
}
