package idv.common;

import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class GridFsUtils {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    
    public String saveFile(MultipartFile file, String fileName) throws IOException {
        return saveFile(file, fileName, null);
    }

    
    public String saveFile(InputStream inputStream, String fileName) {
        return saveFile(inputStream, fileName, null);
    }

    
    public String saveFile(MultipartFile file, String fileName, Map<String, Object> metadata) throws IOException {
        // 获得文件输入流
        InputStream ins = file.getInputStream();
        // 获得文件类型
        String contentType = file.getContentType();
        // 将文件存储到mongodb中,mongodb 将会返回这个文件的唯一标识
        ObjectId objectId = gridFsTemplate.store(ins, fileName, contentType, metadata);
        return objectId.toHexString();
    }

    
    public String saveFile(InputStream inputStream, String fileName, Map<String, Object> metadata) {
        // 将文件存储到mongodb中,mongodb 将会返回这个文件的唯一标识
        ObjectId objectId = gridFsTemplate.store(inputStream, fileName, null, metadata);
        return objectId.toHexString();
    }

    
    public GridFsResource getFileOupById(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if (gridFSFile == null) {
            return null;
        }
        return gridFsTemplate.getResource(gridFSFile);
    }

    
    public GridFsResource getFileOupByFileId(String id) {
        Query query = Query.query(Criteria.where("_id").is(id));
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if (gridFSFile == null) {
            return null;
        }
        return gridFsTemplate.getResource(gridFSFile);
    }

    
    public List<GridFsResource> getFileListByQuery(List<String> ids) {
        Query query = Query.query(Criteria.where("_id").in(ids));
        GridFSFindIterable files = gridFsTemplate.find(query);
        List<GridFsResource> resources = new ArrayList<>();
        for (GridFSFile file : files) {
            resources.add(gridFsTemplate.getResource(file));
        }
        return resources;
    }

    
    public void downloadFileById(String id, HttpServletResponse response) throws IOException {
        Query query = Query.query(Criteria.where("_id").is(id));
        // 查询单个文件
        searchFileThenDownload(response, query);
    }

    
    public void downloadFileByName(String fileName, HttpServletResponse response) throws IOException {
        Query query = Query.query(Criteria.where("filename").is(fileName));
        searchFileThenDownload(response, query);
    }

    
    public GridFsResource getFileByName(String fileName) {
        Query query = Query.query(Criteria.where("filename").is(fileName));
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if (gridFSFile != null) {
            return gridFsTemplate.getResource(gridFSFile);
        }
        return null;
    }


    
    public void deleteFileIds(List<String> ids) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").in(ids)));
    }

    
    public void deleteFileId(String id) {
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is(id)));
    }

    private void searchFileThenDownload(HttpServletResponse response, Query query) throws IOException {
        // 查询单个文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(query);
        if (gridFSFile == null) {
            return;
        }
        GridFsResource resource = gridFsTemplate.getResource(gridFSFile);
        if (resource.exists()) {
            FileUtils.downloadFile(resource.getInputStream(), gridFSFile.getFilename(), response);
        }
    }
}
