package com.example.libraryland;

import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class CloudinaryUploader {
    public interface UploadCallback {
        void onSuccess(String imageUrl);
        void onError(String errorMessage);
    }

    public static void uploadImage(File imageFile, UploadCallback callback) {
        new AsyncTask<Void, Void, String>() {
            private String errorMessage = null;

            @Override
            protected String doInBackground(Void... voids) {
                try {
                    Map response = CloudinaryConfig.getInstance().uploader()
                            .upload(imageFile, ObjectUtils.emptyMap());
                    return response.get("url").toString();
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    Log.e("CloudinaryUploader", "Upload error", e);
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String imageUrl) {
                if (imageUrl != null) {
                    callback.onSuccess(imageUrl);
                } else {
                    callback.onError(errorMessage);
                }
            }
        }.execute();
    }
    public static void deleteImage(String imageUrl, UploadCallback callback) {
        try {
            // Récupérer l'ID public de l'image depuis l'URL
            String publicId = extractPublicIdFromUrl(imageUrl);

            // Créez un objet Cloudinary avec vos configurations
            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "your_cloud_name");
            config.put("api_key", "your_api_key");
            config.put("api_secret", "your_api_secret");

            Cloudinary cloudinary = new Cloudinary(config);

            // Effectuer la suppression de l'image
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            callback.onSuccess("Image deleted successfully");
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }
    private static String extractPublicIdFromUrl(String imageUrl) {
        // Extraire l'ID public de l'image à partir de son URL (en fonction de votre structure URL)
        String[] parts = imageUrl.split("/");
        String fileName = parts[parts.length - 1];
        return fileName.split("\\.")[0]; // Supposons que l'ID public est avant l'extension
    }

}

