package com.bresan.learning.filemanager.util;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.bresan.learning.filemanager.model.Item;

import java.io.File;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by rodrigobresan on 4/27/16.
 */
public class FileUtils {

    /**
     * This method is used to fetch all contents of a received directory.
     *
     * @param currentDir
     * @return
     */
    public static List<Item> getDirectoryContents(File currentDir) {

        // list all files from the current dir
        File[] dirs = currentDir.listFiles();

        List<Item> directoryList = new ArrayList<>();
        List<Item> fileList = new ArrayList<>();

        try {
            for (File currentFile : dirs) {

                Item currentItem;

                Date lastModified = new Date(currentFile.lastModified());
                DateFormat dateFormatter = DateFormat.getDateTimeInstance();

                String formattedDate = dateFormatter.format(lastModified);

                if (currentFile.isDirectory()) {
                    currentItem = getDataFromDirectory(currentFile);
                    currentItem.setDate(formattedDate);
                    directoryList.add(currentItem);
                } else {
                    currentItem = getDataFromFile(currentFile);
                    currentItem.setDate(formattedDate);
                    fileList.add(currentItem);
                }
            }
        } catch(Exception e) {
            Log.d("LOG", e.toString());
        }

        // sort both lists and then add the file list on directory list
        // this way the directories will be listed first and later the files
        Collections.sort(directoryList);
        Collections.sort(fileList);

        // show directories on top and files on bottom
        directoryList.addAll(fileList);

        return directoryList;
    }

    private static Item getDataFromDirectory(File directory) {
        Item directoryItem = new Item();

        File[] childsItems = directory.listFiles();
        int childDirs;

        if(childsItems != null) {
            childDirs = childsItems.length;
        } else {
            childDirs = 0;
        }

        String numItems = String.valueOf(childDirs);

        if (childDirs == 0) {
            numItems = numItems + " item";
        } else {
            numItems = numItems + " items";
        }

        directoryItem.setPath(directory.getAbsolutePath());
        directoryItem.setName(directory.getName());
        directoryItem.setIsDirectory(true);
        directoryItem.setData(numItems);

        return directoryItem;
    }

    private static Item getDataFromFile(File file) {
        Item fileItem = new Item();

        fileItem.setPath(file.getAbsolutePath());
        fileItem.setName(file.getName());
        fileItem.setIsDirectory(false);
        fileItem.setData(file.length() + " bytes"); // x Bytes

        return fileItem;
    }

    public static void openFile(Context context, File file) {
        String type = getFileType(file);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data = Uri.fromFile(file);

        intent.setDataAndType(data, type);
        context.startActivity(intent);
    }

    private static String getFileType(File file) {
        MimeTypeMap map = MimeTypeMap.getSingleton();
        String ext = MimeTypeMap.getFileExtensionFromUrl(file.getName());
        String type = map.getMimeTypeFromExtension(ext);

        if (type == null) {
            type = "*/*";
        }

        return type;
    }

    public static boolean checkIfExists(String filePath) {
        File file = new File(filePath);

        return file.exists();
    }

    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.delete();
    }
}
