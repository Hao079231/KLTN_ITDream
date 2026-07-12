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

            // 1. Tự động xử lý separator cho cả Windows/Ubuntu
            Path targetPath = Paths.get(filePath);

            // 2. Xử lý linh hoạt: Nếu là đường dẫn tương đối thì merge với base path,
            // nếu đã là đường dẫn tuyệt đối (VD: /opt/uploads/...) thì giữ nguyên.
            if (!targetPath.isAbsolute()) {
                Path baseUploadPath = Paths.get(uploadDir, ITDreamConstant.DIRECTORY_GENERAL);
                targetPath = baseUploadPath.resolve(targetPath).normalize();
            } else {
                targetPath = targetPath.normalize();
            }

            log.info("======> Resolved full path: {}", targetPath);

            if (!Files.exists(targetPath)) {
                log.warn("======> Path not found: {}", targetPath);
                return;
            }

            // 3. Phân tích cấu trúc thư mục (Bottom-up)
            // Ví dụ: .../video/{accountId}/{folderId}/livestream.m3u8
            Path parent1 = targetPath.getParent(); // {folderId} hoặc {accountId}
            Path parent2 = parent1 != null ? parent1.getParent() : null; // {accountId} hoặc "video"
            Path parent3 = parent2 != null ? parent2.getParent() : null; // "video" hoặc thư mục cha của nó

            // Trường hợp 1: Cấu trúc .../video/{accountId}/{filename.mp4}
            // Lúc này parent2 chính là thư mục "video"
            if (parent2 != null && "video".equalsIgnoreCase(parent2.getFileName().toString())) {
                if (Files.isRegularFile(targetPath)) {
                    Files.delete(targetPath);
                    log.info("======> Deleted video file: {}", targetPath);
                }
                return;
            }

            // Trường hợp 2: Cấu trúc .../video/{accountId}/{folderId}/livestream.m3u8
            // Lúc này parent3 chính là thư mục "video"
            if (parent3 != null && "video".equalsIgnoreCase(parent3.getFileName().toString())) {
                log.info("======> Deleting livestream folder: {}", parent1);
                deleteDirectory(parent1); // Xóa thư mục {folderId} (chứa các file con)
                return;
            }

            // Trường hợp 3: Các loại file khác (ảnh, file doc,...)
            if (Files.isRegularFile(targetPath)) {
                Files.delete(targetPath);
                log.info("======> File deleted: {}", targetPath);
            } else {
                log.warn("======> Target is not a regular file: {}", targetPath);
            }

        } catch (Exception e) {
            log.error("======> Error deleting path: {}", filePath, e);
        }
    }

    private void deleteDirectory(Path path) throws IOException {
        if (path == null || !Files.exists(path)) return;

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
