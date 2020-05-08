package com.ipant.utils;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.view.ContextThemeWrapper;
import android.widget.Toast;

import com.ipant.R;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ADMIN14 on 7/7/2017.
 */

public class Image_Picker {

    public static final int CAMERA_REQUEST = 100;
    public static final int GALLERY_REQUEST = 130;
    public static final int RESULT_PICK_CONTACT=140;
    public static final int REQUEST_ACESS_STORAGE = 3;
    public static final int REQUEST_ACESS_CAMERA = 2;
    public static String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};
    public static File IMAGE_PATH = null;


    private Uri uri;
    private Context mContext;
    public static final int PERMISSION_ALL = 134;

    public Image_Picker(Context context) {
        mContext = context;
    }


    public void startDialog() {
        AlertDialog.Builder myAlertDilog = new AlertDialog.Builder(mContext);


        String title="Alert";
        String message="Allow asked permissions to make this work. Click begin to proceed.";

        // Initialize a new foreground color span instance
        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(mContext.getResources().getColor(android.R.color.black));

        // Initialize a new spannable string builder instance
        SpannableStringBuilder ssBuilder_title = new SpannableStringBuilder(title);

        // Apply the text color span
        ssBuilder_title.setSpan(
                foregroundColorSpan,
                0,
                title.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Initialize a new spannable string builder instance
        SpannableStringBuilder ssBuilder_msg = new SpannableStringBuilder(message);

        // Apply the text color span
        ssBuilder_msg.setSpan(
                foregroundColorSpan,
                0,
                message.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        myAlertDilog.setTitle(ssBuilder_title);
        myAlertDilog.setMessage(ssBuilder_msg);
        myAlertDilog.setPositiveButton("Begin", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                checkPermission();
            }
        });

        AlertDialog dialog = myAlertDilog.create();
        dialog.show();

        dialog.getButton(android.support.v7.app.AlertDialog.BUTTON_POSITIVE).setTextColor(mContext.getResources().getColor(R.color.colorPrimary));

    }


    public boolean hasPermissions(Context context, String... permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }


    public void checkPermission() {
        if (!hasPermissions(mContext, PERMISSIONS)) {
            ActivityCompat.requestPermissions((Activity) mContext, PERMISSIONS, PERMISSION_ALL);
        }

    }

    public void checkGivenPermission(String... permissions) {
        if (!hasPermissions(mContext, permissions)) {
            ActivityCompat.requestPermissions((Activity) mContext, permissions, PERMISSION_ALL);
        }

    }


    public void takeImageFrom(int i){
        if(i==0){
            openCameraApp();
        }else{
            openGallery();
        }
    }


    private void openCameraApp() {
        Intent picIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE).addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);


        String file_path = Environment.getExternalStorageDirectory().toString() +
                "/" + mContext.getResources().getString(R.string.app_name);

        File dir = new File(file_path);
        if (!dir.exists())
            dir.mkdirs();
       // IMAGE_PATH = new File(dir, mContext.getResources().getString(R.string.app_name) + AppConstants.USER_ID + System.currentTimeMillis() + ".png");

        IMAGE_PATH = new File(dir, mContext.getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".png");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            picIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(mContext, mContext.getPackageName()+".fileprovider", IMAGE_PATH));
        }
        else {
            picIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(IMAGE_PATH));
        }

        ((Activity) mContext).startActivityForResult(picIntent, CAMERA_REQUEST);

    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setType("image/*");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        ((Activity) mContext).startActivityForResult(intent, GALLERY_REQUEST);
    }

    public void imageOptions() {

        final CharSequence[] opsChars = {"Take Picture", "Gallery"};
        new AlertDialog.Builder(new ContextThemeWrapper(mContext, R.style.DialogTheme))
                .setTitle("Select:")
                .setSingleChoiceItems(opsChars, 0, null)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        dialog.dismiss();
                        int selectedPosition = ((AlertDialog) dialog).getListView().getCheckedItemPosition();
                        // Do something useful withe the position of the selected radio button

                        if (selectedPosition == 0) {
                            openCameraApp();
                        } else if (selectedPosition == 1) {
                            openGallery();
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public String getRealPathFromURI(Uri contentURI) {

        String result;
        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


    public boolean checkURI(Uri contentURI) {

        String result;
        Cursor cursor = mContext.getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            return false;
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();

            if (result == null) {
                return false;
            }

            if (new File(result).exists()) {
                // do something if it exists
                return true;
            } else {
                return false;
            }
        }

    }


    public Bitmap getBitmapFromUri(Context context, Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        return image;
    }


    public Bitmap decodeBitmapURI(Context context, Uri uri, int imageWidth, int imageHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        try {
            BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
            // Calculate inSampleSize
            options.inSampleSize = calculateInSampleSize(options, imageWidth, imageHeight);

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeStream(context.getContentResolver().openInputStream(uri), null, options);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return null;
    }

   


    public String resizeImage(Context context, Uri uri){

        Bitmap b= decodeBitmapURI(context, uri, 700, 350);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 60, bytes);
        String file_path = Environment.getExternalStorageDirectory().toString() +
                "/" + context.getResources().getString(R.string.app_name);
        File dir = new File(file_path);
        if (!dir.exists()){
            dir.mkdirs();
        }

        File file = new File(dir, context.getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".png");
        try {
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();
            b.recycle();
            return file.getPath();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

    }


    public  boolean saveImage(Context context, Bitmap b){
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        b.compress(Bitmap.CompressFormat.PNG, 60, bytes);
        String file_path = Environment.getExternalStorageDirectory().toString() +
                "/" + context.getResources().getString(R.string.app_name);
        File dir = new File(file_path);
        if (!dir.exists()){
            dir.mkdirs();
        }
        File file = new File(dir, context.getResources().getString(R.string.app_name) + System.currentTimeMillis() + ".png");
        try {
            file.createNewFile();
            FileOutputStream fo = new FileOutputStream(file);
            fo.write(bytes.toByteArray());
            fo.flush();
            fo.close();


            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText((Activity)context,"Image saved successfully",Toast.LENGTH_LONG).show();
                }
            });

       //     b.recycle();
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

    }




    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }


    public Uri getImageUrlWithAuthority(Context context, Uri uri) {
        InputStream is = null;
        if (uri.getAuthority() != null) {
            try {
                is = context.getContentResolver().openInputStream(uri);
                Bitmap bmp = BitmapFactory.decodeStream(is);
                return writeToTempImageAndGetPathUri(context, bmp);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public Uri writeToTempImageAndGetPathUri(Context inContext, Bitmap inImage) {
      /*  ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.PNG, 70, bytes);*/
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }


    public void cropImage(Context mContext, Uri imageUri, int shape) {

        CropImage.ActivityBuilder activityBuilder = CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON);
        if (shape == 1) {
            activityBuilder.setCropShape(CropImageView.CropShape.RECTANGLE);
        } else {

            if (android.os.Build.VERSION.SDK_INT >= 28) {
                // only for gingerbread and newer versions
                activityBuilder.setCropShape(CropImageView.CropShape.RECTANGLE);
            }else{
                activityBuilder.setCropShape(CropImageView.CropShape.OVAL);
            }


        }
        Intent intent = activityBuilder
                .getIntent(mContext);

        ((Activity) mContext).startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }



    // Contact Picker

    public void contactPickerIntent() {

        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        ((Activity) mContext).startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);
    }





}
