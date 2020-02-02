package com.example.insteapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

//import com.bumptech.glide.Glide;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mInstaList;
    private DatabaseReference mdatabaase;

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        mInstaList=(RecyclerView)findViewById(R.id.insta_list);

        mInstaList.setHasFixedSize(true); //setting a fixed size to the recyclerview

        mInstaList.setLayoutManager(new LinearLayoutManager(this));

        mdatabaase= FirebaseDatabase.getInstance().getReference().child("InsteApp");

        mAuth= FirebaseAuth.getInstance();
        mAuthListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                if(firebaseAuth.getCurrentUser()==null){

                    Intent loginIntent=new Intent(MainActivity.this,RegisterActivity.class);
                    loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK); //when the user press back button he should not be able to go to the previous activity
                    startActivity(loginIntent);
                }
            }
        };


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth.addAuthStateListener(mAuthListener); //set authentication listener on the start of the app

        //populate the recycler view

        FirebaseRecyclerAdapter<Insta,InstaViewHolder> FRBA =new FirebaseRecyclerAdapter<Insta, InstaViewHolder>(

                Insta.class,    //THE CLASS from which the data is going to be taken from
                R.layout.insta_row,    //setting the layout
                InstaViewHolder.class,   // seting a view holder class
                mdatabaase    // and the database reference


        ) {
            @Override
            protected void populateViewHolder(InstaViewHolder instaViewHolder, Insta insta, int i) {

                final String postKey=getRef(i).getKey().toString() ;  // get the reference or the id of the post that will be clicked

                //populate the recyclerview

               instaViewHolder.setTitle(insta.getTitle());
               instaViewHolder.setDesc(insta.getDesc());
              instaViewHolder.setImage(getApplicationContext(),insta.getImage());
              instaViewHolder.setUsername(insta.getUsername());

              //set onclick listener to the recyclerView

                 instaViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {

                         Intent singleInstaActivity=new Intent(MainActivity.this,SingleInstaActivity.class);
                         singleInstaActivity.putExtra("PostId",postKey);
                         startActivity(singleInstaActivity);

                     }
                 });

            }
        };

        mInstaList.setAdapter(FRBA); //set the list to the adapter we have just created
    }


    //This class will get the view (recyclerview) and it is going to set the elements which are titleText,titleDescription and the image

    public static class InstaViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public InstaViewHolder(View itemView)
        {
            super(itemView);
             mView=itemView;
        }

        public void setTitle(String title)
        {
            TextView post_title=(TextView)mView.findViewById(R.id.textTitle);
            post_title.setText(title);
        }
        public void setDesc(String desc)
        {
            TextView post_desc=(TextView)mView.findViewById(R.id.textDescription);
            post_desc.setText(desc);
        }
        public void setImage(Context ctx, String image)
        {
            ImageView post_image=(ImageView)mView.findViewById(R.id.post_image);
            Picasso.with(ctx).load(image).into(post_image);
           // Glide.with(ctx).load(image).into(post_Image);


        }
        public void setUsername(String username)
        {
            TextView post_username=(TextView)mView.findViewById(R.id.textUsername);
            post_username.setText(username);
        }
    }




    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id==R.id.addIcon)
        {
            Intent intent=new Intent(MainActivity.this,PostActivity.class);
            startActivity(intent);
        }
        else if(id==R.id.logout)
        {
            mAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }
}
