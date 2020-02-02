package com.example.mytodoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;

public class AddActivity extends AppCompatActivity {


    private EditText addActivity;
    private FirebaseDatabase database;
    private DatabaseReference myRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);


        database= FirebaseDatabase.getInstance();
        myRef=database.getInstance().getReference().child("Activities");

    }
     public void addActivityButton(View view){

         addActivity=(EditText)findViewById(R.id.newActivity);
        String task= addActivity.getText().toString();

        long date= System.currentTimeMillis();
         SimpleDateFormat sdf= new SimpleDateFormat("MMM MM dd,yyy h:mm a");  //time format
         String dateString=sdf.format(date);


         myRef=database.getInstance().getReference().child("Activities");

         DatabaseReference newTask=myRef.push();
         newTask.child("name").setValue(task);
         newTask.child("time").setValue(dateString);

         Toast.makeText(this,"Task is succesfully added",Toast.LENGTH_LONG).show();

         Intent intent= new Intent(this, MainActivity.class);
         startActivity(intent);


     }
}
