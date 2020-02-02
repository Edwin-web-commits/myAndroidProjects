package com.example.insteapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameField;
    private EditText passField;
    private EditText emailField;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameField=(EditText)findViewById(R.id.nameField);
        emailField=(EditText)findViewById(R.id.emailField);
        passField=(EditText)findViewById(R.id.passField);

        mAuth = FirebaseAuth.getInstance();
        mDatabase= FirebaseDatabase.getInstance().getReference().child("Users"); //we register the user and store thier data in the database

    }






    public void registerButtonClicked(View view)

    {

        final String name=nameField.getText().toString().trim();
         String email=emailField.getText().toString().trim();
        String password=passField.getText().toString().trim();

        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password))
        {
           mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this,new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {


                    if(task.isSuccessful())
                    {
                       String user_id=mAuth.getCurrentUser().getUid();   //get the unique Id of the user

                        // store the user information in the database

                        DatabaseReference current_user_db=mDatabase.child(user_id);
                        current_user_db.child("Name").setValue(name);
                        current_user_db.child("image").setValue("default");

                        //when the user is signed in,direct him to the main Activity



                        Intent mainIntent=new Intent(RegisterActivity.this,SetupActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(mainIntent);
                    }
                    else {
                        // If sign in fails, display a message to the user.
                        // Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }


                }

            }) ;



        }
        else
        {
            Toast.makeText(RegisterActivity.this,"Enter you sign in credentials ...",Toast.LENGTH_LONG).show();

        }
    }
}
