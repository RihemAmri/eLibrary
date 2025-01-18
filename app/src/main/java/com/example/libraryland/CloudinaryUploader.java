package com.example.libraryland;

import android.os.AsyncTask;
import android.util.Log;

import com.cloudinary.utils.ObjectUtils;

import java.io.File;
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
}

