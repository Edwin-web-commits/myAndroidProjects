package com.example.insteapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

public class SetupActivity extends AppCompatActivity {

    private ImageButton displayImage;
    private EditText editDisplayName;
    private static final int GALLERY_REQ=1;
    private Uri imageUri=null;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseusers;
    private StorageReference mStorageref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);

        displayImage=(ImageButton)findViewById(R.id.setupImageButton);
        editDisplayName=(EditText)findViewById(R.id.displayName);

        mDatabaseusers= FirebaseDatabase.getInstance().getReference().child("Users");
        mAuth=FirebaseAuth.getInstance();
        mStorageref= FirebaseStorage.getInstance().getReference().child("profile_image");

    }
    public void profileImageButtonClicked(View view)
    {
        Intent galleryIntent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent,GALLERY_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //setting the image and applying copping on it

        if(requestCode==GALLERY_REQ && resultCode==RESULT_OK)
        {
            imageUri=data.getData();
            CropImage.activity(imageUri).setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1).start(this); // cropping the image

        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                imageUri=result.getUri();
                displayImage.setImageURI(imageUri);

            }else if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error= result.getError();
            }
        }
    }

    // we store the image and the user name under the user associated with them(The user that logged in).

    public void doneButtonClicked(View view)
    {
       final String name =editDisplayName.getText().toString().trim();
       final String user_id=mAuth.getCurrentUser().getUid();

        if(!TextUtils.isEmpty(name) && imageUri !=null)
        {
            final StorageReference filePath=mStorageref.child(imageUri.getLastPathSegment());

            final UploadTask uploadTask= filePath.putFile(imageUri);


            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }

                    // Continue with the task to get the download URL


                    return filePath.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()) {
                        Uri downloadUri = task.getResult();


                       // DatabaseReference newPost=mDatabaseusers.push();
                        mDatabaseusers.child(user_id).child("name").setValue(name);
                        mDatabaseusers.child(user_id).child("image").setValue(downloadUri.toString());

                        Toast.makeText(SetupActivity.this,"saved",Toast.LENGTH_LONG).show();

                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(SetupActivity.this,"ERROR",Toast.LENGTH_LONG).show();
                    }
                }
            });





        }
    }
}
