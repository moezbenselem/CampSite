package moezbenselem.campsite.fragments;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import moezbenselem.campsite.R;
import moezbenselem.campsite.activities.AlbumsActivity;
import moezbenselem.campsite.activities.ShowMediaActivity;

public class ImagesFragment extends Fragment implements
        AdapterView.OnItemClickListener {

    private static final ArrayList<Uri> listUrl = new ArrayList<>();
    private static final ArrayList<String> listNames = new ArrayList<>();
    String eventId, eventName;
    private ImageView selection;

    public ImagesFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_images, container, false);

        selection = view.findViewById(R.id.selection_image);
        final GridView grid = view.findViewById(R.id.grid_images);
        // grid.setAdapter(new ArrayAdapter<Integer>(this, R.layout.cell,
        // items));


        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("media/images/" + AlbumsActivity.eventId);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        listUrl.clear();
                        listNames.clear();
                        final CustomGridAdapter customGridAdapter = new CustomGridAdapter(ImagesFragment.this.getActivity(), listUrl);
                        grid.setAdapter(customGridAdapter);
                        /*for (StorageReference prefix : listResult.getPrefixes()) {
                            //System.out.println("prefix : "+prefix);
                            listNames.add(AlbumsActivity.eventName+prefix);
                        }*/

                        for (final StorageReference item : listResult.getItems()) {
                            System.out.println("item : " + item);
                            item.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    listUrl.add(uri);

                                    item.getMetadata().addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
                                        @Override
                                        public void onSuccess(StorageMetadata storageMetadata) {
                                            listNames.add(new Date(storageMetadata.getCreationTimeMillis()).toString());
                                        }
                                    });

                                    customGridAdapter.notifyDataSetChanged();
                                }
                            });
                        }

                        grid.setOnItemClickListener(ImagesFragment.this);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Uh-oh, an error occurred!
                    }
                });


        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            //String url ="https://firebasestorage.googleapis.com/v0/b/campsite-90984.appspot.com/o/message_images%2F-LX_5VWnKBdIwBP5Pjkc.jpg?alt=media&token=9acd78ff-b6f8-4d93-ab31-30771b893039";
            //String url = "https://firebasestorage.googleapis.com/v0/b/campsite-90984.appspot.com/o/videos%2F-M9uAHK0GNl9nav22g4w?alt=media&token=a86bbe1e-202f-47da-9578-ebcaa393e977";
            Intent toShow = new Intent(this.getActivity(), ShowMediaActivity.class);
            toShow.putExtra("type", "image");
            toShow.putExtra("url", listUrl.get(position).toString());
            toShow.putExtra("name", listNames.get(position));
            getActivity().startActivity(toShow);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public class CustomGridAdapter extends BaseAdapter {
        // Keep all Images in array
        public ArrayList<Uri> mThumbIds;
        private Activity mContext;

        // Constructor
        public CustomGridAdapter(Activity mainActivity, ArrayList<Uri> items) {
            this.mContext = mainActivity;
            this.mThumbIds = items;
        }

        @Override
        public int getCount() {
            return mThumbIds.size();
        }

        @Override
        public Object getItem(int position) {
            return mThumbIds.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            final ImageView imageView = new ImageView(mContext);
            Picasso.with(mContext).load(mThumbIds.get(position)).networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.black).into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(mContext).load(mThumbIds.get(position)).placeholder(R.drawable.black).into(imageView);
                }
            });
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(320, 320));
            return imageView;
        }

    }


}
