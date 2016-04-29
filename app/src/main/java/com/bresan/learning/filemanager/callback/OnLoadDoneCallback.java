package com.bresan.learning.filemanager.callback;

import com.bresan.learning.filemanager.model.Item;

import java.util.List;

/**
 * Created by rodrigobresan on 4/27/16.
 */
public interface OnLoadDoneCallback {
    void onLoadDone(List<Item> itemList);
}
