package com.amit.journal.csv.helper;

import com.amit.journal.interceptor.UserContext;
import com.amit.journal.util.CommonUtil;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;


public class CSVHelper {
	
	public static void saveFile(MultipartFile file, String uploadType) {
		try {
			String storeLocation = CommonUtil.getUploadDir();
			String fileName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
			Path fileLocation = Paths.get(storeLocation).toAbsolutePath().normalize();
			Path targetLocation = Paths.get(fileLocation.toString(), uploadType, UserContext.getUserId(), CommonUtil.getTodayDateString());
			if (!Files.exists(targetLocation))
				Files.createDirectories(targetLocation);
			targetLocation = targetLocation.resolve(fileName);
			Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
