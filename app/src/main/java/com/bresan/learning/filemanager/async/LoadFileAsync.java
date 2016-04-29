package com.bresan.learning.filemanager.async;

import android.os.AsyncTask;

import com.bresan.learning.filemanager.util.FileUtils;
import com.bresan.learning.filemanager.model.Item;
import com.bresan.learning.filemanager.callback.OnLoadDoneCallback;

import java.io.File;
import java.util.List;

/**
 * Created by rodrigobresan on 4/27/16.
 */
public class LoadFileAsync extends AsyncTask<Void, Void, List<Item>> {

    private OnLoadDoneCallback mCallbackDoneLoad;
    private String mFileDir;

    public LoadFileAsync(String dir, OnLoadDoneCallback callbackOnDone) {
        this.mCallbackDoneLoad = callbackOnDone;
        this.mFileDir = dir;
    }

    @Override
    protected List<Item> doInBackground(Void... params) {
        File currentDir = new File(mFileDir);
        List<Item> directoryContents = FileUtils.getDirectoryContents(currentDir);
        return directoryContents;
    }

    @Override
    protected void onPostExecute(List<Item> itemList) {
        super.onPostExecute(itemList);
        mCallbackDoneLoad.onLoadDone(itemList);
    }
}
