package idv.common;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Vector;


public class SftpUtils {

    // ftp服务器地址
    private String host;

    // ftp服务器端口号默认为21
    private Integer port;

    // ftp登录账号
    private String username;

    // ftp登录密码fileName
    private String password;

    private final Logger log = LoggerFactory.getLogger(com.seassoon.sixiang.utils.SftpUtils.class);

    public SftpUtils(String host, Integer port, String username, String password) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    private ChannelSftp channelSftp;
    private ChannelExec channelExec;
    private Session session=null;
    private int timeout=60000;

    public void init() throws JSchException {
        JSch jSch=new JSch(); //创建JSch对象
        session=jSch.getSession(username, host, port);//根据用户名，主机ip和端口获取一个Session对象
        session.setPassword(password); //设置密码
        Properties config=new Properties();
        config.put("StrictHostKeyChecking", "no");
        session.setConfig(config);//为Session对象设置properties
        session.setTimeout(timeout);//设置超时
        session.connect();//通过Session建立连接
    }

    public void download(String src,String dst) throws JSchException, SftpException{
        //src linux服务器文件地址，dst 本地存放地址
        channelSftp=(ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        channelSftp.get(src, dst);
        channelSftp.quit();
    }
    public void upLoad(String src,String dst) throws JSchException,SftpException{
        //src 本机文件地址。 dst 远程文件地址
        channelSftp=(ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        channelSftp.put(src, dst);
        channelSftp.quit();
    }

    public void connect() throws JSchException,SftpException{
        channelSftp=(ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
    }

    public void upLoadSingle(InputStream is, String dst) throws JSchException,SftpException{
//        channelSftp=(ChannelSftp) session.openChannel("sftp");
//        channelSftp.connect();
        channelSftp.put(is, dst);
//        channelSftp.quit();
    }

    public void quit(){
        channelSftp.quit();
    }

    public void close(){
        session.disconnect();
    }

    public int count(String dir) throws JSchException, SftpException {
        channelSftp=(ChannelSftp) session.openChannel("sftp");
        channelSftp.connect();
        Vector<?> vector = channelSftp.ls(dir);
        int size =  vector.size();
        channelSftp.quit();
        return size;
    }

    public List<String> listFileNames(String dir) {
        List<String> list = new ArrayList<String>();
        ChannelSftp sftp = null;
        Channel channel = null;
        Session sshSession = null;
        try {
            JSch jsch = new JSch();
            jsch.getSession(username, host, port);
            sshSession = jsch.getSession(username, host, port);
            sshSession.setPassword(password);
            Properties sshConfig = new Properties();
            sshConfig.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(sshConfig);
            sshSession.connect();
            log.debug("Session connected!");
            channel = sshSession.openChannel("sftp");
            channel.connect();
            log.debug("Channel connected!");
            sftp = (ChannelSftp) channel;
            Vector<?> vector = sftp.ls(dir);
            for (Object item:vector) {
                ChannelSftp.LsEntry entry = (ChannelSftp.LsEntry) item;
//                System.out.println(entry.getFilename());
                list.add(entry.getFilename());
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeChannel(sftp);
            closeChannel(channel);
            closeSession(sshSession);
        }
        return list;
    }

    private static void closeChannel(Channel channel) {
        if (channel != null) {
            if (channel.isConnected()) {
                channel.disconnect();
            }
        }
    }

    private static void closeSession(Session session) {
        if (session != null) {
            if (session.isConnected()) {
                session.disconnect();
            }
        }
    }

}
