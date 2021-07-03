package moezbenselem.campsite;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import com.vstechlab.easyfonts.EasyFonts;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    EditText etName, etEmail, et_login_username, et_login_password, etPsw, etConfirm, et_verify;
    TextView tv, tvImage;
    Button btCreate, btLogin, mainSignIn, mainSignUp, bt_login_confirm, bt_login_signup;
    ImageView image;
    RadioButton male, female;
    LinearLayout layout_login, layout_signUp, layout_main;

    Uri resultUri = null;
    StorageReference mStorageRef;
    ProgressDialog progressDialog;
    DatabaseReference mDatabase;
    DatabaseReference userReference;
    StorageReference filePath;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        try {

            mAuth = FirebaseAuth.getInstance();
            progressDialog = new ProgressDialog(this);

            userReference = FirebaseDatabase.getInstance().getReference().child("Users");
            mStorageRef = FirebaseStorage.getInstance().getReference();

            tv = findViewById(R.id.textView);
            etName = findViewById(R.id.et_firstname);
            etEmail = findViewById(R.id.et_email);
            etPsw = findViewById(R.id.et_password);
            etConfirm = findViewById(R.id.et_confirm_password);
            et_login_username = findViewById(R.id.et_login_username);
            et_login_password = findViewById(R.id.et_login_password);


            image = findViewById(R.id.Image);
            tvImage = findViewById(R.id.tvimage);


            btCreate = findViewById(R.id.btCreate);
            btLogin = findViewById(R.id.bt_login);

            mainSignIn = findViewById(R.id.mainLogin);
            mainSignUp = findViewById(R.id.mainCreate);

            bt_login_confirm = findViewById(R.id.bt_login_confirm);
            bt_login_signup = findViewById(R.id.bt_login_Signup);


            layout_login = findViewById(R.id.layoutSignIn);
            layout_signUp = findViewById(R.id.layoutSignUp);
            layout_main = findViewById(R.id.layoutLog);

            mainSignUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layout_main.setVisibility(View.GONE);
                    layout_signUp.setVisibility(View.VISIBLE);

                }
            });

            mainSignIn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layout_main.setVisibility(View.GONE);
                    layout_login.setVisibility(View.VISIBLE);

                }
            });


            bt_login_signup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layout_login.setVisibility(View.GONE);
                    layout_signUp.setVisibility(View.VISIBLE);
                }
            });

            btLogin.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    layout_signUp.setVisibility(View.GONE);
                    layout_login.setVisibility(View.VISIBLE);
                }
            });

            male = findViewById(R.id.radioMale);
            female = findViewById(R.id.radioFemale);


            male.setTypeface(EasyFonts.robotoThin(this));
            female.setTypeface(EasyFonts.robotoThin(this));

            tv.setTypeface(EasyFonts.robotoRegular(this));
            etName.setTypeface(EasyFonts.robotoThin(this));
            etEmail.setTypeface(EasyFonts.robotoThin(this));
            etPsw.setTypeface(EasyFonts.robotoThin(this));
            etConfirm.setTypeface(EasyFonts.robotoThin(this));
            et_login_password.setTypeface(EasyFonts.robotoThin(this));
            et_login_username.setTypeface(EasyFonts.robotoThin(this));
            tvImage.setTypeface(EasyFonts.robotoThin(this));

            btCreate.setTypeface(EasyFonts.robotoRegular(this));
            btLogin.setTypeface(EasyFonts.robotoRegular(this));
            mainSignIn.setTypeface(EasyFonts.robotoRegular(this));
            mainSignUp.setTypeface(EasyFonts.robotoRegular(this));
            bt_login_confirm.setTypeface(EasyFonts.robotoRegular(this));
            bt_login_signup.setTypeface(EasyFonts.robotoRegular(this));

            image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(LoginActivity.this);

                }
            });


            bt_login_confirm.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String login = et_login_username.getText().toString(),
                            psw = et_login_password.getText().toString();


                    System.out.println("login " + login);
                    System.out.println("password " + psw);

                    if (login.length() == 0 || psw.length() == 0) {
                        Toast.makeText(getApplicationContext(), "Empty Fields !", Toast.LENGTH_SHORT).show();

                    } else {
                        progressDialog.setTitle("Connecting");
                        progressDialog.setMessage("Please wait you are being connected !");
                        progressDialog.setCanceledOnTouchOutside(false);
                        progressDialog.show();
                        loginUSer(login, psw);
                    }
                }


            });


            btCreate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String nom = etName.getText().toString(),
                            psw = etPsw.getText().toString(),
                            email = etEmail.getText().toString(),
                            gender;

                    if (male.isChecked())
                        gender = "male";
                    else
                        gender = "female";

                    System.out.println("nom " + nom);
                    System.out.println("password " + psw);

                    if (email.length() == 0 || nom.length() == 0 || psw.length() == 0) {
                        Toast.makeText(getApplicationContext(), "All Fields Required !", Toast.LENGTH_SHORT).show();
                    } else {

                        registerUSer(email, nom, psw, gender, resultUri);
                    }
                }


            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                resultUri = result.getUri();
                image.setImageURI(resultUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

    }

    public void registerUSer(final String email, final String nom, String password, final String gender, final Uri uriImage) {

        if (email.isEmpty() || nom.length() < 4 || password.length() < 6) {
            Toast.makeText(getApplicationContext(), "Verify All Fields !", Toast.LENGTH_SHORT);
        } else if (password.equals(etConfirm.getText().toString()) == false) {

            Toast.makeText(getApplicationContext(), "Password Confirmation is Incorrect !", Toast.LENGTH_SHORT);
        } else {
            progressDialog.setTitle("Resolving Data");
            progressDialog.setMessage("Please wait !");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            System.out.println("createUserWithEmail:onComplete:" + task.isSuccessful());

                            if (!task.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Registration Failed !\n" + task.getException().getMessage(),
                                        Toast.LENGTH_LONG).show();
                                progressDialog.hide();
                                System.out.println(task.getException().getMessage());
                            } else {

                                try {

                                    String device_token = FirebaseInstanceId.getInstance().getToken();
                                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(nom);

                                    HashMap<String, String> userMap = new HashMap<String, String>();

                                    userMap.put("username", nom);
                                    userMap.put("status", "Camper");
                                    userMap.put("gender", gender);
                                    userMap.put("email", email);
                                    userMap.put("device_token", device_token);
                                    if (uriImage == null) {
                                        userMap.put("image", "default");
                                        userMap.put("thumb_image", "default");

                                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {


                                                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                        .setDisplayName(nom)
                                                        .build();

                                                FirebaseUser user = mAuth.getCurrentUser();
                                                user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

                                                        progressDialog.dismiss();
                                                        Intent toMain = new Intent(LoginActivity.this, MainActivity.class);

                                                        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                                        editor.putString("username", nom);
                                                        editor.commit();


                                                        startActivity(toMain);
                                                        finish();

                                                    }
                                                });

                                            }
                                        });

                                    } else {
                                        mDatabase.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                uploadImage(nom);

                                            }
                                        });
                                    }

                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        }
                    });

        }

    }

    public void loginUSer(String login, String password) {

        mAuth.signInWithEmailAndPassword(login, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        System.out.println("signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            System.out.println("signInWithEmail:failed" + task.getException());
                            progressDialog.hide();
                            Toast.makeText(LoginActivity.this, "Connexion Echouée !",
                                    Toast.LENGTH_LONG).show();
                        } else {

                            String device_token = FirebaseInstanceId.getInstance().getToken();

                            userReference.child(mAuth.getCurrentUser().getDisplayName().toString()).child("device_token").setValue(device_token).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    progressDialog.dismiss();


                                    Intent toMain = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(toMain);
                                    finish();

                                }
                            });


                        }

                    }
                });

    }


    public void uploadImage(final String nom) {

        try {

            filePath = mStorageRef.child("users").child(nom + ".jpg");

            filePath.putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(final UploadTask.TaskSnapshot taskSnapshot) {

                    System.out.println("Upload success !");

                    filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(final Uri uri) {
                            // Got the download URL for 'users/me/profile.png'

                            System.out.println("Uri == " + uri);
                            Map updateHashMap = new HashMap();
                            updateHashMap.put("image", uri.toString());
                            updateHashMap.put("thumb_image", uri.toString());
                            mDatabase.updateChildren(updateHashMap).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (!task.isSuccessful()) {

                                        task.getException().printStackTrace();

                                    } else {
                                        System.out.println("Update success !");
                                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(nom)
                                                .setPhotoUri(uri)
                                                .build();

                                        FirebaseUser user = mAuth.getCurrentUser();
                                        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {

                                                progressDialog.dismiss();
                                                Intent toMain = new Intent(LoginActivity.this, MainActivity.class);

                                                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                                SharedPreferences.Editor editor = sharedPreferences.edit();
                                                editor.putString("username", nom);
                                                editor.commit();


                                                startActivity(toMain);
                                                finish();

                                            }
                                        });
                                    }
                                }
                            });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {

                            exception.printStackTrace();
                        }
                    });

                }

            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        try {
            mAuth = FirebaseAuth.getInstance();
            FirebaseUser user = mAuth.getCurrentUser();
            if (user != null) {
                toMain();
            } else {

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void toMain() {
        Intent toMainAct = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(toMainAct);
        finish();
    }

}