package com.example.dzoli.fingerpaint;

import android.Manifest;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.util.UUID;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private CanvasView canvasView;
    private ImageButton mSelectedColor, drawButton, eraseButton, newButton, saveButton;
    private float smallBrush, mediumBrush, largeBrush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        canvasView = (CanvasView)findViewById(R.id.canvas);
        LinearLayout paintLayout = (LinearLayout)findViewById(R.id.paint_colors);
        mSelectedColor = (ImageButton)paintLayout.getChildAt(0);
        mSelectedColor.setImageDrawable(getResources().getDrawable(R.drawable.color_bg_pressed));
        drawButton = (ImageButton) findViewById(R.id.buttonBrush);
        drawButton.setOnClickListener(this);
        eraseButton = (ImageButton) findViewById(R.id.buttonErase);
        eraseButton.setOnClickListener(this);
        newButton = (ImageButton) findViewById(R.id.buttonNew);
        newButton.setOnClickListener(this);
        saveButton = (ImageButton) findViewById(R.id.buttonSave);
        saveButton.setOnClickListener(this);

        smallBrush = getResources().getInteger(R.integer.small_size);
        mediumBrush = getResources().getInteger(R.integer.medium_size);
        largeBrush = getResources().getInteger(R.integer.large_size);

        canvasView.setBrushSize(mediumBrush);
    }

    public void paintClicked(View view){
        if (view != mSelectedColor){
            ImageButton imageButton = (ImageButton) view;
            String colorTag = imageButton.getTag().toString();
            canvasView.setColor(colorTag);
            imageButton.setImageDrawable(getResources().getDrawable(R.drawable.color_bg_pressed));
            mSelectedColor.setImageDrawable(getResources().getDrawable(R.drawable.color_bg));
            mSelectedColor = (ImageButton)view;
            canvasView.setErase(false);
            canvasView.setBrushSize(canvasView.getLastBrushSize());
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch(id){
            case R.id.buttonBrush:
                showBrushSizeChooserDialog();
                break;
            case R.id.buttonErase:
                showEraserSizeChooserDialog();
                break;
            case R.id.buttonNew:
                showNewPaintingAlertDialog();
                break;
            case R.id.buttonSave:
                showSavePaintingConfirmationDialog();
                break;
        }
    }

    private void showBrushSizeChooserDialog(){
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setContentView(R.layout.brush_size_dialog);
        brushDialog.setTitle(R.string.brush_size);
        Button smallBtn = (Button)brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.setBrushSize(smallBrush);
                canvasView.setLastBrushSize(smallBrush);
                brushDialog.dismiss();
            }
        });
        Button mediumBtn = (Button)brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.setBrushSize(mediumBrush);
                canvasView.setLastBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });

        Button largeBtn = (Button)brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                canvasView.setBrushSize(largeBrush);
                canvasView.setLastBrushSize(largeBrush);
                brushDialog.dismiss();
            }
        });
        canvasView.setErase(false);
        brushDialog.show();
    }

    private void showEraserSizeChooserDialog(){
        final Dialog brushDialog = new Dialog(this);
        brushDialog.setTitle(R.string.eraser_size);
        brushDialog.setContentView(R.layout.brush_size_dialog);
        Button smallBtn = (Button)brushDialog.findViewById(R.id.small_brush);
        smallBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                canvasView.setErase(true);
                canvasView.setBrushSize(smallBrush);
                brushDialog.dismiss();
            }
        });
        Button mediumBtn = (Button)brushDialog.findViewById(R.id.medium_brush);
        mediumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.setErase(true);
                canvasView.setBrushSize(mediumBrush);
                brushDialog.dismiss();
            }
        });
        Button largeBtn = (Button)brushDialog.findViewById(R.id.large_brush);
        largeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasView.setErase(true);
                canvasView.setBrushSize(largeBrush);
                brushDialog.dismiss();
            }
        });
        brushDialog.show();
    }

    private void showNewPaintingAlertDialog(){
        AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
        newDialog.setTitle(R.string.new_drawing);
        newDialog.setMessage(R.string.new_drawing_message);
        newDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                canvasView.startNew();
                dialog.dismiss();
            }
        });
        newDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        newDialog.show();
    }

    private boolean checkPermission(){
        if ( ContextCompat.checkSelfPermission( this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE ) != PackageManager.PERMISSION_GRANTED ) {

            ActivityCompat.requestPermissions( this, new String[] {  Manifest.permission.WRITE_EXTERNAL_STORAGE  },
                    100 );

            return false;
        }
        return true;
    }

    private void showSavePaintingConfirmationDialog(){
        if(checkPermission()){
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle(R.string.save_drawing);
            saveDialog.setMessage(R.string.save_drawing_message);
            saveDialog.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){

                    canvasView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), canvasView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");
                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                R.string.drawing_save_success, Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                R.string.drawing_save_fail, Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }

                    canvasView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
        }
    }
}
