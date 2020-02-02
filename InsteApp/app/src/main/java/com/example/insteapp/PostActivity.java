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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

    private static final int GALLERY_REQUEST=2;
    private Uri uri= null;
    private ImageButton imageButton;

    private EditText editname;
    private EditText editDesc;

    private StorageReference storageReference;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private String downloadUrl;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabaseUsers;
    private FirebaseUser mCurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        editname=(EditText)findViewById(R.id.editName);
        editDesc=(EditText)findViewById(R.id.editDesc);

        storageReference= FirebaseStorage.getInstance().getReference();
        databaseReference=FirebaseDatabase.getInstance().getReference().child("InsteApp");

        mAuth=FirebaseAuth.getInstance();
        mCurrentUser=mAuth.getCurrentUser();
        mDatabaseUsers=FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


    }
    public  void imageButtonClicked(View view)
    {
        Intent galleryintent=new Intent(Intent.ACTION_GET_CONTENT);
        galleryintent.setType("image/*");
        startActivityForResult(galleryintent,GALLERY_REQUEST);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==GALLERY_REQUEST && resultCode==RESULT_OK )
        {
            uri=data.getData();
            imageButton=(ImageButton)findViewById(R.id.imageButton);
            imageButton.setImageURI(uri);

        }
    }
    public void submitButtonClicked(View view){

        final String titleValue=editname.getText().toString().trim();
        final String titleDesc=editDesc.getText().toString().trim();

        if(!TextUtils.isEmpty(titleValue) && !TextUtils.isEmpty(titleDesc))
        {

            final StorageReference filePath=storageReference.child("PostImage").child(uri.getLastPathSegment());

           final UploadTask uploadTask= filePath.putFile(uri);


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
                       final Uri downloadUri = task.getResult();
                        Toast.makeText(PostActivity.this,"Upload Complete",Toast.LENGTH_LONG).show();

                       final DatabaseReference newPost=databaseReference.push();


                        mDatabaseUsers.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                                newPost.child("title").setValue(titleValue);
                                newPost.child("desc").setValue(titleDesc);
                                newPost.child("image").setValue(downloadUri.toString());
                                newPost.child("uid").setValue(mCurrentUser.getUid());
                                newPost.child("username").setValue(dataSnapshot.child("Name").getValue()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {

                                        if(task.isSuccessful())
                                        {
                                            Intent mainActivityIntent=new Intent(PostActivity.this,MainActivity.class);
                                        }
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else {
                        // Handle failures
                        // ...
                        Toast.makeText(PostActivity.this,"ERRor",Toast.LENGTH_LONG).show();
                    }
                }
            });

           /*

         uploadTask.addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                   String message=e.toString();
                   Toast.makeText(PostActivity.this,"Error",Toast.LENGTH_LONG).show();
               }
           }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                   Toast.makeText(PostActivity.this,"Upload Complete",Toast.LENGTH_LONG).show();

                   Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                       @Override
                       public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                           if (!task.isSuccessful()) {
                               throw task.getException();
                           }

                           // Continue with the task to get the download URL

                          downloadUrl=filePath.getDownloadUrl().toString();
                           return filePath.getDownloadUrl();
                       }
                   }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                       @Override
                       public void onComplete(@NonNull Task<Uri> task) {

                           if(task.isSuccessful())
                           {
                               Toast.makeText(PostActivity.this,"Image Url was succesfully got",Toast.LENGTH_LONG).show();
                               DatabaseReference newPost=databaseReference.push();
                               newPost.child("title").setValue(titleValue);
                               newPost.child("desc").setValue(titleDesc);
                               newPost.child("image").setValue(downloadUrl);

                           }
                       }
                   });
               }
           });


            */




        }

    }
}
