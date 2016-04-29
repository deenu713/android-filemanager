package com.bresan.learning.filemanager.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeTransform;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bresan.learning.filemanager.R;
import com.bresan.learning.filemanager.adapter.CustomSpinnerAdapter;
import com.bresan.learning.filemanager.fragment.ListItemsFragment;
import com.bresan.learning.filemanager.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 0;

    private String mCurrentFolder = "/";

    private ArrayList<String> listItemsNavigation = new ArrayList<>();
    private CustomSpinnerAdapter spinnerAdapter;
    private Spinner spinnerNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setAllowReturnTransitionOverlap(true);
            getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
            getWindow().setSharedElementExitTransition(new ChangeTransform());
        }

        setContentView(R.layout.activity_main);

        requestLocationPermission();

        initToolbar();
        initNavigatorSpinner();
        initFloatingActionButton();

        mCurrentFolder = fetchDefaultFolderPath();
        initFileListFragment();
    }

    private String fetchDefaultFolderPath() {
        return SharedPreferencesUtils.getDefaultFolder(getApplicationContext());
    }

    public void setCurrentFolderPath(String newFolder) {
        mCurrentFolder = newFolder;
        loadNavigationSpinner();
    }

    private void loadNavigationSpinner() {
        String[] pathList = mCurrentFolder.split("/");
        listItemsNavigation.clear();

        // copy the list of dirs to our list
        Collections.addAll(listItemsNavigation, pathList);

        if (listItemsNavigation.isEmpty()) {
            listItemsNavigation.add(getString(R.string.root_folder));
        } else {
            listItemsNavigation.set(0, getString(R.string.root_folder));
        }

        spinnerNavigation.setSelection(pathList.length);
        spinnerAdapter.notifyDataSetChanged();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    private void initNavigatorSpinner() {

        listItemsNavigation = new ArrayList<>();

        spinnerAdapter = new CustomSpinnerAdapter(getApplicationContext(), listItemsNavigation);
        spinnerNavigation = (Spinner) findViewById(R.id.spinner_navigator);
        spinnerNavigation.setAdapter(spinnerAdapter);

        // need to post it as a Runnable, otherwise it will issues on the listener callback for
        // onItemClickListener
        spinnerNavigation.post(new Runnable() {
            public void run() {
                spinnerNavigation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        navigateToDirectoryAt(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            }
        });
    }

    private void navigateToDirectoryAt(int selectedPosition) {
        int addedFragmentsCount = listItemsNavigation.size();
        int fragmentsToRemove = addedFragmentsCount - selectedPosition;

        for (int i = 0; i < fragmentsToRemove - 1; i++) {
            getSupportFragmentManager().popBackStack();
        }

    }

    private void initFloatingActionButton() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Hey guys! :-)\nMade by Rodrigo Bresan (rcbresan@gmail.com)", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        });
    }

    private void initFileListFragment() {
        ListItemsFragment listItemsFragment = ListItemsFragment.newInstance(mCurrentFolder);
        getSupportFragmentManager().beginTransaction().replace(R.id.layout_content, listItemsFragment).commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                startSettingsActivity();
                return true;
            case R.id.action_refresh:
                refreshItems();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void refreshItems() {
        initFileListFragment();
    }

    private void startSettingsActivity() {
        Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
        startActivity(intentSettings);
    }

    private void requestLocationPermission() {
        if (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_CODE_STORAGE_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_STORAGE_PERMISSION: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions allowed
                    Toast.makeText(getApplicationContext(), R.string.permissions_allowed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.permissions_not_allowed, Toast.LENGTH_LONG).show();
                }

                return;
            }
        }
    }


}
