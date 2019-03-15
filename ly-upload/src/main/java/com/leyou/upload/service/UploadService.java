package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.utils.enums.ExceptionEnums;
import com.leyou.common.utils.exception.LyException;
import com.leyou.upload.config.FastClientImporter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * @version V1.0
 * @ClassName:${file_name}
 * @Description:${todo}(用一句话描述该文件做什么)
 * @author:cxg
 * @Date:${time}
 */
@Service
@Slf4j
public class UploadService {
    @Autowired
    private FastFileStorageClient fileStorageClient;
    //定义文件类型
    private static final List<String> ALLOW_TYPES = Arrays.asList("image/png", "image/jpeg","image/bmp");

    public String uploadImage(MultipartFile file) {
        try {
            //校验文件类型
            String contentType=file.getContentType();//获取文件类型
            if (!ALLOW_TYPES.contains(contentType)){
                throw new LyException(ExceptionEnums.Invalid_file_type);
            }
            //校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image==null){
                throw new LyException(ExceptionEnums.Invalid_file_type);
            }
            // 2、将图片上传到FastDFS
            // 2.1、获取文件后缀名
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath storePath = fileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);
            //返回路径
            return "http://image.leyou.com/" + storePath.getFullPath();
        } catch (IOException e) {
            log.error("上传文件失败",e);
            throw  new LyException(ExceptionEnums.UPLOAD_FILE_ERROR);
        }

    }
}
