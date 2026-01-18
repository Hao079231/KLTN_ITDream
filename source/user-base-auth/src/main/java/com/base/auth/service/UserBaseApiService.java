package com.base.auth.service;

import com.base.auth.constant.ITDreamConstant;
import com.base.auth.model.Permission;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
public class UserBaseApiService {
    @Value("${file.upload-dir}")
    private String uploadDir;

    @Autowired
    UserBaseOTPService userBaseOTPService;

    @Autowired
    CommonAsyncService commonAsyncService;

    private Map<String, Long> storeQRCodeRandom = new ConcurrentHashMap<>();

    public void deleteByFilePath(String filePath) {
        try {
            if (filePath == null || filePath.trim().isEmpty()) {
                log.warn("======> Empty path provided, skip delete");
                return;
            }

            // Chuẩn hóa separator cho mọi OS
            String normalizedPath = filePath.replace("\\", "/");

            // Bỏ dấu / đầu nếu có
            if (normalizedPath.startsWith("/")) {
                normalizedPath = normalizedPath.substring(1);
            }

            Path relativePath = Paths.get(normalizedPath);

            if (relativePath.getNameCount() < 2) {
                log.warn("======> Invalid path format: {}", filePath);
                return;
            }

            String rootFolder = relativePath.getName(0).toString();

            Path baseUploadPath = Paths.get(
                uploadDir,
                ITDreamConstant.DIRECTORY_GENERAL
            );

            Path fullPath = baseUploadPath.resolve(relativePath).normalize();

            log.info("======> Resolved full path: {}", fullPath);

            // Nếu đường dẫn đến video thì xóa folder chứa video
            if ("video".equalsIgnoreCase(rootFolder)) {

                // folder chứa file video (parent của file)
                Path videoFolder = fullPath.getParent();

                if (videoFolder != null && Files.exists(videoFolder)) {
                    log.info("======> Deleting video folder: {}", videoFolder);
                    deleteDirectory(videoFolder);
                } else {
                    log.warn("======> Video folder not found: {}", videoFolder);
                }
                return;
            }

            // Nếu đường dẫn chứa ảnh, file thì sẽ xóa ảnh, file
            if (Files.exists(fullPath) && Files.isRegularFile(fullPath)) {
                Files.delete(fullPath);
                log.info("======> File deleted: {}", fullPath);
            } else {
                log.warn("======> File not found or not a file: {}", fullPath);
            }

        } catch (Exception e) {
            log.error("======> Error deleting path: {}", filePath, e);
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        Files.walk(path)
            .sorted(Comparator.reverseOrder())
            .map(Path::toFile)
            .forEach(File::delete);
    }
    
    public String getRequestOTP(){
        return userBaseOTPService.generate(6);
    }

    public synchronized Long getOrderHash(){
        return Long.parseLong(userBaseOTPService.generate(9))+System.currentTimeMillis();
    }


    public void sendEmail(String email, String msg, String subject, boolean html){
        commonAsyncService.sendEmail(email,msg,subject,html);
    }


    public String convertGroupToUri(List<Permission> permissions){
        if(permissions!=null){
            StringBuilder builderPermission = new StringBuilder();
            for(Permission p : permissions){
                builderPermission.append(p.getAction().trim().replace("/v1","")+",");
            }
            return  builderPermission.toString();
        }
        return null;
    }

    public String getOrderStt(Long storeId){
        return userBaseOTPService.orderStt(storeId);
    }


    public synchronized boolean checkCodeValid(String code){
        //delelete key has valule > 60s
        Set<String> keys = storeQRCodeRandom.keySet();
        Iterator<String> iterator = keys.iterator();
        while(iterator.hasNext()){
            String key = iterator.next();
            Long value = storeQRCodeRandom.get(key);
            if((System.currentTimeMillis() - value) > 60000){
                storeQRCodeRandom.remove(key);
            }
        }

        if(storeQRCodeRandom.containsKey(code)){
            return false;
        }
        storeQRCodeRandom.put(code,System.currentTimeMillis());
        return true;
    }
}
