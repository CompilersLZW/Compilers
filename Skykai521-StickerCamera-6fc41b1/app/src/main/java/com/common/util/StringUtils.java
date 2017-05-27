package com.common.util;

/*
 * Licensed to the Apache Software Foundation (ASF) under one or more contributor license agreements. See the NOTICE
 * file distributed with this work for additional information regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with the
 * License. You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0 Unless required by
 * applicable law or agreed to in writing, software distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language
 * governing permissions and limitations under the License.
 */

import java.util.Arrays;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * <p>
 * Common <code>String</code> manipulation routines.
 * </p>
 * <p/>
 * <p>
 * Originally from <a href="http://jakarta.apache.org/turbine/">Turbine</a>, the GenerationJavaCore library and
 * Velocity. Later a lots methods from commons-lang StringUtils got added too. Gradually smaller additions and fixes
 * have been made over the time by various ASF committers.
 * </p>
 */
public class StringUtils {

    /**
     * <p>
     * <code>StringUtils</code> instances should NOT be constructed in standard programming. Instead, the class should
     * be used as <code>StringUtils.trim(" foo ");</code>.
     * This constructor is public to permit tools that require a JavaBean manager to operate.
     * </p>
     */
    public StringUtils() {
    }

    // Empty
    // --------------------------------------------------------------------------


    public static String clean(String str) {
        return (str == null ? "" : str.trim());
    }

    /**
     * <p>
     *   去除控制的字符，如空格
     * returning 当字符串为空时返回“”，否则返回处理过的值
     * </p>
     *
     * @param str 输入的需要处理的字符串
     * @return the trimmed text (or <code>null</code>)
     * @see java.lang.String#trim()
     */
    public static String trim(String str) {
        return (str == null ? null : str.trim());
    }

    /**
     * <p>
     * 删除字符串中的空格
     * Whitespace is defined by {@link Character#isWhitespace(char)}.
     * </p>
     * @param str 输入的需要删除空格的字符串
     * @return 不含空格的字符串
     * @throws NullPointerException
     */
    public static String deleteWhitespace(String str) {
        StringBuilder buffer = new StringBuilder();
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                buffer.append(str.charAt(i));
            }
        }
        return buffer.toString();
    }

    /**
     * 判断如果字符串是否存在
     *
     * @param str 输入的需要修改的字符串
     * @return true 如果字符串不为空则返回true
     */
    public static boolean isNotEmpty(String str) {
        return ((str != null) && (str.trim().length() > 0));
    }

    /**
     * <p>
     * Checks if a (trimmed) String is <code>null</code> or empty.
     * 判断字符串是否为空
     * <strong>Note:</strong> In future releases, this method will no longer trim the input string such that it works
     * complementary to {@link #isNotEmpty(String)}. Code that wants to test for whitespace-only strings should be
     * migrated to use {@link #isBlank(String)} instead.
     * </p>
     *
     * @param str 输入的需要判断的字符串
     * @return <code>true</code> if the String is <code>null</code>, or length zero once trimmed
     * 如果字符串为空或者长度为0则返回true
     */
    public static boolean isEmpty(String str) {
        return ((str == null) || (str.trim().length() == 0));
    }

    /**
     * Checks if a String is whitespace, empty ("") or null.
     * 检查字符串中的空格
     * <pre>
     * StringUtils.isBlank(null)      = true
     * StringUtils.isBlank("")        = true
     * StringUtils.isBlank(" ")       = true
     * StringUtils.isBlank("bob")     = false
     * StringUtils.isBlank("  bob  ") = false
     * </pre>
     *
     * @param str 输入的需要检查的字符串
     * @return <code>true</code> 如果字符串为空或者存在空格则返回true
     * @since 1.5.2
     */
    public static boolean isBlank(String str) {
        int strLen;
        if (str == null || (strLen = str.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 返回字符串的淄川
     * @param str 输入的需要处理的字符串
     * @param separator  子串
     * @return
     */
    public static String substringBefore(String str, String separator) {
        if ((isEmpty(str)) || (separator == null)) {
            return str;
        }
        if (separator.length() == 0) {
            return "";
        }
        int pos = str.indexOf(separator);
        if (pos == -1) {
            return str;
        }
        return str.substring(0, pos);
    }

    /**
     * <p>
     * Checks if a String is not empty (""), not null and not whitespace only.
     * 判断字符串不为空且不含空格
     * <pre>
     * StringUtils.isNotBlank(null)      = false
     * StringUtils.isNotBlank("")        = false
     * StringUtils.isNotBlank(" ")       = false
     * StringUtils.isNotBlank("bob")     = true
     * StringUtils.isNotBlank("  bob  ") = true
     * </pre>
     *
     * @param str 输入的需要判断的字符串，可以为空
     * @return <code>true</code> 如果字符串不为空且字符串不含空格
     * @since 1.5.2
     */
    public static boolean isNotBlank(String str) {
        return !StringUtils.isBlank(str);
    }

    // Equals and IndexOf
    // --------------------------------------------------------------------------

    /**
     * <p>
     * Compares two Strings, returning <code>true</code> if they are equal.
     * 判断两个字符串，判断他们是否相等
     * <code>null</code>s are handled without exceptions. Two <code>null</code> references are considered to be equal.
     * The comparison is case sensitive.
     * @param str1 输入的第一个字符串
     * @param str2 输入的第二个字符串
     * @return <code>true</code> if the Strings are equal, case sensitive, or both <code>null</code>、
     * 如果两个字符串相等或者都为空则返回true
     * @see java.lang.String#equals(Object)
     */
    public static boolean equals(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equals(str2));
    }

    /**
     * <p>
     * Compares two Strings, returning <code>true</code> if they are equal ignoring the case.
     * 判断两个字符串是否相等
     * <p>
     * <code>Nulls</code> are handled without exceptions. Two <code>null</code> references are considered equal.
     * Comparison is case insensitive.
     * </p>
     *
     * @param str1 输入的第一个字符串
     * @param str2 输入的第二个字符串
     * @return <code>true</code> if the Strings are equal, case insensitive, or both <code>null</code>
     * 如果两个字符串相等或者都为空则返回true（不明感，即忽略特殊情况）
     * @see java.lang.String#equalsIgnoreCase(String)
     */
    public static boolean equalsIgnoreCase(String str1, String str2) {
        return (str1 == null ? str2 == null : str1.equalsIgnoreCase(str2));
    }

    /**
     * <p>
     * Find the first index of any of a set of potential substrings.
     * 找到第一个子串
     * </p>
     * <p/>
     * <p>
     * <code>null</code> String will return <code>-1</code>.
     * 如果不存在子串则返回 -1
     * </p>
     *
     * @param str 输入的需要检查的字符串
     * @param searchStrs 输入的子串
     * @return the first index of any of the searchStrs in str
     * 返回找到的第一个子串的首字母位置
     * @throws NullPointerException if any of searchStrs[i] is <code>null</code>
     */
    public static int indexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;

        // String's can't have a MAX_VALUEth index.
        int ret = Integer.MAX_VALUE;

        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            tmp = str.indexOf(searchStrs[i]);
            if (tmp == -1) {
                continue;
            }

            if (tmp < ret) {
                ret = tmp;
            }
        }

        return (ret == Integer.MAX_VALUE) ? -1 : ret;
    }

    /**
     * <p>
     * Find the latest index of any of a set of potential substrings.
     * 找到最后一个子串的位置
     * <code>null</code> string will return <code>-1</code>.
     * 如果不存在则返回-1
     * </p>
     * @param str 输入的需要处理的字符串
     * @param searchStrs 子串
     * @return the last index of any of the Strings
     * 返回查找到的最后一个子串
     * @throws NullPointerException if any of searchStrs[i] is <code>null</code>
     */
    public static int lastIndexOfAny(String str, String[] searchStrs) {
        if ((str == null) || (searchStrs == null)) {
            return -1;
        }
        int sz = searchStrs.length;
        int ret = -1;
        int tmp = 0;
        for (int i = 0; i < sz; i++) {
            tmp = str.lastIndexOf(searchStrs[i]);
            if (tmp > ret) {
                ret = tmp;
            }
        }
        return ret;
    }

    // Substring
    // --------------------------------------------------------------------------

    /**
     * Gets a substring from the specified string avoiding exceptions.
     * 从一个字符串中获得子串
     * A negative start position can be used to start <code>n</code> characters from the end of the String.
     * </p>
     *
     * @param str 输入需要取出子串的字符串
     * @param start 输入子串开始的位置
     * characters
     * @return substring from start position
     * 返回从开始位置到字符串结束的子串
     */
    public static String substring(String str, int start) {
        if (str == null) {
            return null;
        }

        // handle negatives, which means last n characters
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        if (start < 0) {
            start = 0;
        }
        if (start > str.length()) {
            return "";
        }

        return str.substring(start);
    }

   //一个消极的位置被定义使用
    public static String substring(String str, int start, int end) {
        if (str == null) {
            return null;
        }

        // handle negatives处理消极位置
        if (end < 0) {
            end = str.length() + end; // remember end is negative
        }
        if (start < 0) {
            start = str.length() + start; // remember start is negative
        }

        // check length next
        if (end > str.length()) {
            // check this works.
            end = str.length();
        }

        // if start is greater than end, return ""
        if (start > end) {
            return "";
        }

        if (start < 0) {
            start = 0;
        }
        if (end < 0) {
            end = 0;
        }

        return str.substring(start, end);
    }

    /**
     * Gets the leftmost <code>n</code> characters of a String.
     * 得到最左边的值，如果他不可达，或者字符串为空，这个字符串返回空
     * If <code>n</code> characters are not available, or the String is <code>null</code>, the String will be returned
     * without an exception.
     * @param str the String to get the leftmost characters from
     * @param len the length of the required String
     * @return the leftmost characters
     * @throws IllegalArgumentException if len is less than zero
     */
    public static String left(String str, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len
                    + " is less than zero");
        }
        if ((str == null) || (str.length() <= len)) {
            return str;
        } else {
            return str.substring(0, len);
        }
    }

    /**
     * <p>
     * Gets the rightmost <code>n</code> characters of a String.
     * 得到最右边的值，如果他不可达，或者字符串为空，这个字符串返回空
     * If <code>n</code> characters are not available, or the String is <code>null</code>, the String will be returned
     * without an exception.
     * </p>
     * @param str the String to get the rightmost characters from
     * @param len the length of the required String
     * @return the leftmost characters
     * @throws IllegalArgumentException if len is less than zero
     */
    public static String right(String str, int len) {
        if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len
                    + " is less than zero");
        }
        if ((str == null) || (str.length() <= len)) {
            return str;
        } else {
            return str.substring(str.length() - len);
        }
    }

    /**
     * <p>
     * Gets <code>n</code> characters from the middle of a String.
     * 得到最中间的值，如果他不可达，或者字符串为空，这个字符串返回空
     * If <code>n</code> characters are not available, the remainder of the String will be returned without an
     * exception. If the String is <code>null</code>, <code>null</code> will be returned.
     * @param str the String to get the characters from
     * @param pos the position to start from
     * @param len the length of the required String
     * @return the leftmost characters
     * @throws IndexOutOfBoundsException if pos is out of bounds
     * @throws IllegalArgumentException if len is less than zero
     */
    public static String mid(String str, int pos, int len) {
        if ((pos < 0) || ((str != null) && (pos > str.length()))) {
            throw new StringIndexOutOfBoundsException("String index " + pos + " is out of bounds");
        }
        if (len < 0) {
            throw new IllegalArgumentException("Requested String length " + len
                    + " is less than zero");
        }
        if (str == null) {
            return null;
        }
        if (str.length() <= (pos + len)) {
            return str.substring(pos);
        } else {
            return str.substring(pos, pos + len);
        }
    }

    // Splitting
    // --------------------------------------------------------------------------

    /**
     * <p>
     * Splits the provided text into a array, using whitespace as the separator.
     * 抛出数组里面提供的空文本
     * The separator is not included in the returned String array.@param str the String to parse
     * @return an array of parsed Strings
     */
    public static String[] split(String str) {
        return split(str, null, -1);
    }

    /**
     * @see #split(String, String, int)
     */
    public static String[] split(String text, String separator) {
        return split(text, separator, -1);
    }

    /**
     * <p>
     * Splits the provided text into a array, based on a given separator.
     * The separator is not included in the returned String array. The maximum number of splits to perfom can be
     * controlled. A <code>null</code> separator will cause parsing to be on whitespace.
     * * 抛出数组里面提供的空文本
     * This is useful for quickly splitting a String directly into an array of tokens, instead of an enumeration of
     * tokens (as <code>StringTokenizer</code> does).
     * @param str The string to parse.
     * @param separator Characters used as the delimiters. If <code>null</code>, splits on whitespace.
     * @param max The maximum number of elements to parse. The rest of the string to parse will be contained in the last
     * array element. A zero or negative value implies no limit.
     * @return an array of parsed Strings
     */
    //定义抛出函数
    public static String[] split(String str, String separator, int max) {
        StringTokenizer tok = null;
        if (separator == null) {
            // Null separator means we're using StringTokenizer's default
            // delimiter, which comprises all whitespace characters.
            tok = new StringTokenizer(str);
        } else {
            tok = new StringTokenizer(str, separator);
        }

        int listSize = tok.countTokens();
        if ((max > 0) && (listSize > max)) {
            listSize = max;
        }

        String[] list = new String[listSize];
        int i = 0;
        int lastTokenBegin = 0;
        int lastTokenEnd = 0;
        while (tok.hasMoreTokens()) {
            if ((max > 0) && (i == listSize - 1)) {
                // In the situation where we hit the max yet have
                // tokens left over in our input, the last list
                // element gets all remaining text.
                String endToken = tok.nextToken();
                lastTokenBegin = str.indexOf(endToken, lastTokenEnd);
                list[i] = str.substring(lastTokenBegin);
                break;
            } else {
                list[i] = tok.nextToken();
                lastTokenBegin = str.indexOf(list[i], lastTokenEnd);
                lastTokenEnd = lastTokenBegin + list[i].length();
            }
            i++;
        }
        return list;
    }

    // Joining
    // --------------------------------------------------------------------------

    /**
     * <p>
     * Concatenates elements of an array into a single String.
     * 分隔符被使用在这里
     * @param array the array of values to concatenate.
     * @return the concatenated string.
     */
    public static String concatenate(Object[] array) {
        return join(array, "");
    }

    /**
     * <p>
     * Joins the elements of the provided array into a single String containing the provided list of elements.
     *添加元素放在提供的数组进一个单独的变量
     * No delimiter is added before or after the list. A <code>null</code> separator is the same as a blank String.
     * @param array the array of values to join together
     * @param separator the separator character to use
     * @return the joined String
     */
    public static String join(Object[] array, String separator) {
        if (separator == null) {
            separator = "";
        }
        int arraySize = array.length;
        int bufSize = (arraySize == 0 ? 0 : (array[0].toString().length() + separator.length())
                * arraySize);
        StringBuilder buf = new StringBuilder(bufSize);

        for (int i = 0; i < arraySize; i++) {
            if (i > 0) {
                buf.append(separator);
            }
            buf.append(array[i]);
        }
        return buf.toString();
    }

    /**
     * <p>
     * Joins the elements of the provided <code>Iterator</code> into a single String containing the provided elements.
     * 添加元素放在提供的接口进一个单独的变量
     * No delimiter is added before or after the list. A <code>null</code> separator is the same as a blank String.
     * @param iterator the <code>Iterator</code> of values to join together
     * @param separator the separator character to use
     * @return the joined String
     */
    //定义join函数
    @SuppressWarnings("rawtypes")
    public static String join(Iterator iterator, String separator) {
        if (separator == null) {
            separator = "";
        }
        StringBuilder buf = new StringBuilder(256); // Java default is 16, probably too small
        while (iterator.hasNext()) {
            buf.append(iterator.next());
            if (iterator.hasNext()) {
                buf.append(separator);
            }
        }
        return buf.toString();
    }

    // Replacing
    // --------------------------------------------------------------------------

    /**
     * <p>
     * Replace a char with another char inside a larger String, once.
     * 在一个长字符串里，用其中一个字符串取代另一个字符串
     * A <code>null</code> reference passed to this method is a no-op.
     * @param text text to search and replace in
     * @param repl char to search for
     * @param with char to replace with
     * @return the text with any replacements processed
     * @see #replace(String text, char repl, char with, int max)
     */
    public static String replaceOnce(String text, char repl, char with) {
        return replace(text, repl, with, 1);
    }

    /**
     * <p>
     * Replace all occurances of a char within another char.
     *取代所有的字符创出现的场合用另一个字符
     * A <code>null</code> reference passed to this method is a no-op.
     * @param text text to search and replace in
     * @param repl char to search for
     * @param with char to replace with
     * @return the text with any replacements processed
     * @see #replace(String text, char repl, char with, int max)
     */
    public static String replace(String text, char repl, char with) {
        return replace(text, repl, with, -1);
    }

    /**
     * <p>
     * Replace a char with another char inside a larger String, for the first <code>max</code> values of the search
     * char.在一个长字符串里，用其中一个字符串取代另一个字符串，第一个搜索值
     * A <code>null</code> reference passed to this method is a no-op.
     * @param text text to search and replace in
     * @param repl char to search for
     * @param with char to replace with
     * @param max maximum number of values to replace, or &lt;=0 if no maximum
     * @return the text with any replacements processed
     */
    public static String replace(String text, char repl, char with, int max) {
        return replace(text, String.valueOf(repl), String.valueOf(with), max);
    }

    /**
     * <p>
     * Replace a String with another String inside a larger String, once.
     * 在一个长字符串里，用其中一个字符串取代另一个字符串
     * A <code>null</code> reference passed to this method is a no-op.
     * @param text text to search and replace in
     * @param repl String to search for
     * @param with String to replace with
     * @return the text with any replacements processed
     * @see #replace(String text, String repl, String with, int max)
     */
    public static String replaceOnce(String text, String repl, String with) {
        return replace(text, repl, with, 1);
    }

    /**
     * <p>
     * Replace all occurances of a String within another String.
     * 取代所有的字符创出现的场合用另一个字符
     * A <code>null</code> reference passed to this method is a no-op.
     * @param text text to search and replace in
     * @param repl String to search for
     * @param with String to replace with
     * @return the text with any replacements processed
     * @see #replace(String text, String repl, String with, int max)
     */
    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    /**
     * <p>
     * Replace a String with another String inside a larger String, for the first <code>max</code> values of the search
     * String. 在一个长字符串里，用其中一个字符串取代另一个字符串，第一个搜索值
     * A <code>null</code> reference passed to this method is a no-op.
     * @param text text to search and replace in
     * @param repl String to search for
     * @param with String to replace with
     * @param max maximum number of values to replace, or &lt;=0 if no maximum
     * @return the text with any replacements processed
     */
    public static String replace(String text, String repl, String with, int max) {
        if ((text == null) || (repl == null) || (with == null) || (repl.length() == 0)) {
            return text;
        }

        StringBuilder buf = new StringBuilder(text.length());
        int start = 0, end = 0;
        while ((end = text.indexOf(repl, start)) != -1) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();

            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    /**
     * Overlay a part of a String with another String.
     * @param text String to do overlaying in
     * @param overlay String to overlay
     * @param start int to start overlaying at
     * @param end int to stop overlaying before
     * @return String with overlayed text
     * @throws NullPointerException if text or overlay is <code>null</code>
     */
    public static String overlayString(String text, String overlay, int start, int end) {
        return new StringBuilder(start + overlay.length() + text.length() - end + 1)
                .append(text.substring(0, start)).append(overlay).append(text.substring(end))
                .toString();
    }

    // Centering
    // --------------------------------------------------------------------------

    /**
     * <p>
     * Center a String in a larger String of size <code>n</code>.
     *最长字符型的中心，使用空间值作为缓冲
     * Uses spaces as the value to buffer the String with. Equivalent to <code>center(str, size, " ")</code>.
     * @param str String to center
     * @param size int size of new String
     * @return String containing centered String
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String center(String str, int size) {
        return center(str, size, " ");
    }

    /**
     * Center a String in a larger String of size <code>n</code>.
     * 最长字符型的中心，使用空间值作为缓冲
     * Uses a supplied String as the value to buffer the String with.
     * @param str String to center
     * @param size int size of new String
     * @param delim String to buffer the new String with
     * @return String containing centered String
     * @throws NullPointerException if str or delim is <code>null</code>
     * @throws ArithmeticException if delim is the empty String
     */
    public static String center(String str, int size, String delim) {
        int sz = str.length();
        int p = size - sz;
        if (p < 1) {
            return str;
        }
        str = leftPad(str, sz + p / 2, delim);
        str = rightPad(str, size, delim);
        return str;
    }

    // Chomping
    // --------------------------------------------------------------------------

    /**
     * <p>
     * Remove the last newline, and everything after it from a String.
     * 移动最后一个新的线
     * @param str String to chomp the newline from
     * @return String without chomped newline
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String chomp(String str) {
        return chomp(str, "\n");
    }

    /**
     * <p>
     * Remove the last value of a supplied String, and everything after it from a String.
     * 移动提供服务的字符型
     * @param str String to chomp from
     * @param sep String to chomp
     * @return String without chomped ending
     * @throws NullPointerException if str or sep is <code>null</code>
     */
    public static String chomp(String str, String sep) {
        int idx = str.lastIndexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx);
        } else {
            return str;
        }
    }

    /**
     * <p>
     * 当且仅当它是在所提供的字符串的末尾删除换行符。
     * @param str String to chomp from
     * @return String without chomped ending
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String chompLast(String str) {
        return chompLast(str, "\n");
    }

    /**
     * <p>
     * Remove a value if and only if the String ends with that value.
     *当且仅当它是在所提供的字符串的末尾删除换行符。
     * @param str String to chomp from
     * @param sep String to chomp
     * @return String without chomped ending
     * @throws NullPointerException if str or sep is <code>null</code>
     */
    public static String chompLast(String str, String sep) {
        if (str.length() == 0) {
            return str;
        }
        String sub = str.substring(str.length() - sep.length());
        if (sep.equals(sub)) {
            return str.substring(0, str.length() - sep.length());
        } else {
            return str;
        }
    }

    /**
     * <p>
     * Remove everything and return the last value of a supplied String, and everything after it from a String.
     * 当且仅当它是在所提供的每一个字符串删除换行符，并且返回提供的字符串
     * @param str String to chomp from
     * @param sep String to chomp
     * @return String chomped
     * @throws NullPointerException if str or sep is <code>null</code>
     */
    public static String getChomp(String str, String sep) {
        int idx = str.lastIndexOf(sep);
        if (idx == str.length() - sep.length()) {
            return sep;
        } else if (idx != -1) {
            return str.substring(idx);
        } else {
            return "";
        }
    }

    /**
     * <p>
     * Remove the first value of a supplied String, and everything before it from a String.
     * </p>
     *
     * @param str String to chomp from
     * @param sep String to chomp
     * @return String without chomped beginning
     * @throws NullPointerException if str or sep is <code>null</code>
     */
    public static String prechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(idx + sep.length());
        } else {
            return str;
        }
    }

    /**
     * <p>
     * Remove and return everything before the first value of a supplied String from another String.
     * 从另一个字符串中移除提供的字符串的第一个值之前返回。
     * @param str String to chomp from
     * @param sep String to chomp
     * @return String prechomped
     * @throws NullPointerException if str or sep is <code>null</code>
     */
    public static String getPrechomp(String str, String sep) {
        int idx = str.indexOf(sep);
        if (idx != -1) {
            return str.substring(0, idx + sep.length());
        } else {
            return "";
        }
    }

    // Chopping
    // --------------------------------------------------------------------------

    /**
     * <p>
     * Remove the last character from a String.
     * 移动最后一个类型
     * If the String ends in <code>\r\n</code>, then remove both of them.
     * @param str String to chop last character from
     * @return String without last character
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String chop(String str) {
        if ("".equals(str)) {
            return "";
        }
        if (str.length() == 1) {
            return "";
        }
        int lastIdx = str.length() - 1;
        String ret = str.substring(0, lastIdx);
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (ret.charAt(lastIdx - 1) == '\r') {
                return ret.substring(0, lastIdx - 1);
            }
        }
        return ret;
    }

    /**
     * <p>
     * Remove <code>\n</code> from end of a String if it's there. If a <code>\r</code> precedes it, then remove that
     * 如果字符型在这里且是一个precedes，就移动他
     * @param str String to chop a newline from
     * @return String without newline
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String chopNewline(String str) {
        int lastIdx = str.length() - 1;
        char last = str.charAt(lastIdx);
        if (last == '\n') {
            if (str.charAt(lastIdx - 1) == '\r') {
                lastIdx--;
            }
        } else {
            lastIdx++;
        }
        return str.substring(0, lastIdx);
    }

    // Conversion
    // --------------------------------------------------------------------------

    // spec 3.10.6

    /**
     * <p>
     * Escapes any values it finds into their String form.
     * 转义到字符串形式中发现的任何值
     * So a tab becomes the characters <code>'\\'</code> and <code>'t'</code>.
     * @param str String to escape values in
     * @return String with escaped values
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String escape(String str) {
        // improved with code from cybertiger@cyberiantiger.org
        // unicode from him, and defaul for < 32's.
        int sz = str.length();
        StringBuilder buffer = new StringBuilder(2 * sz);
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);

            // handle unicode
            if (ch > 0xfff) {
                buffer.append("\\u" + Integer.toHexString(ch));
            } else if (ch > 0xff) {
                buffer.append("\\u0" + Integer.toHexString(ch));
            } else if (ch > 0x7f) {
                buffer.append("\\u00" + Integer.toHexString(ch));
            } else if (ch < 32) {
                //设置不同类型的缓存类型，为了应用不同的函数
                switch (ch) {
                    case '\b':
                        buffer.append('\\');
                        buffer.append('b');
                        break;
                    case '\n':
                        buffer.append('\\');
                        buffer.append('n');
                        break;
                    case '\t':
                        buffer.append('\\');
                        buffer.append('t');
                        break;
                    case '\f':
                        buffer.append('\\');
                        buffer.append('f');
                        break;
                    case '\r':
                        buffer.append('\\');
                        buffer.append('r');
                        break;
                    default:
                        if (ch > 0xf) {
                            buffer.append("\\u00" + Integer.toHexString(ch));
                        } else {
                            buffer.append("\\u000" + Integer.toHexString(ch));
                        }
                        break;
                }
            } else {
                switch (ch) {
                    case '\'':
                        buffer.append('\\');
                        buffer.append('\'');
                        break;
                    case '"':
                        buffer.append('\\');
                        buffer.append('"');
                        break;
                    case '\\':
                        buffer.append('\\');
                        buffer.append('\\');
                        break;
                    default:
                        buffer.append(ch);
                        break;
                }
            }
        }
        return buffer.toString();
    }

    // Padding
    // --------------------------------------------------------------------------

    /**
     * <p>
     * Repeat a String <code>n</code> times to form a new string.
     *返回字符型表格的时间
     * @param str String to repeat
     * @param repeat number of times to repeat str
     * @return String with repeated String
     * @throws NegativeArraySizeException if <code>repeat < 0</code>
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String repeat(String str, int repeat) {
        StringBuilder buffer = new StringBuilder(repeat * str.length());
        for (int i = 0; i < repeat; i++) {
            buffer.append(str);
        }
        return buffer.toString();
    }

    /**
     * <p>
     * Right pad a String with spaces.
     * 右边空间的内边距
     * The String is padded to the size of <code>n</code>.
     * @param str String to repeat
     * @param size number of times to repeat str
     * @return right padded String
     * @throws NullPointerException if str is <code>null</code>
     */
    public static String rightPad(String str, int size) {
        return rightPad(str, size, " ");
    }

    /**
     * <p>
     * Right pad a String with a specified string.
     * 右边字符型和一个具体的字符型
     * The String is padded to the size of <code>n</code>.
     * </p>
     *
     * @param str 输入需要延长的自出
     * @param size 输入延长的子串
     * @param delim String to pad with
     * @return right padded String
     * @throws NullPointerException if str or delim is <code>null</code>
     * @throws ArithmeticException if delim is the empty String
     */
    public static String rightPad(String str, int size, String delim) {
        size = (size - str.length()) / delim.length();
        if (size > 0) {
            str += repeat(delim, size);
        }
        return str;
    }

    /**
     * <p>
     * Left pad a String with spaces.
     * 在字符串的左边添加空格
     * The String is padded to the size of <code>n</code>.
     * 添加空格的大小为n
     * </p>
     *
     * @param str 输入的需要延长的字符串
     * @param size 延长的大小
     * @return left padded String
     * @throws NullPointerException if str or delim is <code>null</code>
     */
    public static String leftPad(String str, int size) {
        return leftPad(str, size, " ");
    }

    /**
     * Left pad a String with a specified string. Pad to a size of n.
     *在字符串的左边延长一个子串
     * @param str 需要延长的子夫长
     * @param size 需要延长的大小
     * @param delim String to pad with
     * @return left padded String
     * @throws NullPointerException if str or delim is null
     * @throws ArithmeticException if delim is the empty string
     */
    public static String leftPad(String str, int size, String delim) {
        size = (size - str.length()) / delim.length();
        if (size > 0) {
            str = repeat(delim, size) + str;
        }
        return str;
    }

    // Stripping
    // --------------------------------------------------------------------------

    /**
     * Remove whitespace from the front and back of a String.
     *将字符串的首尾的空格删除
     * @param str 需要删除空格的字符串
     * @return the stripped String
     */
    public static String strip(String str) {
        return strip(str, null);
    }

    /**
     * Remove a specified String from the front and back of a String.
     * 从字符串的首尾删除一个具体的子串
     * If whitespace is wanted to be removed, used the {@link #strip(java.lang.String)} method.
     * @param str 输入的需要处理的字符串
     * @param delim 需要删除的子串
     * @return the stripped String
     */
    public static String strip(String str, String delim) {
        str = stripStart(str, delim);
        return stripEnd(str, delim);
    }

    /**
     * Strip whitespace from the front and back of every String in the array.
     *将数组中的字符串都做去除首尾空格操作
     * @param strs the Strings to remove whitespace from
     * @return the stripped Strings
     */
    public static String[] stripAll(String[] strs) {
        return stripAll(strs, null);
    }

    /**
     * Strip the specified delimiter from the front and back of every String in the array.
     *将数组中的字符串去除首尾特定的子串
     * @param strs the Strings to remove a String from
     * @param delimiter the String to remove at start and end
     * @return the stripped Strings
     */
    public static String[] stripAll(String[] strs, String delimiter) {
        if ((strs == null) || (strs.length == 0)) {
            return strs;
        }
        int sz = strs.length;
        String[] newArr = new String[sz];
        for (int i = 0; i < sz; i++) {
            newArr[i] = strip(strs[i], delimiter);
        }
        return newArr;
    }

    /**
     * Strip any of a supplied String from the end of a String.
     *去除字符串末端的子串
     * If the strip String is <code>null</code>, whitespace is stripped.
     * @param str the String to remove characters from
     * @param strip the String to remove
     * @return the stripped String
     */
    public static String stripEnd(String str, String strip) {
        if (str == null) {
            return null;
        }
        int end = str.length();

        if (strip == null) {
            while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
                end--;
            }
        } else {
            while ((end != 0) && (strip.indexOf(str.charAt(end - 1)) != -1)) {
                end--;
            }
        }
        return str.substring(0, end);
    }

    /**
     * Strip any of a supplied String from the start of a String.
     * 从字符串的首部去掉一个特定的子串
     * If the strip String is <code>null</code>, whitespace is stripped.
     * @param str the String to remove characters from
     * @param strip the String to remove
     * @return the stripped String
     */
    public static String stripStart(String str, String strip) {
        if (str == null) {
            return null;
        }

        int start = 0;

        int sz = str.length();

        if (strip == null) {
            while ((start != sz) && Character.isWhitespace(str.charAt(start))) {
                start++;
            }
        } else {
            while ((start != sz) && (strip.indexOf(str.charAt(start)) != -1)) {
                start++;
            }
        }
        return str.substring(start);
    }

    // Case conversion
    // --------------------------------------------------------------------------

    /**
     * Convert a String to upper case, <code>null</code> String returns <code>null</code>.
     *将字符串转换为大写形式
     * @param str the String to uppercase
     * @return the upper cased String
     */
    public static String upperCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toUpperCase();
    }

    /**
     * Convert a String to lower case, <code>null</code> String returns <code>null</code>.
     *将字符串装换为小写形式
     * @param str the string to lowercase
     * @return the lower cased String
     */
    public static String lowerCase(String str) {
        if (str == null) {
            return null;
        }
        return str.toLowerCase();
    }

    /**
     * Uncapitalise a String.
     * That is, convert the first character into lower-case. <code>null</code> is returned as <code>null</code>.
     *将字符串的首位字符转换为小写形式
     * @param str the String to uncapitalise
     * @return uncapitalised String
     */
    public static String uncapitalise(String str) {
        if (str == null) {
            return null;
        } else if (str.length() == 0) {
            return "";
        } else {
            return new StringBuilder(str.length()).append(Character.toLowerCase(str.charAt(0)))
                    .append(str.substring(1)).toString();
        }
    }

    /**
     * Capitalise a String.
     * That is, convert the first character into title-case. <code>null</code> is returned as <code>null</code>.
     *将字符串的首位字符转换为大标题形式
     * @param str the String to capitalise
     * @return capitalised String
     */
    public static String capitalise(String str) {
        if (str == null) {
            return null;
        } else if (str.length() == 0) {
            return "";
        } else {
            return new StringBuilder(str.length()).append(Character.toTitleCase(str.charAt(0)))
                    .append(str.substring(1)).toString();
        }
    }

    /**
     * <p>
     * Swaps the case of String.
     * Properly looks after making sure the start of words are Titlecase and not Uppercase.
     * 转换字符的大小写形式
     * <code>null</code> is returned as <code>null</code>.
     * @param str the String to swap the case of
     * @return the modified String
     */
    public static String swapCase(String str) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder buffer = new StringBuilder(sz);

        boolean whitespace = false;
        char ch;
        char tmp;

        for (int i = 0; i < sz; i++) {
            ch = str.charAt(i);
            if (Character.isUpperCase(ch)) {
                tmp = Character.toLowerCase(ch);
            } else if (Character.isTitleCase(ch)) {
                tmp = Character.toLowerCase(ch);
            } else if (Character.isLowerCase(ch)) {
                if (whitespace) {
                    tmp = Character.toTitleCase(ch);
                } else {
                    tmp = Character.toUpperCase(ch);
                }
            } else {
                tmp = ch;
            }
            buffer.append(tmp);
            whitespace = Character.isWhitespace(ch);
        }
        return buffer.toString();
    }

    /**
     * Capitalise all the words in a String.
     * Uses {@link Character#isWhitespace(char)} as a separator between words.
     * 使用一个特定的字符作为单词的分隔符
     * <code>null</code> will return <code>null</code>.
     * @param str the String to capitalise
     * @return capitalised String
     */
    public static String capitaliseAllWords(String str) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder buffer = new StringBuilder(sz);
        boolean space = true;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
                buffer.append(ch);
                space = true;
            } else if (space) {
                buffer.append(Character.toTitleCase(ch));
                space = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    /**
     * <p>
     * Uncapitalise all the words in a string.
     * Uses {@link Character#isWhitespace(char)} as a separator between words.
     * <code>null</code> will return <code>null</code>
     * @param str the string to uncapitalise
     * @return uncapitalised string
     */
    public static String uncapitaliseAllWords(String str) {
        if (str == null) {
            return null;
        }
        int sz = str.length();
        StringBuilder buffer = new StringBuilder(sz);
        boolean space = true;
        for (int i = 0; i < sz; i++) {
            char ch = str.charAt(i);
            if (Character.isWhitespace(ch)) {
                buffer.append(ch);
                space = true;
            } else if (space) {
                buffer.append(Character.toLowerCase(ch));
                space = false;
            } else {
                buffer.append(ch);
            }
        }
        return buffer.toString();
    }

    // Nested extraction
    // --------------------------------------------------------------------------

    /**
     * Get the String that is nested in between two instances of the same String.
     * If <code>str</code> is <code>null</code>, will return <code>null</code>.
     * 找到被两个相同的字符串包裹的子串
     * @param str the String containing nested-string
     * @param tag the String before and after nested-string
     * @return the String that was nested, or <code>null</code>
     * @throws NullPointerException if tag is <code>null</code>
     */
    public static String getNestedString(String str, String tag) {
        return getNestedString(str, tag, tag);
    }

    /**
     * Get the String that is nested in between two Strings.
     * 找到被两个特定的子串包裹的子串
     * @param str the String containing nested-string
     * @param open the String before nested-string
     * @param close the String after nested-string
     * @return the String that was nested, or <code>null</code>
     * @throws NullPointerException if open or close is <code>null</code>
     */
    public static String getNestedString(String str, String open, String close) {
        if (str == null) {
            return null;
        }
        int start = str.indexOf(open);
        if (start != -1) {
            int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    /**
     * <p>
     * How many times is the substring in the larger String.
     * 查找子串子字符串中出现的次数
     * <code>null</code> returns <code>0</code>.
     * 如果没有则返回0
     * @param str the String to check
     * @param sub the substring to count
     * @return the number of occurances, 0 if the String is <code>null</code>
     * @throws NullPointerException if sub is <code>null</code>
     */
    public static int countMatches(String str, String sub) {
        if (sub.equals("")) {
            return 0;
        }
        if (str == null) {
            return 0;
        }
        int count = 0;
        int idx = 0;
        while ((idx = str.indexOf(sub, idx)) != -1) {
            count++;
            idx += sub.length();
        }
        return count;
    }

    // Character Tests
    // --------------------------------------------------------------------------

    /**
     * Checks if the String contains only unicode letters.
     * 判断字符串是否只含有uincode编码
     * <code>null</code> will return <code>false</code>. An empty String will return <code>true</code>.
     * @param str the String to check
     * @return <code>true</code> if only contains letters, and is non-null
     */
    public static boolean isAlpha(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetter(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only whitespace.
     * 判断字符串是否只含有空格
     * <code>null</code> will return <code>false</code>. An empty String will return <code>true</code>.
     * @param str the String to check
     * @return <code>true</code> if only contains whitespace, and is non-null
     */
    public static boolean isWhitespace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isWhitespace(str.charAt(i)) == false)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only unicode letters and space (<code>' '</code>).
     * 判断字符串时候字含有unicode编码字符或者空格
     * <code>null</code> will return <code>false</code>. An empty String will return <code>true</code>.
     * @param str the String to check
     * @return <code>true</code> if only contains letters and space, and is non-null
     */
    public static boolean isAlphaSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isLetter(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only unicode letters or digits.
     * 判断字符串是否字含有unicode字符或者数字
     * <code>null</code> will return <code>false</code>. An empty String will return <code>true</code>.
     * @param str the String to check
     * @return <code>true</code> if only contains letters or digits, and is non-null
     */
    public static boolean isAlphanumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isLetterOrDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only unicode letters, digits or space (<code>' '</code>).
     * 判断字符串是否字含有unicode字符或者数字或者空格
     * <code>null</code> will return <code>false</code>. An empty String will return <code>true</code>.
     * @param str the String to check
     * @return <code>true</code> if only contains letters, digits or space, and is non-null
     */
    public static boolean isAlphanumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isLetterOrDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    /**
     * <p>
     * Checks if the String contains only unicode digits.
     * 判断字符串是否只含有unicode数字
     * <code>null</code> will return <code>false</code>. An empty String will return <code>true</code>.
     * @param str the String to check
     * @return <code>true</code> if only contains digits, and is non-null
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if (Character.isDigit(str.charAt(i)) == false) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if the String contains only unicode digits or space (<code>' '</code>).
     * 判断字符串是否只含有unicode数字或者空格
     * <code>null</code> will return <code>false</code>. An empty String will return <code>true</code>.
     * @param str the String to check
     * @return <code>true</code> if only contains digits or space, and is non-null
     */
    public static boolean isNumericSpace(String str) {
        if (str == null) {
            return false;
        }
        int sz = str.length();
        for (int i = 0; i < sz; i++) {
            if ((Character.isDigit(str.charAt(i)) == false) && (str.charAt(i) != ' ')) {
                return false;
            }
        }
        return true;
    }

    // Defaults
    // --------------------------------------------------------------------------

    /**
     * Returns either the passed in <code>Object</code> as a String, or, if the <code>Object</code> is <code>null</code>
     * 返回越过字符串
     * @param obj the Object to check
     * @return the passed in Object's toString, or blank if it was <code>null</code>
     */
    public static String defaultString(Object obj) {
        return defaultString(obj, "");
    }

    /**
     * Returns either the passed in <code>Object</code> as a String, or, if the <code>Object</code> is <code>null</code>
     * , a passed in default String.
     * @param obj the Object to check
     * @param defaultString the default String to return if str is <code>null</code>
     * @return the passed in string, or the default if it was <code>null</code>
     */
    public static String defaultString(Object obj, String defaultString) {
        return (obj == null) ? defaultString : obj.toString();
    }

    // Reversing
    // --------------------------------------------------------------------------

    /**
     *将字符串反向
     * <code>null</code> String returns <code>null</code>.
     * @param str the String to reverse
     * @return the reversed String
     */
    public static String reverse(String str) {
        if (str == null) {
            return null;
        }
        return new StringBuilder(str).reverse().toString();
    }

    /**
     * <p>
     * Reverses a String that is delimited by a specific character.
     *反向被特定字符分割的子串
     * The Strings between the delimiters are not reversed. Thus java.lang.String becomes String.lang.java (if the
     * delimiter is <code>'.'</code>).
     * @param str the String to reverse
     * @param delimiter the delimiter to use
     * @return the reversed String
     */
    public static String reverseDelimitedString(String str, String delimiter) {
        // could implement manually, but simple way is to reuse other,
        // probably slower, methods.
        String[] strs = split(str, delimiter);
        reverseArray(strs);
        return join(strs, delimiter);
    }

    /**
     * Reverses an array.
     * TAKEN FROM CollectionsUtils.
     * 反向一个数组
     * @param array the array to reverse
     */
    private static void reverseArray(Object[] array) {
        int i = 0;
        int j = array.length - 1;
        Object tmp;

        while (j > i) {
            tmp = array[j];
            array[j] = array[i];
            array[i] = tmp;
            j--;
            i++;
        }
    }

    // Abbreviating
    // --------------------------------------------------------------------------

    /**
     * Turn "Now is the time for all good men" into "Now is the time for..."
     * 将字符串进行省略尾部
     * If str is less than max characters long, return it. Else abbreviate it to (substring(str, 0, max-3) + "..."). If
     * maxWidth is less than 3, throw an IllegalArgumentException. In no case will it return a string of length greater
     * than maxWidth.
     * @param maxWidth maximum length of result string
     */
    public static String abbreviate(String s, int maxWidth) {
        return abbreviate(s, 0, maxWidth);
    }

    /**
     * Turn "Now is the time for all good men" into "...is the time for..."
     * 省略字符串的 首部和尾部
     * Works like abbreviate(String, int), but allows you to specify a "left edge" offset. Note that this left edge is
     * not necessarily going to be the leftmost character in the result, or the first character following the ellipses,
     * but it will appear somewhere in the result. In no case will it return a string of length greater than maxWidth.
     * @param offset left edge of source string
     * @param maxWidth maximum length of result string
     */
    public static String abbreviate(String s, int offset, int maxWidth) {
        if (maxWidth < 4) {
            throw new IllegalArgumentException("Minimum abbreviation width is 4");
        }
        if (s.length() <= maxWidth) {
            return s;
        }
        if (offset > s.length()) {
            offset = s.length();
        }
        if ((s.length() - offset) < (maxWidth - 3)) {
            offset = s.length() - (maxWidth - 3);
        }
        if (offset <= 4) {
            return s.substring(0, maxWidth - 3) + "...";
        }
        if (maxWidth < 7) {
            throw new IllegalArgumentException("Minimum abbreviation width with offset is 7");
        }
        if ((offset + (maxWidth - 3)) < s.length()) {
            return "..." + abbreviate(s.substring(offset), maxWidth - 3);
        }
        return "..." + s.substring(s.length() - (maxWidth - 3));
    }

    // Difference
    // --------------------------------------------------------------------------

    /**
     * Compare two strings, and return the portion where they differ. (More precisely, return the remainder of the
     * second string, starting from where it's different from the first.)
     * 比较两个字符串，返回他们不同的地方
     * @return the portion of s2 where it differs from s1; returns the empty string ("") if they are equal
     */
    public static String difference(String s1, String s2) {
        int at = differenceAt(s1, s2);
        if (at == -1) {
            return "";
        }
        return s2.substring(at);
    }

    /**
     * Compare two strings, and return the index at which the strings begin to differ.
     * 比较两个字符串返回它们开始不同的位置
     * @return the index where s2 and s1 begin to differ; -1 if they are equal
     */
    public static int differenceAt(String s1, String s2) {
        int i;
        for (i = 0; (i < s1.length()) && (i < s2.length()); ++i) {
            if (s1.charAt(i) != s2.charAt(i)) {
                break;
            }
        }
        if ((i < s2.length()) || (i < s1.length())) {
            return i;
        }
        return -1;
    }

    /**
     * Fill all 'variables' in the given text with the values from the map. Any text looking like '${key}' will get
     * replaced by the value stored in the namespace map under the 'key'.
     *根据给出的键值替换文本中相应的单词
     * @param text
     * @param namespace
     * @return the interpolated text.
     */
    @SuppressWarnings("rawtypes")
    public static String interpolate(String text, Map namespace) {
        Iterator keys = namespace.keySet().iterator();

        while (keys.hasNext()) {
            String key = keys.next().toString();

            Object obj = namespace.get(key);

            if (obj == null) {
                throw new NullPointerException("The value of the key '" + key + "' is null.");
            }

            String value = obj.toString();

            text = StringUtils.replace(text, "${" + key + "}", value);

            if (key.indexOf(" ") == -1) {
                text = StringUtils.replace(text, "$" + key, value);
            }
        }
        return text;
    }

    /**
     * This is basically the inverse of {@link #addAndDeHump(String)}. It will remove the 'replaceThis' parameter and
     * uppercase the next character afterwards.
     * @param data
     * @param replaceThis
     * @return
     */
    public static String removeAndHump(String data, String replaceThis) {
        String temp;

        StringBuilder out = new StringBuilder();

        temp = data;

        StringTokenizer st = new StringTokenizer(temp, replaceThis);

        while (st.hasMoreTokens()) {
            String element = (String) st.nextElement();

            out.append(capitalizeFirstLetter(element));
        }

        return out.toString();
    }

    /**
     * Convert the first character of the given String to uppercase. This method will <i>not</i> trim of spaces!
     * 将给出的字符串的首个字母转换为小写形式
     * <b>Attention:</b> this method will currently throw a <code>IndexOutOfBoundsException</code> for empty strings!
     * @param data the String to get capitalized
     * @return data string with the first character transformed to uppercase
     * @throws NullPointerException if data is <code>null</code>
     */
    public static String capitalizeFirstLetter(String data) {
        char firstLetter = Character.toTitleCase(data.substring(0, 1).charAt(0));

        String restLetters = data.substring(1);

        return firstLetter + restLetters;
    }

    /**
     * Convert the first character of the given String to lowercase. This method will <i>not</i> trim of spaces!
     * 将给出的字符串的首字母转换为小写形式，并将空格去除
     * <b>Attention:</b> this method will currently throw a <code>IndexOutOfBoundsException</code> for empty strings!
     * @param data the String to get it's first character lower-cased.
     * @return data string with the first character transformed to lowercase
     * @throws NullPointerException if data is <code>null</code>
     */
    public static String lowercaseFirstLetter(String data) {
        char firstLetter = Character.toLowerCase(data.substring(0, 1).charAt(0));

        String restLetters = data.substring(1);

        return firstLetter + restLetters;
    }

    /**
     * Take the input string and un-camel-case it.
     * 将输入的字符串分解按大小写分解
     * @param input
     * @return deHumped string
     */
    public static String addAndDeHump(String input) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < input.length(); i++) {
            if ((i != 0) && Character.isUpperCase(input.charAt(i))) {
                sb.append('-');
            }

            sb.append(input.charAt(i));
        }

        return sb.toString().trim().toLowerCase(Locale.ENGLISH);
    }

    /**
     * <p>
     * Quote and escape a String with the given character, handling <code>null</code>.
     *将字符串进行转化，转化方式如下
     * StringUtils.quoteAndEscape(null, *)    = null
     * StringUtils.quoteAndEscape("", *)      = ""
     * StringUtils.quoteAndEscape("abc", '"') = abc
     * StringUtils.quoteAndEscape("a\"bc", '"') = "a\"bc"
     * StringUtils.quoteAndEscape("a\"bc", '\'') = 'a\"bc'
     * @param source
     * @param quoteChar
     * @return the String quoted and escaped
     * @see #quoteAndEscape(String, char, char[], char[], char, boolean)
     * @since 1.5.1
     */
    public static String quoteAndEscape(String source, char quoteChar) {
        return quoteAndEscape(source, quoteChar, new char[] { quoteChar }, new char[] { ' ' },
                '\\', false);
    }

    /**
     * <p>
     * Quote and escape a String with the given character, handling <code>null</code>.
     * @param source
     * @param quoteChar
     * @param quotingTriggers
     * @return the String quoted and escaped
     * @see #quoteAndEscape(String, char, char[], char[], char, boolean)
     * @since 1.5.1
     */
    public static String quoteAndEscape(String source, char quoteChar, char[] quotingTriggers) {
        return quoteAndEscape(source, quoteChar, new char[] { quoteChar }, quotingTriggers, '\\',
                false);
    }

    /**
     * @param source
     * @param quoteChar
     * @param escapedChars
     * @param escapeChar
     * @param force
     * @return the String quoted and escaped
     * @see #quoteAndEscape(String, char, char[], char[], char, boolean)
     * @since 1.5.1
     */
    public static String quoteAndEscape(String source, char quoteChar, final char[] escapedChars,
                                        char escapeChar, boolean force) {
        return quoteAndEscape(source, quoteChar, escapedChars, new char[] { ' ' }, escapeChar,
                force);
    }

    /**
     * @param source
     * @param quoteChar
     * @param escapedChars
     * @param quotingTriggers
     * @param escapeChar
     * @param force
     * @return the String quoted and escaped
     * @since 1.5.1
     */
    public static String quoteAndEscape(String source, char quoteChar, final char[] escapedChars,
                                        final char[] quotingTriggers, char escapeChar, boolean force) {
        if (source == null) {
            return null;
        }

        if (!force && source.startsWith(Character.toString(quoteChar))
                && source.endsWith(Character.toString(quoteChar))) {
            return source;
        }

        String escaped = escape(source, escapedChars, escapeChar);

        boolean quote = false;
        if (force) {
            quote = true;
        } else if (!escaped.equals(source)) {
            quote = true;
        } else {
            for (int i = 0; i < quotingTriggers.length; i++) {
                if (escaped.indexOf(quotingTriggers[i]) > -1) {
                    quote = true;
                    break;
                }
            }
        }

        if (quote) {
            return quoteChar + escaped + quoteChar;
        }

        return escaped;
    }

    /**
     * @param source
     * @param escapedChars
     * @param escapeChar
     * @return the String escaped
     * @since 1.5.1
     */
    public static String escape(String source, final char[] escapedChars, char escapeChar) {
        if (source == null) {
            return null;
        }

        char[] eqc = new char[escapedChars.length];
        System.arraycopy(escapedChars, 0, eqc, 0, escapedChars.length);
        Arrays.sort(eqc);

        StringBuilder buffer = new StringBuilder(source.length());

        @SuppressWarnings("unused")
        int escapeCount = 0;
        for (int i = 0; i < source.length(); i++) {
            final char c = source.charAt(i);
            int result = Arrays.binarySearch(eqc, c);

            if (result > -1) {
                buffer.append(escapeChar);
                escapeCount++;
            }

            buffer.append(c);
        }

        return buffer.toString();
    }

    /**
     * Remove all duplicate whitespace characters and line terminators are replaced with a single space.
     *将重复的空格替换为单个的空格
     * @param s a not null String
     * @return a string with unique whitespace.
     * @since 1.5.7
     */
    public static String removeDuplicateWhitespace(String s) {
        StringBuilder result = new StringBuilder();
        int length = s.length();
        boolean isPreviousWhiteSpace = false;
        for (int i = 0; i < length; i++) {
            char c = s.charAt(i);
            boolean thisCharWhiteSpace = Character.isWhitespace(c);
            if (!(isPreviousWhiteSpace && thisCharWhiteSpace)) {
                result.append(c);
            }
            isPreviousWhiteSpace = thisCharWhiteSpace;
        }
        return result.toString();
    }

    /**
     * Parses the given String and replaces all occurrences of '\n', '\r' and '\r\n' with the system line separator.
     *解析带单斜杠的字符
     * @param s a not null String
     * @return a String that contains only System line separators.
     * @see #unifyLineSeparators(String, String)
     * @since 1.5.7
     */
    public static String unifyLineSeparators(String s) {
        return unifyLineSeparators(s, System.getProperty("line.separator"));
    }

    /**
     * Parses the given String and replaces all occurrences of '\n', '\r' and '\r\n' with the given line separator.
     *解析所有带斜杠的字符
     * @param s a not null String
     * @param ls the wanted line separator ("\n" on UNIX), if <code>null</code> using the System line separator.
     * @return a String that contains only System line separators.
     * @throws IllegalArgumentException if ls is not '\n', '\r' and '\r\n' characters.
     * @since 1.5.7
     */
    public static String unifyLineSeparators(String s, String ls) {
        if (s == null) {
            return null;
        }

        if (ls == null) {
            ls = System.getProperty("line.separator");
        }

        if (!(ls.equals("\n") || ls.equals("\r") || ls.equals("\r\n"))) {
            throw new IllegalArgumentException("Requested line separator is invalid.");
        }

        int length = s.length();

        StringBuilder buffer = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            if (s.charAt(i) == '\r') {
                if ((i + 1) < length && s.charAt(i + 1) == '\n') {
                    i++;
                }

                buffer.append(ls);
            } else if (s.charAt(i) == '\n') {
                buffer.append(ls);
            } else {
                buffer.append(s.charAt(i));
            }
        }

        return buffer.toString();
    }

    /**
     * <p>
     * Checks if String contains a search character, handling <code>null</code>. This method uses
     * {@link String#indexOf(int)}.
     * 检查字符串是否含有要进行查找的字符
     * A <code>null</code> or empty ("") String will return <code>false</code>.
     * StringUtils.contains(null, *)    = false
     * StringUtils.contains("", *)      = false
     * StringUtils.contains("abc", 'a') = true
     * StringUtils.contains("abc", 'z') = false
     * @param str the String to check, may be null
     * @param searchChar the character to find
     * @return true if the String contains the search character, false if not or <code>null</code> string input
     * @since 1.5.7
     */
    public static boolean contains(String str, char searchChar) {
        if (isEmpty(str)) {
            return false;
        }
        return str.indexOf(searchChar) >= 0;
    }

    /**
     * Checks if String contains a search String, handling <code>null</code>. This method uses
     * {@link String#indexOf(int)}.
     * 判断字符串是否含有要查找的特定字符子串或者句柄
     * A <code>null</code> String will return <code>false</code>.
     * StringUtils.contains(null, *)     = false
     * StringUtils.contains(*, null)     = false
     * StringUtils.contains("", "")      = true
     * StringUtils.contains("abc", "")   = true
     * StringUtils.contains("abc", "a")  = true
     * StringUtils.contains("abc", "z")  = false
     * @param str the String to check, may be null
     * @param searchStr the String to find, may be null
     * @return true if the String contains the search String, false if not or <code>null</code> string input
     * @since 1.5.7
     */
    public static boolean contains(String str, String searchStr) {
        if (str == null || searchStr == null) {
            return false;
        }
        return str.indexOf(searchStr) >= 0;
    }

    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);

        return m.matches();
    }

    public static boolean isMobileNo(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }
}
