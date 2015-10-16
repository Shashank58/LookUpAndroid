package com.cybrilla.shashankm.lookup;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

/**
 * Created by shashankm on 14/10/15.
 */
public class UserInfoActivity extends AppCompatActivity{

    private TextView name, id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        String name = getIntent().getExtras().getString("name");
        String id = getIntent().getStringExtra("id");
        getInfo(name, id);
    }

    public void getInfo(String user_name, String user_id){
        name = (TextView) findViewById(R.id.name);
        id = (TextView) findViewById(R.id.id);
        name.setText("Name: "+user_name);
        id.setText("ID: "+user_id);
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }
}
