package com.cybrilla.shashankm.lookup;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class MapFilterActivity extends Activity {

    private String[] boxes = {"food|restaurant","school|university","hospital","liquor_store"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_filter);
    }

    //Called when filter is set (button click)
    public void setFilter(View v){
        EditText radius = (EditText) findViewById(R.id.radius);
        if(!(radius.getText().toString().matches("")) && checked().length > 0) {
            Intent intent = new Intent(this, MapActivity.class);
            intent.putExtra("radius", radius.getText().toString());
            String[] results = checked();
            String name = getIntent().getStringExtra("name");
            String id = getIntent().getStringExtra("id");
            intent.putExtra("name",name);
            intent.putExtra("id",id);
            intent.putExtra("checkedBoxes", results);
            startActivity(intent);
        }else{
            Toast.makeText(this, "Invalid filter", Toast.LENGTH_LONG).show();
        }
    }

    //Function to retrieve checked checkboxes
    private String[] checked(){
        String[] checkedBoxes = new String[4];
        int j = 0;
        for(int i = 0;i < 4;i++){
            String checkBoxId = "checkbox_"+i;
            int resource =  getResources().getIdentifier(checkBoxId, "id", getPackageName());
            CheckBox check = (CheckBox) findViewById(resource);
            if(check.isChecked()){
                checkedBoxes[j++] = boxes[i];
            }
        }
        return checkedBoxes;
    }
}
