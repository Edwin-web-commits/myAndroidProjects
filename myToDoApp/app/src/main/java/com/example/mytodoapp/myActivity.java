package com.example.mytodoapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class myActivity extends AppCompatActivity {

    private TextView myActiviy;
    private TextView myTime;

    private FirebaseDatabase myDatabase;
    private DatabaseReference myDatabaseRef;

    private String post=null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my);

        myDatabase=FirebaseDatabase.getInstance();
        myDatabaseRef=myDatabase.getReference().child("Activities");


         post=getIntent().getExtras().getString("POST_KEY");

        myActiviy=(TextView)findViewById(R.id.taskName);
        myTime=(TextView)findViewById(R.id.timeStamp);

        myDatabaseRef.child(post).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                String taskname=(String) dataSnapshot.child("name").getValue();
                String taskTime=(String) dataSnapshot.child("time").getValue();

                myTime.setText(taskTime);
                myActiviy.setText(taskname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        }) ;

    }

    public void deleteTaskButton(View view)
    {
        myDatabaseRef.child(post).removeValue() ;

        Intent myIntent= new Intent(myActivity.this,MainActivity.class);
        startActivity(myIntent);
    }
}
