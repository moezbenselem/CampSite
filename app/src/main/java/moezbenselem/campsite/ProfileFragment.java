package moezbenselem.campsite;


import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import de.hdodenhof.circleimageview.CircleImageView;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    Button btStaus, btImage;

    public static CircleImageView imageView;
    TextView tvDisplay, tvStatus;
    int holderResource;

    StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    ProgressDialog progressDialog;
    DatabaseReference mDatabase;

    SharedPreferences sharedPreferences;

    String username;


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {


            mAuth = FirebaseAuth.getInstance();
            username = mAuth.getCurrentUser().getDisplayName();
            mStorageRef = FirebaseStorage.getInstance().getReference();
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(username);
            mDatabase.keepSynced(true);

            System.out.println("Display name = " + mAuth.getCurrentUser().getDisplayName());
            System.out.println("photo url = " + mAuth.getCurrentUser().getPhotoUrl());


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
            btImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                /*Intent galleryIntent = new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"Select Image"),REQUEST_RESULT);
                */

                    CropImage.activity()
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setAspectRatio(1, 1)
                            .start(ProfileFragment.this.getActivity());

                }
            });


            mDatabase.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    tvStatus.setText(dataSnapshot.child("status").getValue().toString());
                    tvDisplay.setText(mAuth.getCurrentUser().getDisplayName());
                    final String image = dataSnapshot.child("thumb_image").getValue().toString();
                    final String gender = dataSnapshot.child("gender").getValue().toString();
                    System.out.println("gender ==== "+gender);
                    if (gender.equalsIgnoreCase("male"))
                        holderResource = R.drawable.male_user;
                    else if (gender.equalsIgnoreCase("female"))
                        holderResource = R.drawable.female_user;
                    if(image.equalsIgnoreCase("default")==false)
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
                    }else
                    {
                        if(gender.equalsIgnoreCase("male"))
                            Picasso.with(getContext()).load(image).placeholder(holderResource).into(imageView);
                        else if (gender == "female")
                            Picasso.with(getContext()).load(image).placeholder(holderResource).into(imageView);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }



}
