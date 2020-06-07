package moezbenselem.campsite;

import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class EventDialog extends Dialog implements
        View.OnClickListener {

    SharedPreferences sharedPreferences;
    public Activity c;
    public Dialog d;
    public Button save, cancel;
    TextView tvDate,tvLoc,tvTopic,tvEvent,tvAdmin;





    DatabaseReference mDatabase,groupChatRef;
    Event event;

    public EventDialog(Activity a, Event event) {
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
            setContentView(R.layout.event_dialog);

            tvAdmin = findViewById(R.id.tv_admin_d);
            tvDate = findViewById(R.id.tv_date_d);
            tvEvent = findViewById(R.id.tv_event_d);
            tvLoc = findViewById(R.id.tv_location_d);
            tvTopic = findViewById(R.id.tv_topic_d);

            tvDate.append(event.getDate());
            tvEvent.append(event.getName());
            tvLoc.append(event.getLocation());
            tvTopic.append(event.getTopic());
            tvAdmin.append(event.getAdmin());

            save = findViewById(R.id.btn_participer);
            cancel = findViewById(R.id.btn_cancel);
            save.setOnClickListener(this);
            cancel.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_participer:

                final FirebaseAuth mAuth = FirebaseAuth.getInstance();
                mDatabase = FirebaseDatabase.getInstance().getReference().child("My_Events").child(mAuth.getCurrentUser().getDisplayName());
                groupChatRef = FirebaseDatabase.getInstance().getReference().child("GroupChat").child(event.id);
                Map<String, Object> updates = new HashMap<>();
                updates.put(event.id, true);
                mDatabase.updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Map<String, Object> updates = new HashMap<>();
                        updates.put(mAuth.getCurrentUser().getDisplayName(),true);
                        groupChatRef.child("members").updateChildren(updates);
                    }
                });
//                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                        dismiss();
//
//                    }
//                });

                break;
            case R.id.btn_no:
                dismiss();
                break;
            default:
                break;
        }
        dismiss();
    }


}
