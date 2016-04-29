package com.bresan.learning.filemanager.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bresan.learning.filemanager.R;
import com.bresan.learning.filemanager.util.FileUtils;
import com.bresan.learning.filemanager.util.SharedPreferencesUtils;

/**
 * Created by rodrigobresan on 4/27/16.
 */
public class SettingsActivity extends AppCompatActivity {

    private String mCurrentFolder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);
        initToolbar();

        LinearLayout layoutChangeFolder = (LinearLayout) findViewById(R.id.layout_change_folder);
        TextView txtCurrentFolder = (TextView) findViewById(R.id.txt_current_folder);

        mCurrentFolder = SharedPreferencesUtils.getDefaultFolder(getApplicationContext());
        txtCurrentFolder.setText("Current folder: " + mCurrentFolder);

        layoutChangeFolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initChangeFolderDialog();
            }
        });
    }

    private void initChangeFolderDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);

        LayoutInflater inflater = getLayoutInflater();

        View view = inflater.inflate(R.layout.dialog_change_folder, null);
        builder.setView(view);

        final EditText txtFolderPath = (EditText) view.findViewById(R.id.ed_folder_path);

        txtFolderPath.setText(mCurrentFolder);

        final TextView txtNewFolderInfo = (TextView) view.findViewById(R.id.txt_new_folder_info);
        txtFolderPath.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String currentFolder = s.toString();

                if (FileUtils.checkIfExists(currentFolder)) {
                    txtNewFolderInfo.setText(R.string.valid_path);
                } else {
                    txtNewFolderInfo.setText(R.string.invalid_path);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        builder.setTitle(R.string.dialog_title_change_default_folder);
        builder.setPositiveButton(R.string.dialog_done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String newPath = txtFolderPath.getText().toString();

                if (TextUtils.isEmpty(newPath)) {
                    Toast.makeText(getApplicationContext(), R.string.inform_the_path, Toast.LENGTH_SHORT).show();
                    return;
                }

                if (FileUtils.checkIfExists(newPath)) {
                    SharedPreferencesUtils.setDefaultFolder(getApplicationContext(), newPath);
                    Toast.makeText(getApplicationContext(), R.string.default_folder_changed, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.file_dont_exists, Toast.LENGTH_SHORT).show();
                }

                dialog.dismiss();
            }
        });

        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        builder.show();
    }

    private void initToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(true);
            setTitle(getString(R.string.title_settings));
        }
    }
}
