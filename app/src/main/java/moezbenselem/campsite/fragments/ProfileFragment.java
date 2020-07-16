package moezbenselem.campsite.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import moezbenselem.campsite.R;
import moezbenselem.campsite.dialogs.CustomDialog;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public static CircleImageView imageView;
    Button btStaus, btImage;
    Uri resultUri = null;
    TextView tvDisplay, tvStatus;
    int holderResource;

    StorageReference mStorageRef;
    ProgressDialog progressDialog;
    DatabaseReference mDatabase;
    String username;
    StorageReference filePath;
    private FirebaseAuth mAuth;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {

            mAuth = FirebaseAuth.getInstance();
            username = mAuth.getCurrentUser().getDisplayName();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
            mDatabase.keepSynced(true);

            tvDisplay = getView().findViewById(R.id.display_name);

            tvStatus = getView().findViewById(R.id.description);

            imageView = getView().findViewById(R.id.circleImageView);

            btStaus = getView().findViewById(R.id.btStatus);
            btStaus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CustomDialog cd = new CustomDialog(ProfileFragment.this.getActivity(), mAuth.getCurrentUser().getDisplayName());
                    cd.show();

                }
            });

            btImage = getView().findViewById(R.id.btImage);
            final Fragment currentFragment = this;
            btImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(getContext(), currentFragment);

                }
            });


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    try {
                        tvStatus.setText(dataSnapshot.child("status").getValue().toString());
                        tvDisplay.setText(mAuth.getCurrentUser().getDisplayName());
                        final String image = dataSnapshot.child("thumb_image").getValue().toString();
                        final String gender = dataSnapshot.child("gender").getValue().toString();
                        System.out.println("gender ==== " + gender);
                        if (gender.equalsIgnoreCase("male"))
                            holderResource = R.drawable.male_user;
                        else if (gender.equalsIgnoreCase("female"))
                            holderResource = R.drawable.female_user;
                        if (image.equalsIgnoreCase("default") == false)
                        //Picasso.with(SettingsActivity.this).load(image).placeholder(R.drawable.male_avatar).into(imageView);
                        {


                            Picasso.with(getContext()).load(image).networkPolicy(NetworkPolicy.OFFLINE)
                                    .placeholder(holderResource).into(imageView, new Callback() {
                                @Override
                                public void onSuccess() {

                                }

                                @Override
                                public void onError() {

                                    if (gender.equalsIgnoreCase("male"))
                                        Picasso.with(getContext()).load(image).placeholder(holderResource).into(imageView);
                                    else if (gender == "female")
                                        Picasso.with(getContext()).load(image).placeholder(holderResource).into(imageView);
                                }
                            });
                        } else {
                            if (gender.equalsIgnoreCase("male"))
                                Picasso.with(getContext()).load(image).placeholder(holderResource).into(imageView);
                            else if (gender == "female")
                                Picasso.with(getContext()).load(image).placeholder(holderResource).into(imageView);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == Activity.RESULT_OK) {
                resultUri = result.getUri();
                uploadImage(mAuth.getCurrentUser().getDisplayName());
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }

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

                                                //progressDialog.dismiss();


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }


}
