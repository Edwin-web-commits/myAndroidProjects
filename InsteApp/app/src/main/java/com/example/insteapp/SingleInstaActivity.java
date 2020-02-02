package com.example.insteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SingleInstaActivity extends AppCompatActivity {

    private String postKey=null;
    private DatabaseReference mDatabase;
    private ImageView singlePostImage;
    private TextView singlePostTitle;
    private TextView singlePostDesc;
    private Button deleteButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_insta);

        postKey= getIntent().getExtras().getString("PostId");

        mDatabase= FirebaseDatabase.getInstance().getReference().child("InsteApp");

        singlePostDesc=(TextView)findViewById(R.id.singleDesc);
        singlePostTitle=(TextView)findViewById(R.id.singleTitle);
        singlePostImage=(ImageView) findViewById(R.id.singleImageView);
        deleteButton=(Button)findViewById(R.id.singleDeleteButton);
        deleteButton.setVisibility(View.INVISIBLE);

        mAuth=FirebaseAuth.getInstance();

        //extract values from the database

        mDatabase.child(postKey).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String post_title=(String) dataSnapshot.child("title").getValue();
                String post_desc=(String) dataSnapshot.child("desc").getValue();
                String post_image=(String) dataSnapshot.child("image").getValue();
                String post_uid=(String) dataSnapshot.child("uid").getValue();

                singlePostTitle.setText(post_title);
                singlePostDesc.setText(post_desc);
                Picasso.with(SingleInstaActivity.this).load(post_image).into(singlePostImage);

                //make the button visible
                if(mAuth.getCurrentUser().getUid().equals(post_uid))
                {

                    deleteButton.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }

    public void deleteButtonClicked(View view)
    {
        mDatabase.child(postKey).removeValue();
        Intent mainIntent=new Intent(SingleInstaActivity.this,MainActivity.class);
        startActivity(mainIntent);
    }
}
