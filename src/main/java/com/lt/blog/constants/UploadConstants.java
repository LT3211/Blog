package com.lt.blog.constants;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Description: 图片上传路径常量
 */
@Component
public class UploadConstants {

    // TODO 这里注意路径，本地和prod的区别，只需要在配置文件中配好，linux部署的时候用prod.yml即可，不需要在改动代码
    // 用户头像默认上传路径
    public static String UPLOAD_AUTHOR_IMG = "/tmp/myblog/authorImg/";
    // 文章图片默认上传路径
    public static String FILE_UPLOAD_DIC = "/tmp/myblog/upload/";
    // 用户头像数据库路径
    public static String SQL_AUTHOR_IMG = "/Blog/authorImg/";
    // 文章图片数据库路径
    public static String FILE_SQL_DIC = "/Blog/upload/";


    @Value("${upload.uploadAuthorImg}")
    public void setUploadAuthorImg(String uploadAuthorImg) {
        com.lt.blog.constants.UploadConstants.UPLOAD_AUTHOR_IMG = uploadAuthorImg;
    }

    @Value("${upload.fileUploadDic}")
    public void setFileUploadDic(String fileUploadDic) {
        com.lt.blog.constants.UploadConstants.FILE_UPLOAD_DIC = fileUploadDic;
    }

    @Value("${upload.sqlAuthorImg}")
    public void setSqlAuthorImg(String sqlAuthorImg) {
        com.lt.blog.constants.UploadConstants.SQL_AUTHOR_IMG = sqlAuthorImg;
    }

    @Value("${upload.fileSqlDic}")
    public void setFileSqlDic(String fileSqlDic) {
        com.lt.blog.constants.UploadConstants.FILE_SQL_DIC = fileSqlDic;
    }
}
