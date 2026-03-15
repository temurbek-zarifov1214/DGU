package com.example.manualapp.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public final class FileUtils {

    public static String copyPdfToAppStorage(Context context, Uri sourceUri, String folderName) throws Exception {
        File dir = new File(context.getFilesDir(), "custom/" + folderName);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String baseName = "user_" + System.currentTimeMillis() + ".pdf";
        File dest = new File(dir, baseName);
        try (InputStream in = context.getContentResolver().openInputStream(sourceUri);
             FileOutputStream out = new FileOutputStream(dest)) {
            if (in == null) throw new Exception("Cannot open input stream");
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) > 0) {
                out.write(buf, 0, n);
            }
        }
        return dest.getAbsolutePath();
    }
}
