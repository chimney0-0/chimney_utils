package idv.common;

import org.apache.commons.lang3.StringUtils;

/**
 * Created with IntelliJ IDEA.
 *
 * @author Berry_Cooper.
 * @date 2019-03-29 22:36
 * fileName：VersionUtils
 * Use：
 */
public class VersionUtil {

    /**
     * v<主版本号>.<次版本号>.<修订版本号>.日期版本号(20190329)_希腊字母版本号(beta)
     * 如 v1.0.0.20190329_beta
     * alpha版：内部测试版。α是希腊字母的第一个，表示最早的版本，一般用户不要下载这个版本，这个版本包含很多BUG，功能也不全，主要是给开发人员和 测试人员测试和找BUG用的。
     * beta版：公开测试版。β是希腊字母的第二个，顾名思义，这个版本比alpha版发布得晚一些，主要是给“部落”用户和忠实用户测试用的，该版本任然存 在很多BUG，但是相对alpha版要稳定一些。这个阶段版本的软件还会不断增加新功能。如果你是发烧友，可以下载这个版本。
     * rc版：Release Candidate（候选版本），该版本又较beta版更进一步了，该版本功能不再增加，和最终发布版功能一样。这个版本有点像最终发行版之前的一个类似 预览版，这个的发布就标明离最终发行版不远了。作为普通用户，如果你很急着用这个软件的话，也可以下载这个版本。
     * stable版：稳定版。在开源软件中，都有stable版，这个就是开源软件的最终发行版，用户可以放心大胆的用了。
     * 上一级版本升级，次级版本归零
     *
     * @param args
     */
    public static void main(String[] args) {
        System.out.println(getFirstVersion("beta"));
    }

    public static String getFirstVersion(String alpha) {
        String dateVersion = DateUtils.getDateTimeDay();
        return "v0.0.1." + dateVersion + "_" + alpha;
    }

    /**
     * 升级版本号 按照默认最低级别升级
     *
     * @param version v<主版本号>.<次版本号>.<修订版本号>.日期版本号(20190329)_希腊字母版本号(beta)
     * @param level   升级级别 1，2，3
     * @param alpha   希腊字母版本级别
     * @return 下一个版本
     */
    public static String getNextVersion(String version, int level, String alpha) {
        if (!version.matches("^v\\d+\\.\\d+\\.\\d+\\.\\d{8}_[a-zA-Z]+")) {
            throw new RuntimeException("版本格式不正确");
        }
        String dateVersion = DateUtils.getDateTimeDay();
        String targetStr;
        String reg;
        if (StringUtils.isNotBlank(alpha)) {
            version = version.replaceAll("(^v\\d+\\.\\d+\\.\\d+\\.\\d{8}_)([a-zA-Z]+)", "$1" + alpha);
        }
        if (level == 1) {
            targetStr = version.replaceAll("^v(\\d+)\\..*", "$1");
            reg = "^(v)(\\d+)(\\.\\d+\\.\\d+\\.)(\\d{8})(_[a-zA-Z]+)";
        } else if (level == 2) {
            targetStr = version.replaceAll("^v\\d+\\.(\\d+)\\..*", "$1");
            reg = "^(v\\d+\\.)(\\d+)(\\.\\d+\\.)(\\d{8})(_[a-zA-Z]+)";
        } else if (level == 3) {
            targetStr = version.replaceAll("^v\\d+\\.\\d+\\.(\\d+)\\.\\d{8}_[a-zA-Z]+", "$1");
            reg = "^(v\\d+\\.\\d+\\.)(\\d+)(\\.)(\\d{8})(_[a-zA-Z]+)";
        } else {
            throw new RuntimeException("更新版本级别非法");
        }
        int nextValue = StringUtils.isBlank(targetStr) ? 0 : Integer.valueOf(targetStr) + 1;
        return version.replaceAll(reg, "$1" + nextValue + "$3" + dateVersion + "$5");
    }
}
