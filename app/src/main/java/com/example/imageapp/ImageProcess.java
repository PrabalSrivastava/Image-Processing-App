package com.example.imageapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ImageProcess extends AppCompatActivity {

    Button  buttonUndo,buttonSave;
    Bitmap bitmap,editedBitmap;
    ImageView imageView;
    Button buttonGrayScale,buttonNegative,buttonBlackAndWhite;
    Button buttonPixelGrayScale,buttonPixelNegative,buttonPixelBlackAndWhite;
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_process);
        //byte[] bytes = getIntent().getByteArrayExtra("bitmapbytes");
        File imgFile=getIntent().getParcelableExtra("bitmapbytes");
        /*Log.d("My",path);
        Uri capturedImage=Uri.parse(path);
        try {
            bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(capturedImage));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }*/
        //File imgFile = new  File(path);

        if(imgFile.exists()){

            bitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

            //ImageView myImage = (ImageView) findViewById(R.id.imageviewTest);

            //myImage.setImageBitmap(myBitmap);

        }
        //bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.setVisibility(View.VISIBLE);
        //imageView.setImageURI(Uri.fromFile(new File(path)));
        imageView.setImageBitmap(bitmap);
        buttonUndo = (Button) findViewById(R.id.buttonUndo);
        buttonUndo.setVisibility(View.INVISIBLE);
        buttonUndo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageBitmap(bitmap);
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
        imageView.setImageBitmap(editedBitmap);
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
        imageView.setImageBitmap(editedBitmap);

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
        imageView.setImageBitmap(editedBitmap);

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
        imageView.setImageBitmap(editedBitmap);

        buttonUndo.setVisibility(View.VISIBLE);
        buttonSave.setVisibility(View.VISIBLE);
    }
}
//waste methods
/*public void openImageProcessActivity() {

        Intent intent = new Intent(this, ImageProcess.class);
        //ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try {

            //Boolean b=bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            //Log.i("ExternalStorage", "Scanned "+ b + ":");
            //byte[] bytes = stream.toByteArray();
            //String p=path.get(2)+".jpg";
            intent.putExtra("bitmapbytes",imageFile );

            startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),e.toString()+"Error: High Quality images not suppported.\nSet camera to 1M pixel.",Toast.LENGTH_SHORT).show();
        }

    }
*/
/*private void saveImage() throws FileNotFoundException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        imageFileName = "Temp_" + timeStamp + "_";
        //File path = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),"camera"); //Creates app specific folder
        //path.mkdirs();
        imageFile = new File(storageDir, imageFileName+".png"); // Imagename.png
        FileOutputStream out = new FileOutputStream(imageFile);
        try{
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // Compress Image
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
    }*/