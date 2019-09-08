package com.example.fileexplore;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.webkit.MimeTypeMap;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.example.fileexplore.Utils.getMimeType;

public class FileExplore extends AppCompatActivity {

    private static final int REQUEST_PERMISSION_KEY = 0;
    private final String PERMISSIONS[] = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private FileRecyclerAdapter adapter;
    private List<FileItem> mAdapterList = new ArrayList<>();

    private File path = new File(Environment.getExternalStorageDirectory() + "");

    // Check if the first level of the directory structure is the one showing
    private Boolean firstLvl = true;
    private FileItem[] fileList;
    private String chosenFile;
    private List<String> chosenFileList = new ArrayList<>();


    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        setUpViews();

    }

    private void setUpViews() {
        RecyclerView contentRecyclerView = findViewById(R.id.content_recycler_view);
        contentRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        contentRecyclerView.setHasFixedSize(true);
        adapter = new FileRecyclerAdapter(mAdapterList, new BaseRecyclerAdapter.RecyclerClickListener() {
            @Override
            public void onClickAction(View view) {


                int position = (int) view.getTag();

                chosenFile = mAdapterList.get(position).getFile();
                File sel = new File(path + "/" + chosenFile);
                if (sel.isDirectory()) {
                    firstLvl = false;

                    // Adds chosen directory to list
                    chosenFileList.add(chosenFile);
                    fileList = null;
                    path = new File(sel + "");

                    loadFileList();

                }else if (chosenFile.equalsIgnoreCase("up") && !sel.exists()) {

                    goBack();

                }else {
                    openFile(Uri.fromFile(sel),sel.getPath());
                }

                adapter.notifyDataSetChanged();
            }
        });
        contentRecyclerView.setAdapter(adapter);
    }

    private void goBack(){
        // present directory removed from list

        String s = chosenFileList.remove(chosenFileList.size() - 1);

        // path modified to exclude present directory
        path = new File(path.toString().substring(0,
                path.toString().lastIndexOf(s)));
        fileList = null;

        // if there are no more directories in the list, then
        // its the first level
        if (chosenFileList.isEmpty()) {
            firstLvl = true;
        }
        loadFileList();
    }

    //Fires intents to handle files of known mime types.
    private void openFile(Uri uri,String filePath) {

        String mimeType = getMimeType(filePath);

        if (mimeType != null) { //we have determined a mime type and can probably handle the file.
            try {

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                intent.setType(mimeType);
                startActivity(Intent.createChooser(intent,"Choose App"));
            } catch (ActivityNotFoundException e) {
                /*If we have figured out the mime type of the file, but have no application installed
                to handle it, send the user a message.
                 */
                Utils.showToast(this,"The System understands this file type," +
                        "but no applications are installed to handle it.");
            }
        } else {
            Utils.showToast(this,"System doesn't know how to handle that file type!");
        }
    }

    private void loadFileList() {

        try {
            path.mkdirs();
        } catch (SecurityException e) {
            Utils.showToast(this, "path does not exist");
        }

        // Checks whether path exists
        if (path.exists()) {
            FilenameFilter filter = new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    File sel = new File(dir, filename);
                    // Filters based on whether the file is hidden or not
                    return (sel.isFile() || sel.isDirectory())
                            && !sel.isHidden();

                }
            };

            String[] fList = path.list(filter);
            fileList = new FileItem[fList.length];
            for (int i = 0; i < fList.length; i++) {
                File sel = new File(path + "/" + fList[i]);
                fileList[i] = new FileItem(fList[i],sel.isDirectory());

            }

//            if (!firstLvl) {
//                FileItem temp[] = new FileItem[fileList.length + 1];
//                for (int i = 0; i < fileList.length; i++) {
//                    temp[i + 1] = fileList[i];
//                }
//                temp[0] = new FileItem("Up",true);
//                fileList = temp;
//            }
        } else {
            Utils.showToast(this, "path does not exist");
        }

        mAdapterList.clear();
        Collections.addAll(mAdapterList, fileList);
        adapter.notifyDataSetChanged();

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!Utils.hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, REQUEST_PERMISSION_KEY);
        } else {

            loadFileList();

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_PERMISSION_KEY:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    loadFileList();
                } else {
                    Utils.showToast(this, "You must accept permissions for app to function properly");
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {

        if (!firstLvl){
            goBack();
        }else {
            super.onBackPressed();
        }


    }
}