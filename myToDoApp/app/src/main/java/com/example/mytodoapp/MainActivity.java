package com.example.mytodoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private DatabaseReference myRef;

    private TextView DayOfTheWeek;
    private TextView myDate ;


   ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        recyclerView=(RecyclerView)findViewById(R.id.ActivityList);
        myRef= FirebaseDatabase.getInstance().getReference().child("Activities");

        DayOfTheWeek=(TextView)findViewById(R.id.DayOfTheWeek);
        myDate=(TextView)findViewById(R.id.myDate);




        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //setting date values

        SimpleDateFormat sdf= new SimpleDateFormat("EEEE");  //time format

        Date date= new Date();
        String currentDayOfTheweek= sdf.format(date);

        DayOfTheWeek.setText(currentDayOfTheweek);

        long currentDate= System.currentTimeMillis();
        SimpleDateFormat sdffff=new SimpleDateFormat("MMM MM dd,yyy h:mm a");
        String dateStamp=sdffff.format(currentDate);

        myDate.setText(dateStamp);


    }

    public static class ActivityViewHolder extends RecyclerView.ViewHolder{

        View mView;

        public ActivityViewHolder(View itemView)
        {
            super(itemView);
            mView=itemView ;
        }

        public void setName(String activityName)
        {
            TextView name =(TextView)mView.findViewById(R.id.activityName);
            name.setText(activityName);

        }
        public void setTime(String mytime)
        {
            TextView date_time =(TextView)mView.findViewById(R.id.mytime);
            date_time.setText(mytime);

        }


    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseRecyclerAdapter<Activities,ActivityViewHolder> FBRA=new FirebaseRecyclerAdapter<Activities, ActivityViewHolder>(
                Activities.class,
                R.layout.activity_row,
                ActivityViewHolder.class,
                myRef
        ) {
            @Override
            protected void populateViewHolder(ActivityViewHolder activityViewHolder, Activities activities, int i) {


                final String post_key=getRef(i).getKey().toString();



               activityViewHolder.setName(activities.getName());
               activityViewHolder.setTime(activities.getTime());


               activityViewHolder.mView.setOnClickListener(new View.OnClickListener() {
                   @Override
                   public void onClick(View v) {

                       Intent intent= new Intent(MainActivity.this,myActivity.class);
                       intent.putExtra("POST_KEY",post_key);
                       startActivity(intent);

                   }
               });

            }
        };

        recyclerView.setAdapter(FBRA);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
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
        if(id==R.id.addActivity)
        {
            Intent intent=new Intent(MainActivity.this,AddActivity.class);
            startActivity(intent);

        }

        return super.onOptionsItemSelected(item);
    }
}
