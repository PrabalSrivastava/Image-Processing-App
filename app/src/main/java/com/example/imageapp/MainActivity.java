package com.example.imageapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ImageView image;
    Button buttonLoadFromGallery;
    Button buttonLoadFromCamera;
    LinearLayout editFunctionLayout, otherFunctionLayout, getImageLayout;
    //edit layout
    Button  buttonUndo,buttonSave;
    ImageView imageView;
    Button buttonGrayScale,buttonNegative,buttonBlackAndWhite;
    Button buttonPixelGrayScale,buttonPixelNegative,buttonPixelBlackAndWhite;
    private static final int PICK_IMAGE = 100;
    private static final int CAMERA_REQUEST_CODE = 200;
    //private static final int MY_CAMERA_PERMISSION_CODE = 300;
    Bitmap bitmap, editedBitmap;
    private String path;
    private File imageFile;
    private String imageFileName;
    private File storageDir;
    private Uri imageURI;
    private String cameraFilePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        image=(ImageView)findViewById(R.id.image);
        getImageLayout=(LinearLayout)findViewById(R.id.getImageLayout);
        editFunctionLayout=(LinearLayout)findViewById(R.id.editFunctionLayout);
        otherFunctionLayout=(LinearLayout)findViewById(R.id.otherFunctionLayout);

        buttonLoadFromGallery = (Button)findViewById(R.id.buttonLoadFromGallery);
        buttonLoadFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });
        buttonLoadFromCamera = (Button)findViewById(R.id.buttonLoadFromCamera);
        buttonLoadFromCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureFromCamera();
            }
        });



    }

    private void setEditLayoutButtons() {

        buttonUndo = (Button) findViewById(R.id.buttonUndo);
        buttonUndo.setVisibility(View.INVISIBLE);
        buttonUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.setImageBitmap(bitmap);
                editedBitmap=bitmap;
            }
        });
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setVisibility(View.INVISIBLE);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Bitmap savingBitmap=editedBitmap;
                    savingBitmap=pasteWaterMark(savingBitmap);
                    saveImage(savingBitmap);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        });
        buttonNegative = (Button) findViewById(R.id.buttonNegative);
        buttonNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                image.setImageBitmap(bitmap);
                negativeImage();
                //changeImageUsingPixel(1);
            }
        });
        buttonGrayScale = (Button) findViewById(R.id.buttonGrayScale);
        buttonGrayScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                grayScaleImage();
                //changeImageUsingPixel(2);
            }
        });
        buttonBlackAndWhite=(Button)findViewById(R.id.buttonBlackAndWhite);
        buttonBlackAndWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                blackAndWhiteImage();
                //changeImageUsingPixel(3);
            }
        });
        buttonPixelNegative = (Button) findViewById(R.id.buttonPixelNegative);
        buttonPixelNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //negativeImage();
                changeImageUsingPixel(1);
            }
        });
        buttonPixelGrayScale = (Button) findViewById(R.id.buttonPixelGrayScale);
        buttonPixelGrayScale.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //grayScaleImage();
                changeImageUsingPixel(2);
            }
        });
        buttonPixelBlackAndWhite=(Button)findViewById(R.id.buttonPixelBlackAndWhite);
        buttonPixelBlackAndWhite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //blackAndWhiteImage();
                changeImageUsingPixel(3);
            }
        });
    }

    private void openGallery() {
        try {
            Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
            startActivityForResult(gallery, PICK_IMAGE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"X"+e.toString(),Toast.LENGTH_SHORT).show();
        }

    }

    private void captureFromCamera() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", createImageFile()));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.toString()+"Error: Permission Denied.\nProvide Storage Permission manually.",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE)
        {
            imageURI = data.getData();

        }
        else if (resultCode == RESULT_OK && requestCode == CAMERA_REQUEST_CODE)
        {
            imageURI=Uri.parse(cameraFilePath);
        }
        image.setImageURI(imageURI);
        setEditLayoutVisible();
        setEditLayoutButtons();
        convertUriToBitmap(imageURI);

    }

    private void convertUriToBitmap(Uri imageURI) {
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imageURI));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void setEditLayoutVisible() {
        getImageLayout.setVisibility(View.GONE);
        editFunctionLayout.setVisibility(View.VISIBLE);
        otherFunctionLayout.setVisibility(View.VISIBLE);

    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        //This is the directory in which the file will be created. This is the default location of Camera photos
        storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM), "Camera");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        // Save a file: path for using again
        cameraFilePath = "file://" + image.getAbsolutePath();
        return image;
    }

    private Bitmap pasteWaterMark(Bitmap savingBitmap) {
        String date=new Date().toString();
        String d=date.substring(0,19);
        int x=savingBitmap.getWidth(),y=savingBitmap.getHeight();
        Bitmap tempBitmap = Bitmap.createBitmap(savingBitmap.getWidth(), savingBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(tempBitmap);
        Paint paint = new Paint();
        canvas.drawBitmap(savingBitmap, 0, 0, paint);
        paint.setColor(Color.WHITE);
        paint.setTextSize(25);
        //canvas.rotate(-45);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText("[ "+d+" ]", (int)(x-x*0.4), (int)(y-y*0.065), paint);
        canvas.drawText("Editor: Prabal's ImageApp", (int)(x-x*0.42), (int)(y-y*0.035), paint);
        //canvas.rotate(45);
        return tempBitmap;
    }

    private void saveImage(Bitmap editedBitmap) throws FileNotFoundException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "PNG_"+"Edited_" + timeStamp + "_";
        File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"camera"); //Creates app specific folder
        //path.mkdirs();
        File imageFile = new File(path, imageFileName+".png"); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try{
            editedBitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
            out.flush();
            out.close();

            // Tell the media scanner about the new file so that it is
            // immediately available to the user.
            MediaScannerConnection.scanFile(getApplicationContext(),new String[] { imageFile.getAbsolutePath() }, null,new MediaScannerConnection.OnScanCompletedListener() {
                public void onScanCompleted(String path, Uri uri) {
                    Log.i("ExternalStorage", "Scanned " + path + ":");
                    Log.i("ExternalStorage", "-> uri=" + uri);
                }
            });
            Toast.makeText(getApplicationContext(),"New Image Saved!",Toast.LENGTH_SHORT).show();
        } catch(Exception e) {
        }
    }

    private void negativeImage() {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        editedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(editedBitmap);
        Paint paint = new Paint();
        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {
                        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                });
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0, 0, paint);
        image.setImageBitmap(editedBitmap);
        buttonUndo.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.VISIBLE);
    }

    private void grayScaleImage() {
        //for grayscale
        //int avg = (r+g+b)/3;
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        editedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(editedBitmap);
        Paint paint = new Paint();

        ColorMatrix matrixGrayscale = new ColorMatrix();
        matrixGrayscale.setSaturation(0);

        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {
                        -1.0f, 0.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, -1.0f, 0.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, -1.0f, 0.0f, 255.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f

                });
        matrixInvert.preConcat(matrixGrayscale);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0, 0, paint);
        image.setImageBitmap(editedBitmap);

        buttonUndo.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.VISIBLE);
    }
    private void blackAndWhiteImage() {
        int height = bitmap.getHeight();
        int width = bitmap.getWidth();

        editedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(editedBitmap);
        Paint paint = new Paint();

        ColorMatrix matrixGrayscale = new ColorMatrix();
        matrixGrayscale.setSaturation(0);

        ColorMatrix matrixInvert = new ColorMatrix();
        matrixInvert.set(new float[]
                {
                        1.0f, 0.0f, 0.0f, 0.0f, 0.0f, //for black and white
                        0.0f, 1.0f, 0.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 1.0f, 0.0f, 0.0f,
                        0.0f, 0.0f, 0.0f, 1.0f, 0.0f
                });
        matrixInvert.preConcat(matrixGrayscale);

        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrixInvert);
        paint.setColorFilter(filter);

        canvas.drawBitmap(bitmap, 0, 0, paint);
        image.setImageBitmap(editedBitmap);

        buttonUndo.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.VISIBLE);
    }


    private void changeImageUsingPixel(int option) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        editedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(editedBitmap);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setScale(.3f, .3f, .3f, 1.0f);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        canvas.drawBitmap(bitmap, 0, 0, paint);
        for (int y = 0; y < height; y++)
        {
            for (int x = 0; x < width; x++)
            {
                int p = bitmap.getPixel(x,y);
                int a = Color.alpha(p);
                int r = Color.red(p);
                int g = Color.green(p);
                int b = Color.blue(p);
                int newColor=0;
                switch (option)
                {
                    //Case 1 for Negative
                    case 1:
                        r = 255 - r;
                        g = 255 - g;
                        b = 255 - b;
                        newColor=Color.rgb(r,g,b);
                        break;
                    //Case 2 for GrayScale
                    case 2:
                        //r=b=g=(r+b+g)/3;
                        r=(int)(r*0.299);
                        g=(int)(r*0.587);
                        b=(int)(r*0.114);
                        newColor=Color.rgb(r+g+b,r+g+b,r+g+b);
                        break;
                    //Case 3 for BlackAndWhite
                    case 3:
                        int threshold=127;
                        if((r*0.7+g*0.2+b*0.1)<threshold)
                            newColor=Color.WHITE;
                        else
                            newColor=Color.BLACK;
                        break;
                    default:
                        break;
                }
                editedBitmap.setPixel(x, y, newColor);
            }
        }
        image.setImageBitmap(editedBitmap);

        buttonUndo.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.VISIBLE);
    }


}
