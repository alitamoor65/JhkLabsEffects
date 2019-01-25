package com.example.jhklabseffects;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;


public class MainActivity extends Activity {

    String TAG = "MAIN_ACT";

    private ImageView imgMain ;
    private static final int SELECT_PHOTO = 100;
    private Bitmap src;
    private Bitmap filteredImage;

    ThumbsAdapter thumbsAdapter;
    RecyclerView.LayoutManager layoutManager;
    RecyclerView recyclerView;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imgMain = (ImageView) findViewById(R.id.effect_main);
        src = BitmapFactory.decodeResource(getResources(), R.drawable.image);
        new SetupThumbs().execute(src);
    }

    @SuppressLint("StaticFieldLeak")
    class SetupThumbs extends AsyncTask<Bitmap,Void,List<DataModelFilters>>{

        @Override
        protected void onPreExecute() {
            showLoading("Please wait...");
        }

        @Override
        protected List<DataModelFilters> doInBackground(Bitmap... bitmaps) {

            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = false;
            options.inSampleSize = 4;
            Bitmap thumb = bitmaps[0];
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            thumb.compress(Bitmap.CompressFormat.PNG, 20, out);
            Bitmap decoded = BitmapFactory.decodeStream(new ByteArrayInputStream(out.toByteArray()),null,options);
            //Bitmap decoded = BitmapFactory.de
            ImageFilters imageFilters = new ImageFilters();
            return imageFilters.setupFilterThumbs(decoded);
        }

        @Override
        protected void onPostExecute(List<DataModelFilters> bitmaps) {
            recyclerView = findViewById(R.id.thumbsRecyclerViewID);
            layoutManager = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(new ThumbsAdapter(getApplicationContext(),bitmaps, new ThumbsAdapter.setFilterChooser() {
                @Override
                public void onFilterClicked(int position) {
                    new applyFilterNumber().execute(position);
                }
            }));
            hideLoading();
        }
    }

    class applyFilterNumber extends AsyncTask<Integer,Void,Bitmap>{

        @Override
        protected Bitmap doInBackground(Integer... ints) {
            ImageFilters imageFilters = new ImageFilters();
            return imageFilters.setFilter(ints[0],src);
        }

        @Override
        protected void onPreExecute() {
           showLoading("Applying filter");
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            filteredImage = bitmap;
            imgMain.setImageBitmap(bitmap);
            hideLoading();
        }
    }

    public  int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;

        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }



    public void buttonClicked(View v){

        Toast.makeText(this,"Processing...",Toast.LENGTH_SHORT).show();
        ImageFilters imgFilter = new ImageFilters();
        if(v.getId() == R.id.btn_pick_img){
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");
            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }else if(v.getId() == R.id.btn_del_img){
            if (filteredImage != null) {
                saveBitmap(filteredImage);
            }else {
                Toast.makeText(this, "No effect applied", Toast.LENGTH_SHORT).show();
            }
        }
        /*else if(v.getId() == R.id.effect_highlight)
            saveBitmap(imgFilter.applyHighlightEffect(src), "effect_highlight");
        else if(v.getId() == R.id.effect_black)
            saveBitmap(imgFilter.applyBlackFilter(src),"effect_black");
        else if(v.getId() == R.id.effect_boost_1)
            saveBitmap(imgFilter.applyBoostEffect(src, 1, 40),"effect_boost_1");
        else if(v.getId() == R.id.effect_boost_2)
            saveBitmap(imgFilter.applyBoostEffect(src, 2, 30),"effect_boost_2");
        else if(v.getId() == R.id.effect_boost_3)
            saveBitmap(imgFilter.applyBoostEffect(src, 3, 67),"effect_boost_3");
        else if(v.getId() == R.id.effect_brightness)
            saveBitmap(imgFilter.applyBrightnessEffect(src, 80),"effect_brightness");
        else if(v.getId() == R.id.effect_color_red)
            saveBitmap(imgFilter.applyColorFilterEffect(src, 255, 0, 0),"effect_color_red");
        else if(v.getId() == R.id.effect_color_green)
            saveBitmap(imgFilter.applyColorFilterEffect(src, 0, 255, 0),"effect_color_green");
        else if(v.getId() == R.id.effect_color_blue)
            saveBitmap(imgFilter.applyColorFilterEffect(src, 0, 0, 255),"effect_color_blue");
        else if(v.getId() == R.id.effect_color_depth_64)
            saveBitmap(imgFilter.applyDecreaseColorDepthEffect(src, 64),"effect_color_depth_64");
        else if(v.getId() == R.id.effect_color_depth_32)
            saveBitmap(imgFilter.applyDecreaseColorDepthEffect(src, 32),"effect_color_depth_32");
        else if(v.getId() == R.id.effect_contrast)
            saveBitmap(imgFilter.applyContrastEffect(src, 70),"effect_contrast");
        else if(v.getId() == R.id.effect_emboss)
            saveBitmap(imgFilter.applyEmbossEffect(src),"effect_emboss");
        else if(v.getId() == R.id.effect_engrave)
            saveBitmap(imgFilter.applyEngraveEffect(src),"effect_engrave");
        else if(v.getId() == R.id.effect_flea)
            saveBitmap(imgFilter.applyFleaEffect(src),"effect_flea");
        else  if(v.getId() == R.id.effect_gaussian_blue)
            saveBitmap(imgFilter.applyGaussianBlurEffect(src),"effect_gaussian_blue");
        else if(v.getId() == R.id.effect_gamma)
            saveBitmap(imgFilter.applyGammaEffect(src, 1.8, 1.8, 1.8),"effect_gamma");
        else if(v.getId() == R.id.effect_grayscale)
            saveBitmap(imgFilter.applyGreyscaleEffect(src),"effect_grayscale");
        else  if(v.getId() == R.id.effect_hue)
            saveBitmap(imgFilter.applyHueFilter(src, 2),"effect_hue");
        else if(v.getId() == R.id.effect_invert)
            saveBitmap(imgFilter.applyInvertEffect(src),"effect_invert");
        else if(v.getId() == R.id.effect_mean_remove)
            saveBitmap(imgFilter.applyMeanRemovalEffect(src),"effect_mean_remove");
        else if(v.getId() == R.id.effect_reflaction)
            saveBitmap(imgFilter.applyReflection(src),"effect_reflaction");
        else if(v.getId() == R.id.effect_round_corner)
            saveBitmap(imgFilter.applyRoundCornerEffect(src, 45),"effect_round_corner");
        else if(v.getId() == R.id.effect_saturation)
            saveBitmap(imgFilter.applySaturationFilter(src, 1),"effect_saturation");
        else if(v.getId() == R.id.effect_sepia)
            saveBitmap(imgFilter.applySepiaToningEffect(src, 10, 1.5, 0.6, 0.12),"effect_sepia");
        else if(v.getId() == R.id.effect_sepia_green)
            saveBitmap(imgFilter.applySepiaToningEffect(src, 10, 0.88, 2.45, 1.43),"effect_sepia_green");
        else if(v.getId() == R.id.effect_sepia_blue)
            saveBitmap(imgFilter.applySepiaToningEffect(src, 10, 1.2, 0.87, 2.1),"effect_sepia_blue");
        else if(v.getId() == R.id.effect_smooth)
            saveBitmap(imgFilter.applySmoothEffect(src, 100),"effect_smooth");
        else if(v.getId() == R.id.effect_sheding_cyan)
            saveBitmap(imgFilter.applyShadingFilter(src, Color.CYAN),"effect_sheding_cyan");
        else if(v.getId() == R.id.effect_sheding_yellow)
            saveBitmap(imgFilter.applyShadingFilter(src, Color.YELLOW),"effect_sheding_yellow");
        else if(v.getId() == R.id.effect_sheding_green)
            saveBitmap(imgFilter.applyShadingFilter(src, Color.GREEN),"effect_sheding_green");
        else if(v.getId() == R.id.effect_tint)
            saveBitmap(imgFilter.applyTintEffect(src, 100),"effect_tint");
        else if(v.getId() == R.id.effect_watermark)
            saveBitmap(imgFilter.applyWaterMarkEffect(src, "Soft Logix", 200, 200, Color.GREEN, 80, 24, false),"effect_watermark");
*/
    }

    private void saveBitmap(Bitmap bmp){
        try {
            //imgMain.setImageBitmap(bmp);
            File f = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/helloImage.png");
            FileOutputStream fos = new FileOutputStream(f);
            bmp.compress(Bitmap.CompressFormat.PNG,90,fos);
            Log.i(TAG, "saveBitmap: " + f.getAbsolutePath());
        }
        catch(Exception ex){
            ex.printStackTrace();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case SELECT_PHOTO:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = data.getData();
                    Bitmap bmp = decodeUri(selectedImage);
                    if(bmp !=null){
                        src = bmp;
                        imgMain.setImageBitmap(src);
                        new SetupThumbs().execute(bmp);
                    }
                }
        }
    }

    private Bitmap decodeUri(Uri selectedImage)  {

        try {

        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 400;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp / 2 < REQUIRED_SIZE
                    || height_tmp / 2 < REQUIRED_SIZE) {
                break;
            }
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(getContentResolver().openInputStream(selectedImage), null, o2);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
    protected void showLoading(@NonNull String message) {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage(message);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialog.setCancelable(false);
        mProgressDialog.show();
    }

    protected void hideLoading() {
        if (mProgressDialog != null) {
            mProgressDialog.dismiss();
        }
    }
}
