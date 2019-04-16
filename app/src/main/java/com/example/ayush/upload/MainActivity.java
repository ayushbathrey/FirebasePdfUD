package com.example.ayush.upload;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Random;

import dmax.dialog.SpotsDialog;

public class MainActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_CODE = 1000;
    Button btn_upload;
    ImageView image_view;
    AlertDialog dialog;

    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Init
        dialog = new SpotsDialog.Builder().setContext(this).build();
        btn_upload = findViewById(R.id.btn_upload);
        image_view = findViewById(R.id.image_view);

        //Create name for file
        Random r = new Random();
        int r1 = r.nextInt(60-10)+10;
        storageReference = FirebaseStorage.getInstance().getReference("Document_Upload"+r1+r1+1);

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //We will use Intent to pick image
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select picture"), PICK_IMAGE_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
            //Show dialog
            dialog.show();

            UploadTask uploadTask = storageReference.putFile(data.getData());
            Toast.makeText(this, "OnActresult", Toast.LENGTH_SHORT).show();
            Log.d("onactclick", String.valueOf(uploadTask));

            //Create task
            Task<Uri> task = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Failed", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();
                    }
                    return storageReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        String url = task.getResult().toString().substring(0, task.getResult().toString().indexOf("&token"));
                        Log.d("DIRECTLINK", url);
                        Picasso.get().load(url).into(image_view);

                        dialog.dismiss();
                    }
                }
            });

    }
}
