package de.eldoria.shepard.util;

import com.google.api.client.util.IOUtils;
import de.eldoria.shepard.ShepardBot;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class FileHelper {
    public static File getFileFromURL(String url) {
        try {
            InputStream inputStream = new URL(url).openStream();
            String[] split = url.split("\\.");
            String suffix = split[split.length - 1];
            String[] urlSplitted = split[split.length - 2].split("\\\\");
            String name = urlSplitted[urlSplitted.length - 1];
            File tempFile = File.createTempFile(name, "." + suffix);
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            IOUtils.copy(inputStream, fileOutputStream);
            tempFile.deleteOnExit();

            return tempFile;
        } catch (IOException e) {
            ShepardBot.getLogger().error(e);
        }
        return null;
    }
}
