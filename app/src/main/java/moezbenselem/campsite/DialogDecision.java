package moezbenselem.campsite;

import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class DialogDecision extends Dialog implements
        View.OnClickListener {

    SharedPreferences sharedPreferences;
    public Activity c;
    public Dialog d;
    public Button chat, remove , track;






    DatabaseReference mDatabase,groupChatRef;
    Event event;

    public DialogDecision(Activity a, Event event) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.event = event;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            setContentView(R.layout.dicision_dialog);

            chat = findViewById(R.id.btn_chat);
            remove = findViewById(R.id.btn_remove);
            track = findViewById(R.id.btn_track);

            chat.setOnClickListener(this);
            remove.setOnClickListener(this);
            track.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_remove:

                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("My_Events").child(mAuth.getCurrentUser().getDisplayName()).child(event.id);
               mDatabase.removeValue(new DatabaseReference.CompletionListener() {
                   @Override
                   public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                       if(databaseError !=null){
                           Toast.makeText(c,"Event Deleted !",Toast.LENGTH_LONG).show();
                           dismiss();
                       }else
                           Toast.makeText(c,"Event Deleted !",Toast.LENGTH_LONG).show();
                   }
               });

                break;
            case R.id.btn_chat:
                Intent chatIntent = new Intent(getContext(), GroupChatActivity.class);

                chatIntent.putExtra("uid", event.id);
                chatIntent.putExtra("name", event.getName());
                c.startActivity(chatIntent);
                break;

            default:
                break;
        }
        dismiss();
    }


}
