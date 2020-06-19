package moezbenselem.campsite.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import moezbenselem.campsite.R;
import moezbenselem.campsite.activities.ShowMediaActivity;


public class VideoFragment extends Fragment implements
        AdapterView.OnItemClickListener{

    private static final Integer[] items = {R.drawable.com_vstechlab_lib_ic_launcher,
            R.drawable.camp_icon, R.drawable.bt_blue,
            R.drawable.bt_vert, R.drawable.chat,
            R.drawable.annonce, R.drawable.new_team};
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
            GridView grid = view.findViewById(R.id.grid_videos);
            // grid.setAdapter(new ArrayAdapter<Integer>(this, R.layout.cell,
            // items));
            grid.setAdapter(new VideoFragment.CustomGridAdapter(this.getActivity(), items));
            grid.setOnItemClickListener(this);

        }catch (Exception e){
            e.printStackTrace();
        }


        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        try {
            //String url ="https://firebasestorage.googleapis.com/v0/b/campsite-90984.appspot.com/o/message_images%2F-LX_5VWnKBdIwBP5Pjkc.jpg?alt=media&token=9acd78ff-b6f8-4d93-ab31-30771b893039";
            String url = "https://firebasestorage.googleapis.com/v0/b/campsite-90984.appspot.com/o/videos%2F-M9uAHK0GNl9nav22g4w?alt=media&token=a86bbe1e-202f-47da-9578-ebcaa393e977";
            Intent toShow = new Intent(this.getActivity(), ShowMediaActivity.class);
            toShow.putExtra("type","video");
            toShow.putExtra("url",url);
            getActivity().startActivity(toShow);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class CustomGridAdapter extends BaseAdapter {
        // Keep all Images in array
        public Integer[] mThumbIds;
        private Activity mContext;

        // Constructor
        public CustomGridAdapter(Activity mainActivity, Integer[] items) {
            this.mContext = mainActivity;
            this.mThumbIds = items;
        }

        @Override
        public int getCount() {
            return mThumbIds.length;
        }

        @Override
        public Object getItem(int position) {
            return mThumbIds[position];
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(mContext);
            imageView.setImageResource(mThumbIds[position]);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
            return imageView;
        }

    }

}
