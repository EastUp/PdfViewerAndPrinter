package com.east.pdfviewerandprinter.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SortedList;
import androidx.recyclerview.widget.SortedListAdapterCallback;

import com.east.east_utils.ui.recycleview.divider.RecyclerViewItemDecoration;
import com.east.east_utils.utils.DisplayUtil;
import com.east.pdfviewerandprinter.R;
import com.east.pdfviewerandprinter.view.adapter.FileAdapter;

import java.io.File;

/**
 * Created by mediate on 2016/7/6.
 * //上传固件时打开的文件管理器选中
 */
public class FileSelectActivity extends AppCompatActivity {
    public static final String EXTRA_FILE_PATH = "EXTRA_FILE_PATH";
    private static final String ARG_TITLE = "ARG_TITLE";

    RecyclerView fileListRecyclerView;
    FileAdapter fileAdapter;

    File currentDir;

    Handler mainHandler;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        setResult(RESULT_CANCELED);


        Intent intent = getIntent();

        currentDir = getRootDir();
        mainHandler = new Handler();

        fileAdapter = new FileAdapter();
        fileAdapter.setItemClickListener(new FileAdapter.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, File file, int itemType) {
                if(itemType == FileAdapter.VIEW_HOLDER_TYPE_PARENT_DIR){
                    if(currentDir == null || currentDir.getAbsolutePath().equals(Environment.getExternalStorageDirectory().getAbsolutePath())){
                        return;
                    }

                    File parentDir = currentDir.getParentFile();
                    if(parentDir != null) {
                        currentDir = parentDir;
                        fileAdapter.setFileList(loadFileList(currentDir));
                    }
                }else {
                    if(file == null){
                        return;
                    }

                    if(file.isFile()) {
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_FILE_PATH,file.getAbsolutePath());
                        setResult(RESULT_OK,intent);
                        finish();
                    } else if (file.isDirectory()) {
                        currentDir = file;
                        fileAdapter.setFileList(loadFileList(currentDir));
                    }
                }
            }
        });

        fileListRecyclerView = (RecyclerView) findViewById(R.id.activity_select_file_list);
        fileListRecyclerView.setHasFixedSize(true);
        fileListRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        fileListRecyclerView.addItemDecoration(new RecyclerViewItemDecoration.Builder(this)
                .color(ContextCompat.getColor(this,R.color.gray))
                .thickness(DisplayUtil.dip2px(this,0.5f))
                .create()
        );
        fileListRecyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onStart(){
        super.onStart();
        fileAdapter.setFileList(loadFileList(currentDir));
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    protected File getRootDir(){
        return Environment.getExternalStorageDirectory();
    }

    protected SortedList<File> loadFileList(File dir){
        File[] filesArray = dir == null ? null : dir.listFiles();
        final int cap = filesArray == null ? 0 : filesArray.length;

        SortedList<File> files = new SortedList<File>(File.class, new SortedListAdapterCallback<File>(fileAdapter) {
            @Override
            public int compare(File lhs, File rhs) {
                int ret = 0;
                if(lhs.isDirectory() && !rhs.isDirectory()){
                    ret = -1;
                }else if(rhs.isDirectory() && !lhs.isDirectory()){
                    ret = 1;
                }else {
                    ret = lhs.getName().compareToIgnoreCase(rhs.getName());
                }
                return ret;
            }
            @Override
            public boolean areContentsTheSame(File oldFile, File newFile) {
                return oldFile.getAbsolutePath().equals(newFile.getAbsolutePath()) && (oldFile.isFile() == newFile.isFile());
            }

            @Override
            public boolean areItemsTheSame(File item1, File item2) {
                return areContentsTheSame(item1, item2);
            }
        }, cap);

        files.beginBatchedUpdates();
        if(filesArray != null){
            for(File f: filesArray){
                if(!f.isHidden()){
                    files.add(f);
                }
            }
        }
        files.endBatchedUpdates();

        return files;
    }
}
