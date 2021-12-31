package idv.common;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.seassoon.sixiang.business.ss.model.GitAction;
import com.seassoon.sixiang.frame.exception.BaseException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.internal.storage.file.FileRepository;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.gitlab.api.GitlabAPI;
import org.gitlab.api.models.GitlabGroup;
import org.gitlab.api.models.GitlabProject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Configuration
public class GitUtils {
    private final static Logger log = LoggerFactory.getLogger(com.seassoon.sixiang.utils.GitUtils.class);
    @Value("${gitlab.token}")
    private String apiToken;
    @Value("${gitlab.host}")
    private String hostUrl;
    @Value("${gitlab.path}")
    private String path;
    private static Map<String, String> nameMap;

    private static GitlabAPI gitlabAPI;

    @Resource
    com.seassoon.sixiang.utils.MinIOUtils minIOUtils;

    /**
     * 初始化GitlabAPI
     */
    @Primary
    @Bean
    public GitlabAPI getApi() {
        gitlabAPI = GitlabAPI.connect(hostUrl, apiToken);
        if (Objects.isNull(gitlabAPI)) {
            log.error("git连接出错>>>>>");
        }
        return gitlabAPI;
    }

    /**
     * 创建gitlab项目
     *
     * @param grouping    分组名称
     * @param projectName 项目名称
     * @param description 描述
     * @param url         根路径
     * @return 返回创建项目路径
     * @throws IOException on gitlab api call error
     */
    public String createGitLabProject(String grouping, String projectName, String description, String url) throws
            IOException {
        if (Objects.nonNull(exProject(projectName))) {
            log.error("项目已存在");
        }
        GitlabAPI gitlabAPI = new com.seassoon.sixiang.utils.GitUtils().getApi();
        //获取命名空间id
        Integer nameSpaceId = getNameSpaceId(grouping, url);
        //2.创建项目
        if (nameSpaceId != null) {
            GitlabProject project = gitlabAPI.createProject(projectName, nameSpaceId, description, true, true, true, true, true, true, null, null);
            if (project.getHttpUrl() == null) {
                log.error("获取nameSpaceId错误");
                return null;
            } else {
                return project.getHttpUrl();
            }
        } else {
            log.error("获取nameSpaceId错误");
        }
        return null;
    }


    /**
     * 判断项目是否已存在
     *
     * @param orgName 项目名称
     * @return 检查是否已经创建git项目，如果已经创建返回项目ID，没创建返回Null
     * @throws IOException on gitlab api call error
     */
    public Integer exProject(String orgName) throws IOException {
        List<GitlabProject> gitlabProjects = gitlabAPI.searchProjects(orgName);
        if (gitlabProjects.size() > 0) {
            for (GitlabProject gitlabProject : gitlabProjects) {
                String name = gitlabProject.getName();
                if (orgName.equals(name)) {
                    log.info("{}:改项目已存在，返回git地址", orgName);
                    return gitlabProject.getId();
                }
            }
        }
        return null;
    }

    //获取spaceid
    public static Integer getNameSpaceId(String grouping, String url) throws IOException {
        Integer nameSpaceId = null;
        String developerName = nameMap.get(grouping);
        String url2 = url + developerName;
        CloseableHttpClient httpClient = null;
        httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url2 + "?private_token=" + new com.seassoon.sixiang.utils.GitUtils().apiToken);
        CloseableHttpResponse execute = httpClient.execute(httpGet);
        HttpEntity entity = execute.getEntity();
        log.info("响应状态为:" + execute.getStatusLine());
        String html = null;
        if (entity != null) {
            html = EntityUtils.toString(entity, "utf-8");
        } else {
            log.error("获取nameSpaceId错误");
        }
        if (null != html) {
            Document document = Jsoup.parse(html);
            if (null != document) {
                Elements a = document.select(" div.top-area > div > a");
                String href = a.attr("href");
                Matcher m = Pattern.compile("(\\d+)").matcher(href);
                if (m.find()) {
                    nameSpaceId = Integer.valueOf(m.group(0));
                }
            }
        } else {
            log.error("获取nameSpaceId错误");
        }
        httpClient.close();
        return nameSpaceId;
    }

//    /**
//     * 提交图片
//     *
//     * @param filePath      文件路径
//     * @param file          文件
//     * @param commitMessage 描述
//     * @param id            项目id
//     * @param action        指令 create/delete/move/update/chmod
//     * @param branch        分支
//     * @return 返回boolean
//     * @throws IOException on gitlab api call error
//     */
//    public boolean commintFile(String filePath, File file, String commitMessage, Integer id, String action, String branch) throws IOException {
//        JSONObject jsonObject = new JSONObject();
//        JSONArray actionsArray = new JSONArray();
//        JSONObject actionsObject = new JSONObject();
//        actionsObject.put("action", action);
//        actionsObject.put("file_path", filePath);
//        actionsObject.put("content", BaseUtils.GetImageStr(file.getPath()));
//        actionsObject.put("encoding", "base64");
//        actionsArray.add(actionsObject);
//        jsonObject.put("id", id);
//        jsonObject.put("private_token", apiToken);
//        jsonObject.put("branch", branch);
//        jsonObject.put("commit_message", commitMessage);
//        jsonObject.put("actions", actionsArray);
//        JSONObject resultJson = JSONObject.parseObject(httpUtils.postRaw(hostUrl + "/api/v4/projects/" + id + "/repository/commits", jsonObject.toJSONString()));
//        return Objects.isNull(resultJson.get("author_name")) ? false : true;
//    }


    /**
     * 提交多张图片
     *
     * @param actions       内容
     * @param commitMessage 描述
     * @param id            项目id
     * @param branch        分支
     * @return 返回boolean
     * @throws IOException on gitlab api call error
     */
    public boolean commintFiles(List<GitAction> actions, String commitMessage, Integer id, String branch) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONArray actionsArray = new JSONArray();
        actions.forEach(action -> {
            JSONObject actionsObject = new JSONObject();
            actionsObject.put("action", action.getAction());
            actionsObject.put("file_path", action.getFilePath());
            actionsObject.put("content", GetImageStr(action.getFile()));
            actionsObject.put("encoding", "base64");
            actionsArray.add(actionsObject);
        });
        jsonObject.put("id", id);
        jsonObject.put("private_token", apiToken);
        jsonObject.put("branch", branch);
        jsonObject.put("commit_message", commitMessage);
        jsonObject.put("actions", actionsArray);
        JSONObject resultJson = JSONObject.parseObject(httpUtils.postRaw(hostUrl + "/api/v4/projects/" + id + "/repository/commits", jsonObject.toJSONString()));
        return Objects.isNull(resultJson.get("author_name")) ? false : true;
    }


//    /**
//     * 提交文本类型
//     *
//     * @param filePath      文件路径
//     * @param content       文件内容
//     * @param commitMessage 描述
//     * @param id            项目id
//     * @param action        指令 create/delete/move/update/chmod
//     * @param branch        分支
//     * @return 返回boolean
//     * @throws IOException on gitlab api call error
//     */
//    public boolean commint(String filePath, String content, String commitMessage, Integer id, String action, String branch) throws IOException {
//        JSONObject jsonObject = new JSONObject();
//        JSONArray actionsArray = new JSONArray();
//        JSONObject actionsObject = new JSONObject();
//        actionsObject.put("action", action);
//        actionsObject.put("file_path", filePath);
//        actionsObject.put("content", content);
//        actionsArray.add(actionsObject);
//        jsonObject.put("id", id);
//        jsonObject.put("private_token", apiToken);
//        jsonObject.put("branch", branch);
//        jsonObject.put("commit_message", commitMessage);
//        jsonObject.put("actions", actionsArray);
//        JSONObject resultJson = JSONObject.parseObject(httpUtils.postRaw(hostUrl + "/api/v4/projects/" + id + "/repository/commits", jsonObject.toJSONString()));
//        return Objects.isNull(resultJson.get("author_name")) ? false : true;
//    }

    /**
     * @param actions       提交多个文件
     * @param commitMessage 描述
     * @param id            项目id
     * @param branch        分支
     * @return 返回boolean
     * @throws IOException on gitlab api call error
     */
    public boolean commint(List<GitAction> actions, String commitMessage, Integer id, String branch) throws IOException {
        JSONObject jsonObject = new JSONObject();
        JSONArray actionsArray = new JSONArray();
        actions.forEach(action -> {
            JSONObject actionsObject = new JSONObject();
            actionsObject.put("action", action.getAction());
            actionsObject.put("file_path", action.getFilePath());
            actionsObject.put("content", action.getContent());
            actionsArray.add(actionsObject);
        });
        jsonObject.put("id", id);
        jsonObject.put("private_token", apiToken);
        jsonObject.put("branch", branch);
        jsonObject.put("commit_message", commitMessage);
        jsonObject.put("actions", actionsArray);
        JSONObject resultJson = JSONObject.parseObject(httpUtils.postRaw(hostUrl + "/api/v4/projects/" + id + "/repository/commits", jsonObject.toJSONString()));
        return Objects.isNull(resultJson.get("author_name")) ? false : true;
    }

    /**
     * 获取项目
     *
     * @param namespace   命名空间
     * @param projectName 项目名称
     * @return 获取项目，如果已经创建返回项目ID
     */
    public GitlabProject getProjectId(String namespace, String projectName) {
        try {
            return gitlabAPI.getProject(namespace, projectName.trim());
        } catch (IOException e) {
            log.error("获取项目失败", e);
            return null;
        }
    }


    /**
     * 创建组
     *
     * @param groupName 组名
     * @return 返回组id
     */
    public GitlabGroup createGroup(String groupName) throws IOException {
        groupName = PinyinUtil.getPinyin(groupName);
        GitlabGroup gitlabGroup = gitlabAPI.getGroup(path);
        GitlabGroup newGroup = null;
        try {
            newGroup = gitlabAPI.createGroup(groupName, groupName, null, null, null, gitlabGroup.getId());
//            newGroup.setDescription(groupName);
//            gitlabAPI.updateGroup(newGroup, gitlabAPI.getUser());
            return newGroup;
        } catch (IOException e) {
            return gitlabAPI.getGroup(path + "/" + groupName);
        }
    }


    /**
     * 创建项目
     *
     * @param projectName 项目名称
     * @param group       组
     * @param description 描述
     * @return 返回组id
     */
    public GitlabProject createProject(String projectName, GitlabGroup group, String description) {
        try {
            GitlabProject gitlabProject = gitlabAPI.createProjectForGroup(projectName, group, description);
            return gitlabProject;
        } catch (IOException e) {
            return getProjectId(group.getFullPath(), projectName);
        }
    }


    /**
     * 创建分支
     *
     * @param project    项目
     * @param branchName 分支名称
     * @return 返回boolean
     */
    public boolean createProject(GitlabProject project, String branchName) {
        try {
            gitlabAPI.createBranch(project, branchName, "master");
            return true;
        } catch (IOException e) {
            throw new BaseException(403, "创建分支失败");
        }
    }

    /**
     * 删除分支
     *
     * @param project    项目
     * @param branchName 分支名称
     * @return 返回boolean
     */
    public boolean deleteBranch(GitlabProject project, String branchName) {
        try {
            gitlabAPI.deleteBranch(project.getId(), branchName);
            return true;
        } catch (IOException e) {
            try {
                if (Objects.nonNull(gitlabAPI.getBranch(project, branchName))) {
                    throw new BaseException(403, "删除分支失败 " + e);
                }
                return false;
            } catch (IOException ioException) {
                //分支不存在报错 默认已经删除该分支
                return true;
            }
        }
    }

    /**
     * @param userName 用户名
     * @param password 密码
     * @return
     */
    public static CredentialsProvider createCredential(String userName, String password) {
        return new UsernamePasswordCredentialsProvider(userName, password);
    }

    /**
     * @param repoUrl
     * @param cloneDir
     * @param provider
     * @param branch
     * @return
     * @throws GitAPIException
     */
    public static Git fromCloneRepository(String repoUrl, String cloneDir, String branch, CredentialsProvider provider) throws GitAPIException {
        Git git = Git.cloneRepository()
                .setCredentialsProvider(provider)
                .setURI(repoUrl)
                .setBranch(branch)
                .setDirectory(new File(cloneDir)).call();


        return git;

    }

    /**
     * 本地代码提交
     *
     * @param msg:提交信息
     * @param localRepoPath:本地代码仓位置
     */
    public void gitCommit(String msg, String localRepoPath) {
        Git git = null;
        try {
            git = new Git(new FileRepository(localRepoPath + "/.git"));
            //全部提交
            git.commit().setAll(true).setMessage(msg).call();
            log.debug("Git commit success");
        } catch (Exception e) {
            log.error("Git commit fail.", e);
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }

    /**
     * 本地代码提交
     *
     * @param branch:提交信息
     * @param localRepoPath:提交信息
     * @return boolean:提交结果
     */
    public boolean gitPull(String branch, String localRepoPath) {
        boolean pullFlag = true;
        try (Git git = Git.open(new File(localRepoPath + "/.git"))) {
            git.pull().setRemoteBranchName(branch).call();
        } catch (Exception e) {
            pullFlag = false;
        }
        return pullFlag;
    }


    /**
     * push本地代码到远程仓库
     *
     * @param remoteRepoPath：远程提交地址
     * @param localRepoPath：项目地址
     * @param branch：分支
     * @param userName：用户名
     * @param passWord：密码
     */
    public void gitPush(String remoteRepoPath, String localRepoPath, String branch, String userName,
                        String passWord) {
        Git git = null;
        try {
            git = new Git(new FileRepository(localRepoPath + "/.git"));
            UsernamePasswordCredentialsProvider usernamePasswordCredentialsProvider
                    = new UsernamePasswordCredentialsProvider(userName, passWord);
            git.push()
                    .setRemote(remoteRepoPath)
                    .setRefSpecs(new RefSpec(branch))
                    .setCredentialsProvider(usernamePasswordCredentialsProvider)
                    .call();
            log.debug("Git push success");
        } catch (Exception e) {
            log.error("Git push fail.", e);
        } finally {
            if (git != null) {
                git.close();
            }
        }
    }


    public String GetImageStr(String imgFilePath) {
        // 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        byte[] data = null;

        // 读取图片字节数组
        try {
            InputStream in = minIOUtils.getFileOupFullPath(imgFilePath);
//            data = new byte[in.available()];
//            in.read(data);
            data = IOUtils.toByteArray(in);
            in.close();
        } catch (IOException e) {
            log.error("read image fail",e);
            return null;
        }

        // 对字节数组Base64编码
        Base64 encoder = new Base64();
        return encoder.encodeAsString(data);// 返回Base64编码过的字节数组字符串
    }

}



