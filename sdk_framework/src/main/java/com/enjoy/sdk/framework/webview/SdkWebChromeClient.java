package com.enjoy.sdk.framework.webview;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.text.format.DateFormat;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.Toast;

import java.io.File;
import java.util.Calendar;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;



/**
 * User: Ranger
 * Date: 09/01/2018
 * Time: 2:31 PM
 */

public class SdkWebChromeClient extends WebChromeClient {
    private final static int IMAGE_CHOOSER_RESULT_CODE = 1272;
    private final static int REQUEST_CODE = 2333;

    private Context mContext;
    private ChromeCallBack mChromeCallback;

    private ValueCallback<Uri> uploadFile;
    private ValueCallback<Uri[]> uploadFiles;

    public SdkWebChromeClient(Context context, ChromeCallBack chromeCallBack) {
        this.mChromeCallback = chromeCallBack;
        this.mContext = context;
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        if (mChromeCallback != null) {
            mChromeCallback.onLoading(view, newProgress);
        }
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        if (mChromeCallback != null) {
            mChromeCallback.onReceivedTitle(view, title);
        }
    }

    @Override
    public void onReceivedIcon(WebView view, Bitmap icon) {
        if (mChromeCallback != null) {
            mChromeCallback.onReceiveIcon(view, icon);
        }
    }

    @Override
    public void onRequestFocus(WebView view) {
        if (mChromeCallback != null) {
            mChromeCallback.onRequestFocus(view);
        }
    }

    // For Android 3.0+
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
        this.uploadFile = uploadFile;
        openFileChooseProcess(true);
    }

    // For Android < 3.0
    public void openFileChooser(ValueCallback<Uri> uploadMsg) {
        this.uploadFile = uploadFile;
        openFileChooseProcess(true);
    }

    // For Android  > 4.1.1
    public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
        this.uploadFile = uploadFile;
        openFileChooseProcess(true);
    }

    // For Android  >= 5.0
    @Override
    public boolean onShowFileChooser(WebView webView,
                                     ValueCallback<Uri[]> filePathCallback,
                                     FileChooserParams fileChooserParams) {
        this.uploadFiles = filePathCallback;
        openFileChooseProcess(false);
        return true;
    }

    public void handleOnActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE) {
            // ????????????(1)???(2)??????????????????????????????????????????????????????????????????????????????????????????
            if (uploadFile != null) {
                chooseBelow(resultCode, data);
            } else if (uploadFiles != null) {
                chooseAbove(resultCode, data);
            } else {
                Toast.makeText(((Activity) mContext), "????????????", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void chooseBelow(int resultCode, Intent data) {

        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // ?????????????????????????????????
                Uri uri = data.getData();
                if (uri != null) {

                    uploadFile.onReceiveValue(uri);
                } else {
                    uploadFile.onReceiveValue(null);
                }
            } else {
                // ??????????????????????????????????????????????????????????????????data??????
                uploadFile.onReceiveValue(imageUri);
            }
        } else {
            uploadFile.onReceiveValue(null);
        }
        uploadFile = null;
    }
    private void chooseAbove(int resultCode, Intent data) {

        if (RESULT_OK == resultCode) {
            updatePhotos();

            if (data != null) {
                // ?????????????????????????????????????????????
                Uri[] results;
                Uri uriData = data.getData();
                if (uriData != null) {
                    results = new Uri[]{uriData};
                    uploadFiles.onReceiveValue(results);
                } else {
                    uploadFiles.onReceiveValue(null);
                }
            } else {
                uploadFiles.onReceiveValue(new Uri[]{imageUri});
            }
        } else {
            uploadFiles.onReceiveValue(null);
        }
        uploadFiles = null;
    }

    private void updatePhotos() {
        // ??????????????????????????????????????????????????????????????????????????????????????????????????????????????????
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        intent.setData(imageUri);
        ((Activity) mContext).sendBroadcast(intent);
    }

    private void openFileChooseProcess(boolean isDeFault) {
        if (isDeFault) {
            if (mContext != null && mContext instanceof Activity) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                ((Activity) mContext).startActivityForResult(Intent.createChooser(intent, "Image"), IMAGE_CHOOSER_RESULT_CODE);
            }
        } else {
            fileSelect();
        }
    }
    Uri imageUri;
    private void fileSelect() {
        // ?????????????????????????????????????????????
        String filePath = Environment.getExternalStorageDirectory() + File.separator
                + Environment.DIRECTORY_PICTURES + File.separator;
        String fileName = "IMG_" + DateFormat.format("yyyyMMdd_hhmmss", Calendar.getInstance(Locale.CHINA)) + ".jpg";
        imageUri = Uri.fromFile(new File(filePath + fileName));

        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        Intent Photo = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        Intent chooserIntent = Intent.createChooser(Photo, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

        ((Activity) mContext).startActivityForResult(chooserIntent, REQUEST_CODE);
    }

    private Uri getPathContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Images.Media._ID}, MediaStore.Images.Media.DATA + "=? ",
                new String[]{filePath}, null);
        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor.getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }

    public interface ChromeCallBack {

        public void onLoading(WebView view, int newProgress);

        public void onReceiveIcon(WebView view, Bitmap icon);

        public void onReceivedTitle(WebView view, String title);

        public void onRequestFocus(WebView view);

    }

}
