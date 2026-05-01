package com.example.libraryland;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

public class CloudinaryConfig {
    private static Cloudinary cloudinary;

    public static Cloudinary getInstance() {
        if (cloudinary == null) {
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dsjjuqlla");
            config.put("api_key", "437495691156414");
            config.put("api_secret", "O3cxVm7-FWEW08G320PfIu0e5RI");

            cloudinary = new Cloudinary(config);
        }
        return cloudinary;
    }
}

