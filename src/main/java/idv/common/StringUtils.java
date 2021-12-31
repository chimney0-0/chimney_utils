package idv.common;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author xueancao
 */
public class StringUtils extends org.apache.commons.lang3.StringUtils {
    private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     *
     * @param keyword
     * @return
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (isNotBlank(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]", "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static boolean checkNoSpecChar(String inputStr) {
        String[] specCharArr = "\\/:*?\"<>|".split("");
        for (String s : specCharArr) {
            if (inputStr.contains(s)) {
                return false;
            }
        }
        return true;
    }

    public static String removeEnterChar(String str) {
        if (isNotBlank(str)) {
            str = str.replace("\n", "");
        }
        return str;
    }

    public static String sortMap(Map<String, String> paraMap) {
        List<Map.Entry<String, String>> infoIds = new ArrayList<>(paraMap.entrySet());
        // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
        infoIds.sort(Comparator.comparing(Map.Entry::getKey));
        // 构造URL 键值对的格式
        StringBuilder buf = new StringBuilder();
        for (Map.Entry<String, String> item : infoIds) {
            if (isNotBlank(item.getKey())) {
                String key = item.getKey();
                String val = item.getValue();
                buf.append(key).append("=").append(val);
                buf.append("&");
            }

        }
        String buff = buf.toString();
        if (!buff.isEmpty()) {
            buff = buff.substring(0, buff.length() - 1);
        }
        return buff;
    }

    /**
     * 随机生成32位字符串
     *
     * @return 32位字符串
     */
    public static String getRandomStr() {
        return getRandomStr(32);
    }

    /**
     * 随机生成指定长度位字符串
     *
     * @return 指定长度位字符串
     */
    public static String getRandomStr(int length) {
        String base = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 将请求参数流转为字符串
     *
     * @param request
     * @return
     */
    public static String requestToStr(HttpServletRequest request) {
        String requestStr = null;
        try {
            InputStream inStream = null;
            inStream = request.getInputStream();
            int bufferSize = 1024;
            if (inStream != null) {
                ByteArrayOutputStream outStream = new ByteArrayOutputStream();
                byte[] tempBytes = new byte[bufferSize];
                int count = -1;
                while ((count = inStream.read(tempBytes, 0, bufferSize)) != -1) {
                    outStream.write(tempBytes, 0, count);
                }
                outStream.flush();
                //将流转换成字符串
                requestStr = new String(outStream.toByteArray(), "UTF-8");
            }
        } catch (IOException e) {
            log.error("request 2 str fail", e);
        }
        return requestStr;
    }

    /**
     * 解析出url参数中的键值对
     * 如 "index.jsp?Action=&id=123"，解析出Action:del,id:123存入map中
     *
     * @param url url地址
     * @return url请求参数部分
     */
    public static Map<String, String> getParamsFromUrl(String url) {
        Map<String, String> mapRequest = new HashMap<>(16);
        String[] paramsArray = url.substring(url.indexOf('?') + 1).split("&");
        for (String s : paramsArray) {
            String[] item = s.split("=");
            if (item.length == 2) {
                mapRequest.put(item[0], item[1]);
            }
        }
        return mapRequest;
    }


    /**
     * 求两个字符串的相似度，基于jaro similarity算法实现。<br/>
     * <p>
     * <ol>
     *   <li>“指定代表或者共同委托代理人授权委托书”与“指定代表人或者共同委托代理人授权委托书”相似度为0.89，不符合业务场景，
     *   引入正序和倒序求相似，并求最大值，得出相似度为0.988</li>
     *   <li>在1基础上“住所口”与“住所”相似度为0.88，不符合业务场景，引入一个与字符串长度相关的函数，对短字符串相似系数适当放大，得出相似度为0.929</li>
     * </ol>
     *
     * @param a         字符串a
     * @param b         字符串b
     * @param threshold 相似度阈值
     * @return 相似度
     */
    public static double similarity(String a, String b, double threshold) {
//        Assert.notBlank(a, "Argument 'a' cannot be null");
//        Assert.notBlank(b, "Argument 'b' cannot be null");

        // 放大系数
        double amplificationCoefficient = 2.5D;

        // 分别计算字符串相似度和反转字符串后的相似度，然后求最大值
        double regularSimilarity = jaroDistance(a, b);
        double reverseSimilarity = jaroDistance(org.apache.commons.lang3.StringUtils.reverse(a), org.apache.commons.lang3.StringUtils.reverse(b));
        double similarity = Math.max(regularSimilarity, reverseSimilarity);

        similarity = similarity + (1 - similarity) * Math.exp(Math.negateExact((a.length() + b.length()) / 2) / amplificationCoefficient);
        similarity = Math.min(similarity, 1);

        // 修正单个字不同但相似度低于阈值情况
        if (similarity < threshold && a.length() == b.length() && a.length() > 3) {
            Collection<Integer> leftDiff = CollectionUtils.subtract(a.chars().boxed().collect(Collectors.toSet()), b.chars().boxed().collect(Collectors.toSet()));
            Collection<Integer> rightDiff = CollectionUtils.subtract(b.chars().boxed().collect(Collectors.toSet()), a.chars().boxed().collect(Collectors.toSet()));

            if (leftDiff.size() == 1 && rightDiff.size() == 1) {
                similarity = threshold + 0.01;
            }
        }
        return similarity;
    }


    /**
     * 字符串相似度比较算法jaro distance similarity的java版实现。
     *
     * @param a 字符串a
     * @param b 字符串b
     * @return 相似度
     * @see <a href="https://www.geeksforgeeks.org/jaro-and-jaro-winkler-similarity/?ref=lbp">jaro similarity</a>
     */
    public static double jaroDistance(String a, String b) {
//        Assert.notBlank(a, "Argument 'a' cannot be null");
//        Assert.notBlank(b, "Argument 'b' cannot be null");

        // If the Strings are equal
        if (a.equals(b)) {
            return 1.0;
        }

        // Maximum distance upto which matching
        // is allowed
        int maxDist = (int) (Math.floor(Math.max(a.length(), b.length()) / 2.00) - 1);

        // Hash for matches
        int[] aHash = new int[a.length()];
        int[] bHash = new int[b.length()];

        // Count of matches
        int match = 0;

        // Traverse through the first String
        for (int i = 0; i < a.length(); i++) {
            // Check if there is any matches
            for (int j = Math.max(0, i - maxDist); j < Math.min(b.length(), i + maxDist + 1); j++)
                // If there is a match
                if (a.charAt(i) == b.charAt(j) && bHash[j] == 0) {
                    aHash[i] = 1;
                    bHash[j] = 1;
                    match++;
                    break;
                }
        }

        // If there is no match
        if (match == 0)
            return 0.0;

        // Number of transpositions
        double t = 0;

        int point = 0;

        // Count number of occurrences
        // where two characters match but
        // there is a third matched character
        // in between the indices
        for (int i = 0; i < a.length(); i++)
            if (aHash[i] == 1) {
                // Find the next matched character
                // in second String
                while (bHash[point] == 0)
                    point++;

                if (a.charAt(i) != b.charAt(point++))
                    t++;
            }

        t /= 2;

        // Return the Jaro Similarity
        return (((double) match) / ((double) a.length())
                + ((double) match) / ((double) b.length())
                + ((double) match - t) / ((double) match))
                / 3.0;
    }
}
