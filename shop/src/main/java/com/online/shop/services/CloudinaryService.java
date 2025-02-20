package com.online.shop.services;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
public class CloudinaryService {
    private Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    public List<String> uploadFile(MultipartFile file, String folderName) {
        try {
            File convFile = new File(System.getProperty("java.io.tmpdir") + "/" + file.getOriginalFilename());
            file.transferTo(convFile);

            HashMap<Object, Object> options = new HashMap<>();
            options.put("folder", folderName);

            Map uploadedFile = cloudinary.uploader().upload(convFile, options);
            String publicId = (String) uploadedFile.get("public_id");

            convFile.delete();
            
            return new ArrayList<>(Arrays.asList(cloudinary.url().secure(true).generate(publicId),publicId));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public void deleteFile(String publicId) {
    	try {
    		Map result=cloudinary.uploader().destroy(publicId,ObjectUtils.emptyMap());
    		System.out.println("\n\nrezultat stergere: "+result+"\n\n\n");
    	}catch (Exception e) {
            throw new RuntimeException("Eroare la ștergerea imaginii: " + e.getMessage());
        }
    }
}