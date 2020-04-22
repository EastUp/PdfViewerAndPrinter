package com.east.pdfviewerandprinter;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintManager;
import android.text.TextUtils;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.east.east_utils.utils.ToastUtils;
import com.east.east_utils.utils.click.ThrottleClickUtils;
import com.east.east_utils.utils.permission.PermissionCheckUtils;
import com.east.east_utils.utils.permission.PermissionListener;
import com.east.pdfviewerandprinter.printeradapter.MyPrintPdfAdapter;
import com.east.pdfviewerandprinter.view.FileSelectActivity;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    PDFView pdfView; //pdf预览View
    String pdfPath; //pdf文件全路径
    Button selectFile; //选择文件
    Button printer; //打印文件
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PermissionCheckUtils.INSTANCE.checkPermission(this, new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_EXTERNAL_STORAGE
        }, new PermissionListener() {
            @Override
            public void onGranted() {

            }

            @Override
            public void onCancel() {
                finish();
            }
        });
        pdfView = findViewById(R.id.pdfView);
        selectFile = findViewById(R.id.selectFile);
        printer = findViewById(R.id.printer);
        ThrottleClickUtils
                .bind(selectFile)
                .throttleClick(v -> startActivityForResult(new Intent(MainActivity.this, FileSelectActivity.class), 10));
        ThrottleClickUtils
                .bind(printer)
                .throttleClick(v -> {
                    if(TextUtils.isEmpty(pdfPath)){
                        ToastUtils.show("请选择pdf文件");
                        return;
                    }
                    onPrintPdf(pdfPath);
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void onPrintPdf(String filePath) {
        PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
        PrintAttributes.Builder builder = new PrintAttributes.Builder();
        builder.setColorMode(PrintAttributes.COLOR_MODE_COLOR);
        printManager.print("test pdf print",
                /*new MyPrintAdapter(this,filePath)*/ new MyPrintPdfAdapter(filePath),
                builder.build());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10 && resultCode == RESULT_OK) {
            pdfPath = data.getStringExtra(FileSelectActivity.EXTRA_FILE_PATH);
            File pdf = new File(pdfPath);
            pdfView.fromFile(pdf).load();
        }
    }
}
