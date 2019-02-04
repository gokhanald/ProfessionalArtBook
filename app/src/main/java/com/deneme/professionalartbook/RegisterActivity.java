package com.deneme.professionalartbook;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;

public class RegisterActivity extends AppCompatActivity {


    ImageView imageView;

    Button btnSave,btnUpdate,btnDelete;
    EditText editTextArtName;

    Bitmap selectedImage;
    String firstName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        imageView=findViewById(R.id.selectImageView);

        btnSave=findViewById(R.id.btnSave);
        btnUpdate=findViewById(R.id.btnUpdate);
        btnDelete=findViewById(R.id.btnDelete);

        editTextArtName=findViewById(R.id.editTextArtName);





        Intent intent=getIntent();
        String info=intent.getStringExtra("info");

        if (info.equals("new")){
            Bitmap background= BitmapFactory.decodeResource(getApplicationContext().getResources(),R.drawable.ic_launcher_background);
            imageView.setImageBitmap(background);

            editTextArtName.setText("");
            btnDelete.setVisibility(View.INVISIBLE);
            btnUpdate.setVisibility(View.INVISIBLE);
            btnSave.setVisibility(View.VISIBLE);
        }
        else {

            String name=intent.getStringExtra("name");
            editTextArtName.setText(name);
            firstName=name;

            int position=intent.getIntExtra("position",0);
            imageView.setImageBitmap(MainActivity.artImageList.get(position));

            btnDelete.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);
            btnSave.setVisibility(View.INVISIBLE);
        }
    }
    public void selectImage(View view){
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},2);
        }
        else{
            Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent,1);
        }
    }

    public void updateRecord(View view){
        String artName=editTextArtName.getText().toString();
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        Bitmap bitmap=((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] bytes=outputStream.toByteArray();

        ContentValues contentValues=new ContentValues();
        contentValues.put(ArtContentProvider.NAME,artName);
        contentValues.put(ArtContentProvider.IMAGE,bytes);

        String[] selectionArgs={firstName};

        getContentResolver().update(ArtContentProvider.CONTENT_URI,contentValues,"name=?",selectionArgs);


        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);


    }

    public void deleteRecord(View view){

        String recordName=editTextArtName.getText().toString();

        String[] selectionArgs={recordName};

        getContentResolver().delete(ArtContentProvider.CONTENT_URI,"name=?",selectionArgs);

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);
    }

    public void saveRecord(View view){
        String artName=editTextArtName.getText().toString();
        ByteArrayOutputStream outputStream=new ByteArrayOutputStream();
        selectedImage.compress(Bitmap.CompressFormat.PNG,50,outputStream);
        byte[] bytes=outputStream.toByteArray();

        ContentValues contentValues=new ContentValues();
        contentValues.put(ArtContentProvider.NAME,artName);
        contentValues.put(ArtContentProvider.IMAGE,bytes);

        getContentResolver().insert(ArtContentProvider.CONTENT_URI,contentValues);

        Intent intent=new Intent(getApplicationContext(),MainActivity.class);
        startActivity(intent);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode==2){
            if (grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED){
                Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,1);
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode==1&&data!=null&&resultCode==RESULT_OK){
            Uri image=data.getData();
            try{
                 selectedImage= MediaStore.Images.Media.getBitmap(this.getContentResolver(),image);
                imageView.setImageBitmap(selectedImage);
            }
            catch (Exception e){
                e.printStackTrace();
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}
