package com.example.chattest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private Button add_room;
    private EditText room_name;

    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    private ArrayList<String> list_of_rooms = new ArrayList<>();
    private String name;
    private DatabaseReference root = FirebaseDatabase.getInstance().getReference().getRoot();
    private DatabaseReference BasicUser = root.child(getUniquePsuedoID()).getRef();

    List<String> dss = new ArrayList<>();
    List<String> set;
    List<String> devices = new ArrayList<>();

    private final boolean ADMIN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        add_room = (Button) findViewById(R.id.btn_add_room);
        listView = (ListView) findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list_of_rooms);

        listView.setAdapter(arrayAdapter);

        request_user_name();

        add_room.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*Map<String,Object> map = new HashMap<String, Object>();
                map.put(getUniquePsuedoID(),null);
                root.updateChildren(map);*/
                Map<String,Object> map2 = new HashMap<String, Object>();
                Room room = new Room();
                room.Name = "Miloš Valovič";
                room.Subject = "Kvalita";
                map2.put(root.push().getKey(),room);
                BasicUser.updateChildren(map2);


            }
        });

        root.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                set = new ArrayList<String>();
                dss.clear();
                devices.clear();
                if(ADMIN) {
                    Iterator i = dataSnapshot.getChildren().iterator();
                    Log.e("DS", dataSnapshot.toString());

                    while (i.hasNext()) {
                        DataSnapshot ds = ((DataSnapshot) i.next());
                        //set.add(ds.child("Name").getValue() + " - " + ds.child("Subject").getValue());
                        Iterator i2 = ds.getChildren().iterator();
                        while (i2.hasNext()) {

                            DataSnapshot ds2 = ((DataSnapshot) i2.next());

                            set.add(ds2.child("Name").getValue() + " - " + ds2.child("Subject").getValue());
                            dss.add(ds2.getKey());
                            devices.add(ds.getKey());

                            Log.e("DS", ds.getKey());
                        }


                    }
                } else {
                    Iterator i = dataSnapshot.child(getUniquePsuedoID()).getChildren().iterator();
                    Log.e("DS", dataSnapshot.toString());

                    while (i.hasNext()) {
                        DataSnapshot ds = ((DataSnapshot) i.next());
                        //set.add(ds.child("Name").getValue() + " - " + ds.child("Subject").getValue());
                            set.add(ds.child("Name").getValue() + " - " + ds.child("Subject").getValue());
                            dss.add(ds.getKey());
                            devices.add(getUniquePsuedoID());
                            Log.e("DS", ds.getKey());

                    }

                }

                list_of_rooms.clear();
                list_of_rooms.addAll(set);

                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                intent.putExtra("room_name",dss.get(i));
                intent.putExtra("user_name",name);
                intent.putExtra("device_id",devices.get(i));
                Log.e("device_id", dss.get(i));
                startActivity(intent);
            }
        });

    }

    private void request_user_name() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter name:");

        final EditText input_field = new EditText(this);

        builder.setView(input_field);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                name = input_field.getText().toString();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                request_user_name();
            }
        });

        builder.show();
    }

    public static String getUniquePsuedoID() {
        String m_szDevIDShort = "35" + (Build.BOARD.length() % 10) + (Build.BRAND.length() % 10) + (Build.CPU_ABI.length() % 10) + (Build.DEVICE.length() % 10) + (Build.MANUFACTURER.length() % 10) + (Build.MODEL.length() % 10) + (Build.PRODUCT.length() % 10);

        String serial = null;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial";
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }
}
