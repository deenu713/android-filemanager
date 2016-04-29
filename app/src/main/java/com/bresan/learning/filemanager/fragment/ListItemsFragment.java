package com.bresan.learning.filemanager.fragment;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bresan.learning.filemanager.activity.MainActivity;
import com.bresan.learning.filemanager.util.FileUtils;
import com.bresan.learning.filemanager.model.Item;
import com.bresan.learning.filemanager.R;
import com.bresan.learning.filemanager.adapter.ItemAdapter;
import com.bresan.learning.filemanager.async.LoadFileAsync;
import com.bresan.learning.filemanager.callback.OnLoadDoneCallback;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by rodrigobresan on 4/27/16.
 */
public class ListItemsFragment extends Fragment implements ItemAdapter.ClickListener {

    private static final String KEY_CURRENT_PATH = "current_path";
    private static final int SIZE_GRID = 4;

    private GridLayoutManager mGridLayoutManager;
    private RecyclerView mRecyclerItems;
    private ItemAdapter mItemAdapter;
    private List<Item> mItemList;
    private RelativeLayout mEmptyView;

    private String mCurrentPath = "";

    private ActionMode mActionMode;
    private ActionModeCallback mActionModeCallback = new ActionModeCallback();


    public static ListItemsFragment newInstance(String currentDir) {
        Bundle itemsBundle = new Bundle();
        itemsBundle.putString(KEY_CURRENT_PATH, currentDir);

        ListItemsFragment itemsFragment = new ListItemsFragment();
        itemsFragment.setArguments(itemsBundle);
        return itemsFragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        ((MainActivity) getActivity()).setCurrentFolderPath(mCurrentPath);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mItemList = new ArrayList<>();
        mCurrentPath = getArguments().getString(KEY_CURRENT_PATH);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View itemsView = inflater.inflate(R.layout.fragment_items, container, false);

        mGridLayoutManager = new GridLayoutManager(getContext(), 1);
        mRecyclerItems = (RecyclerView) itemsView.findViewById(R.id.recycler_view_items);
        mEmptyView = (RelativeLayout) itemsView.findViewById(R.id.layout_empty_view);

        mRecyclerItems.setLayoutManager(mGridLayoutManager);

        return itemsView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        loadDirectoryContentsAsync();
    }

    private void loadDirectoryContentsAsync() {
        new LoadFileAsync(mCurrentPath, new OnLoadDoneCallback() {
            @Override
            public void onLoadDone(List<Item> itemList) {
                mItemList = itemList;
                setRecyclerAdapter();
            }
        }).execute();
    }

    private void setRecyclerAdapter() {

        if (mItemList.isEmpty()) {
            mRecyclerItems.setVisibility(View.GONE);
            mEmptyView.setVisibility(View.VISIBLE);
        } else {
            mRecyclerItems.setVisibility(View.VISIBLE);
            mEmptyView.setVisibility(View.GONE);
        }

        mItemAdapter = new ItemAdapter(mItemList, this);
        mRecyclerItems.setAdapter(mItemAdapter);
        mItemAdapter.notifyDataSetChanged();

        mRecyclerItems.setItemAnimator(new DefaultItemAnimator());
    }

    private void openItem(Item selectedItem) {
        if (selectedItem.isDirectory()) {
            openDirectory(selectedItem);
        } else {
            openFile(selectedItem);
        }
    }

    private void openFile(Item selectedItem) {
        File file = new File(selectedItem.getPath());
        FileUtils.openFile(getContext(), file);
    }

    private void openDirectory(Item selectedItem) {
        ListItemsFragment listItemsFragment = ListItemsFragment.newInstance(selectedItem.getPath());

        getFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right)
                .replace(R.id.layout_content, listItemsFragment)
                .addToBackStack(mCurrentPath).commit();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            mGridLayoutManager.setSpanCount(SIZE_GRID);
            mRecyclerItems.setLayoutManager(mGridLayoutManager);
        } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            mGridLayoutManager.setSpanCount(1);
            mRecyclerItems.setLayoutManager(mGridLayoutManager);
        }
    }

    @Override
    public void onItemClicked(int position) {
        if (mActionMode != null) {
            toggleSelection(position);
        } else {

            Item currentItem = mItemList.get(position);
            openItem(currentItem);
        }
    }

    @Override
    public boolean onItemLongClicked(int position) {
        if (mActionMode == null) {
            mActionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(mActionModeCallback);
        }

        toggleSelection(position);

        return true;
    }

    private void toggleSelection(int position) {
        mItemAdapter.toggleSelection(position);

        int count = mItemAdapter.getSelectedItemCount();

        if (count == 0) {
            mActionMode.finish();
        } else {
            mActionMode.setTitle(String.valueOf(count));
            mActionMode.invalidate();
        }
    }

    private class ActionModeCallback implements ActionMode.Callback {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate (R.menu.toolbar_cab, menu);
            mItemAdapter.setActionModeEnabled(true);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.action_delete:

                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());

                    dialogBuilder.setTitle(R.string.dialog_delete_files_title);
                    dialogBuilder.setMessage(R.string.dialog_delete_files_message);

                    dialogBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            removeSelectedItems();
                            dialog.dismiss();
                            mode.finish();
                        }
                    });

                    dialogBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    dialogBuilder.show();
                    return true;

                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mItemAdapter.clearSelection();
            mActionMode = null;
            mItemAdapter.setActionModeEnabled(false);
        }
    }

    private void removeSelectedItems() {
        List<Integer> selectedItems = mItemAdapter.getSelectedItems();

        boolean successful = true;
        for (Integer currentPosition : selectedItems) {
            String currentFile = mItemList.get(currentPosition).getPath();
            boolean removed = FileUtils.deleteFile(currentFile);

            if (!removed) {
                successful = false;
            } else {
                Item currentItem = mItemList.get(currentPosition);
                mItemList.remove(currentItem);
            }
        }

        if (!successful) {
            Toast.makeText(getContext(), R.string.file_not_removed, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getContext(), R.string.file_removed, Toast.LENGTH_SHORT).show();
        }

        mItemAdapter.notifyDataSetChanged();

    }

}
