package com.example.samhattangady.mymdb;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_details, new DetailsFragment())
                    .commit();
        }

    }

    public class DetailsFragment extends Fragment {

        public DetailsFragment() {

        }

        public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

            View rootView = inflater.inflate(R.layout.fragment_details, container, false);
            Intent intent = getIntent();

            if (intent != null) {

                Bundle data = intent.getExtras();
                DisplayStruct displayStruct = (DisplayStruct) data.getParcelable("DisplayStruct");

                if (displayStruct != null) {

                    TextView textViewTitle = (TextView) rootView.findViewById(R.id.title_of_movie);
                    textViewTitle.setText(displayStruct.getName());

                    TextView textViewOverview = (TextView) rootView.findViewById(R.id.overview);
                    textViewOverview.setText(displayStruct.overview);

                    ImageView imageView = (ImageView) rootView.findViewById(R.id.backdrop);
                    Picasso.with(getContext())
                            .load("http://image.tmdb.org/t/p/w500" + displayStruct.getBackdrop())
                            .placeholder(R.mipmap.place_holder)
                            .into(imageView);
                }
            }
            return rootView;
        }

    }
}
