package moezbenselem.campsite.dialogs;


import android.app.Activity;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import moezbenselem.campsite.R;

/*
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
*/

/**
 * Created by Moez on 30/04/2018.
 */

public class CustomDialog extends Dialog implements
        View.OnClickListener {

    public Activity c;
    public Dialog d;
    public Button save, cancel;
    SharedPreferences sharedPreferences;
    EditText etStatus;
    DatabaseReference mDatabase;
    String uid;

    public CustomDialog(Activity a, String uid) {
        super(a);
        // TODO Auto-generated constructor stub
        this.c = a;
        this.uid = uid;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            requestWindowFeature(Window.FEATURE_NO_TITLE);

            setContentView(R.layout.dialog_layout);

            etStatus = findViewById(R.id.dialog_status);
            save = findViewById(R.id.btn_save);
            cancel = findViewById(R.id.btn_no);
            save.setOnClickListener(this);
            cancel.setOnClickListener(this);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_save:

                mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uid);
                String status = etStatus.getText().toString();
                mDatabase.child("status").setValue(status).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        dismiss();
                        c.recreate();

                    }
                });

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
