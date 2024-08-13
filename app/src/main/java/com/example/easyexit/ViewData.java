package com.example.easyexit;

import static com.example.easyexit.Admin.bag;
import static com.example.easyexit.User.reqroll;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class ViewData extends AppCompatActivity {

    ArrayList<UserData2> lists;
    ArrayList<UserData2> lists1;
    AdapterClass adapterClass;
    SearchView search;
    RecyclerView list;
    SwipeRefreshLayout swipeRefreshLayout;
    UserData2 ud;

    FirebaseUser muser;
    FirebaseDatabase mdata;
    DatabaseReference databaseReference;
    java.sql.Date date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_data);
        search = (SearchView) findViewById(R.id.search);
        list = (RecyclerView) findViewById(R.id.recycle);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.cont4);
        list.setHasFixedSize(true);
        list.setLayoutManager(new LinearLayoutManager(this));
        getSupportActionBar().hide();
        lists = new ArrayList<>();
        lists1 = new ArrayList<>();
        adapterClass = new AdapterClass(lists);
        ArrayAdapter<UserData2> adapter = new ArrayAdapter<UserData2>(ViewData.this, R.layout.items, lists);
        list.setAdapter(adapterClass);
        mdata = FirebaseDatabase.getInstance();
        databaseReference = mdata.getReference().child("Out Data");
        long millis = System.currentTimeMillis();
        date = new java.sql.Date(millis);
        databaseReference = databaseReference.child(String.valueOf(date));
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onRefresh() {
                adapterClass.notifyDataSetChanged();
                finish();
                startActivity(getIntent());
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query;
        if(Objects.equals(bag, "my"))
        {
            lists.clear();
            query = mdata.getReference().child("Out Data").child(String.valueOf(date)).orderByChild("rollno").equalTo(String.valueOf(reqroll));
            query.addListenerForSingleValueEvent(valueEventListener);
        }
        else if(Objects.equals(bag, "waiting"))
        {
            lists.clear();
            query = mdata.getReference().child("Out Data").child(String.valueOf(date)).orderByChild("status").equalTo("waiting");
            query.addListenerForSingleValueEvent(valueEventListener);

        }
        else if(Objects.equals(bag, "Approved"))
        {
            lists.clear();
            query = mdata.getReference().child("Out Data").child(String.valueOf(date)).orderByChild("status").equalTo("Approved");
            query.addListenerForSingleValueEvent(valueEventListener);
        }
        else if(Objects.equals(bag, "mydata"))
        {
            try{
            lists.clear();
            query = mdata.getReference().child("Out Data").child("2023-01-05").orderByChild("rollno").equalTo(String.valueOf(reqroll));
            query.addListenerForSingleValueEvent(valueEventListener);}
            catch (Exception e){
                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show(); }
        }
        else {
            try{
            databaseReference.addListenerForSingleValueEvent(valueEventListener);}
            catch (Exception e)
            {
                Toast.makeText(getApplicationContext(), ""+e, Toast.LENGTH_LONG).show();
            }
        }
        if (search != null) {
            search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String s) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String s) {
                    search(s);
                    return true;
                }
            });
        }
    }

    ValueEventListener valueEventListener = new ValueEventListener() {
        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot){
                lists.clear();
               try {
                   if (snapshot.exists()) {
              /*  if (Objects.equals(bag, "waiting") && snapshot.exists()) {
                    try{
                    if (snapshot.exists()) {
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            UserData2 i = ds.getValue(UserData2.class);
                            assert i != null;
                            if (String.valueOf(i.getStatus()).equals("waiting")||String.valueOf( i.getStatus()).equals("Rejected")) {
                                lists.add(i);
                            }
                        }
                    }
                    adapterClass.notifyDataSetChanged();}
                    catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),"error occurred"+e,Toast.LENGTH_SHORT).show();
                    }
                }
                else if(Objects.equals(bag, "my") &&  snapshot.exists())
                {
               }*/
                       // else {
                       for (DataSnapshot ds : snapshot.getChildren()) {
                           UserData2 i = ds.getValue(UserData2.class);
                           lists.add(i);
                       }
                       if(lists.isEmpty())
                       {
                           Toast.makeText(getApplicationContext(), "No data available", Toast.LENGTH_LONG).show();
                       }
                       adapterClass.notifyDataSetChanged();
                   }
               }
                catch (Exception e) { Toast.makeText(getApplicationContext(),"error occurred",Toast.LENGTH_SHORT).show();}
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
        }
    };


    private void search(String s) {
        ArrayList<UserData2> mylist = new ArrayList<>();
        /*if (Objects.equals(bag, "waiting")) {
            for (UserData2 object : lists1) {
                if (object.getRollno().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                    mylist.add(object);
                }
            }
        }
        else {*/
            for (UserData2 object : lists) {
                if (object.getRollno().toLowerCase(Locale.ROOT).contains(s.toLowerCase(Locale.ROOT))) {
                    mylist.add(object);
                }
            }
        AdapterClass adapterClass = new AdapterClass(mylist);
        list.setAdapter(adapterClass);
    }
}


