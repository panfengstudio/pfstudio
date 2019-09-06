package top.ayang818.pfstudio.util;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyuncs.OssAcsRequest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.Date;

public class AliCloudOssServeUtil {
    private static String endpoint = "https://oss-cn-beijing.aliyuncs.com";
    private static String accessKeyId = "LTAI4FcconyC2qQUGgPy6Hx4";
    private static String accessKeySecret = "P2IJThC2mmlKuIo3fkLdo8kecLUcFH";

    private static String bucketName = "upload-serve";

    private static  AliCloudOssServeUtil aliCloudOssServeUtil;
    private static OSS ossClient;

    private AliCloudOssServeUtil() {
        ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
    }

    // 使用单例模式user，创建阿里云的oss连接
    public static AliCloudOssServeUtil getInstance() {
        if (ossClient == null) {
            synchronized (AliCloudOssServeUtil.class) {
                if (aliCloudOssServeUtil == null ) {
                    aliCloudOssServeUtil = new AliCloudOssServeUtil();
                    return aliCloudOssServeUtil;
                }
            }
        }
        return aliCloudOssServeUtil;
    }

    public void destory() {
        ossClient.shutdown();
    }

    public void uploadImage(String filedir, String filepath) {
        try {
            File file = new File(filepath);
            String[] filenameList = filepath.split("/");
            String filePath = filedir + "/" + filenameList[filenameList.length - 1];
            FileInputStream fileInputStream;
            fileInputStream = new FileInputStream(file);
            this.uploadFileToOss(fileInputStream, filePath);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void uploadImage(File file, String filedir) {
        String filename = file.getName();
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            String filepath = filedir + "/" + filename;
            this.uploadFileToOss(fileInputStream, filepath);
        } catch (FileNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    public void uploadFileToOss(FileInputStream fileInputStream, String imagePath) {
        ossClient.putObject(bucketName, imagePath, fileInputStream);
        System.out.println("upload success!");
    }


    public String getUrl(String filePath) {
        // 设置url过期时间
        Date expiration = new Date(new Date().getTime() + 3600L * 1000 * 24 * 365 * 10);
        URL url = ossClient.generatePresignedUrl(bucketName, filePath, expiration);
        if (url != null) {
            String urlString = url.toString();
            return urlString;
        }
        return null;
    }
}
