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

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Date;

import moezbenselem.campsite.R;
import moezbenselem.campsite.activities.AlbumsActivity;
import moezbenselem.campsite.activities.ShowMediaActivity;


public class VideoFragment extends Fragment implements
        AdapterView.OnItemClickListener {

    private static final ArrayList<Uri> listUrl = new ArrayList<>();
    private static final ArrayList<String> listNames = new ArrayList<>();
    String eventId, eventName;
    private ImageView selection;

    public VideoFragment() {
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
        View view = inflater.inflate(R.layout.fragment_video, container, false);
        try {
            selection = view.findViewById(R.id.selection_video);
            final GridView grid = view.findViewById(R.id.grid_videos);
            // grid.setAdapter(new ArrayAdapter<Integer>(this, R.layout.cell,
            // items));
            StorageReference listRef = FirebaseStorage.getInstance().getReference().child("media/videos/" + AlbumsActivity.eventId);

            listRef.listAll()
                    .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                        @Override
                        public void onSuccess(ListResult listResult) {
                            listUrl.clear();
                            listNames.clear();
                            final CustomGridAdapter customGridAdapter = new CustomGridAdapter(VideoFragment.this.getActivity(), listUrl);
                            grid.setAdapter(customGridAdapter);
                        /*for (StorageReference prefix : listResult.getPrefixes()) {
                            //System.out.println("prefix : "+prefix);
                            listNames.add(AlbumsActivity.eventName+prefix);
                        }*/

                            for (final StorageReference item : listResult.getItems()) {
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

                            grid.setOnItemClickListener(VideoFragment.this);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Uh-oh, an error occurred!
                        }
                    });


        } catch (Exception e) {
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            Intent toShow = new Intent(this.getActivity(), ShowMediaActivity.class);
            toShow.putExtra("type", "video");
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
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(mContext);
            //Picasso.with(mContext).load(mThumbIds.get(position)).into(imageView);
            /*Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(mThumbIds.get(position).toString(), MediaStore.Video.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(bitmap);*/

            //long thumb = getLayoutPosition()*1000;
            RequestOptions options = new RequestOptions().frame(1000);
            Glide.with(getContext()).load(mThumbIds.get(position)).placeholder(R.drawable.black).apply(options).into(imageView);

            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(320, 320));
            return imageView;
        }

    }

}
