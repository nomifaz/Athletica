package com.example.athletica.ui.facility;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.athletica.R;
import com.example.athletica.data.account.LoginRegisterManager;
import com.example.athletica.data.facility.FacilityManager;
import com.example.athletica.ui.maps.MapsActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ViewFacilityActivity extends AppCompatActivity {


    private String facilityIndex;
    String userid;

    private ImageView imageView, mapBtn;
    private RatingBar ratingRatingBar;
    private TextView tvFacilityName, tvFacilityOffered, tvWebsiteLink, tvAddress;

    private int[] images;

    private FacilityManager facilityManager;

    private EditText addcomment;
    private Button sendComment;
    private Button submitButton;
    private TextView ratingDisplayTextView;
    ListView listViewComments;
    List<Comments> commentsList;

    DatabaseReference Comments_DB_Reference;
    DatabaseReference Ratings_DB_Ref;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_facility);

        imageView = findViewById(R.id.facility_imageView);
        mapBtn = findViewById(R.id.map_button);
        ratingRatingBar = findViewById(R.id.facility_rating_bar);
        tvFacilityName = findViewById(R.id.facility_name);
        tvFacilityOffered = findViewById(R.id.facilities_offered);
        tvWebsiteLink = findViewById(R.id.website);
        tvAddress = findViewById(R.id.facility_address);


        submitButton = (Button) findViewById(R.id.submit_button);
        ratingDisplayTextView = (TextView) findViewById(R.id.rating_display_text_View);
        listViewComments=(ListView)findViewById(R.id.list_view_comment);
        addcomment=findViewById(R.id.add_comment);
        sendComment=(Button)findViewById(R.id.send);
        commentsList=new ArrayList<>();


        facilityIndex = getIntent().getStringExtra("index");
        facilityManager=new FacilityManager(this,facilityIndex);

        // setting textViews

        tvFacilityName.setText(facilityManager.getName());
        tvFacilityOffered.setText(facilityManager.getFacilities());
        tvWebsiteLink.setText(facilityManager.getWebsite());
        tvAddress.setText(facilityManager.getAddress());
        images = new int[]{R.raw.i0, R.raw.i1, R.raw.i2, R.raw.i3, R.raw.i4, R.raw.i5, R.raw.i6, R.raw.i7, R.raw.i8, R.raw.i9, R.raw.i10};
        imageView.setImageResource(images[Integer.parseInt(facilityIndex) % 11]);
        mapBtn.setImageResource(R.drawable.mapicon);




        userid= LoginRegisterManager.loggedUser.getId();


        Comments_DB_Reference= FirebaseDatabase.getInstance().getReference("facility_comments");
        Ratings_DB_Ref=FirebaseDatabase.getInstance().getReference("facility_ratings");

        ListView listViewComments;
        List<Comments> commentsList;


       // int rating = 3; // get rating from DB
        //ratingBar.setRating(rating);




        // Starts the MapsActivity with the facility's location data
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double lat = facilityManager.getLat();
                double lng = facilityManager.getLong();

                Intent intent = new Intent(ViewFacilityActivity.this, MapsActivity.class);
                intent.putExtra("lat", lat);
                intent.putExtra("lng", lng);
                startActivity(intent);
            }
        });


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //addRatings();
                float submitted_rating=ratingRatingBar.getRating();
                Ratings ratings_;
                if(submitted_rating==5.0)
                    ratings_=new Ratings(1);
                else
                    ratings_=new Ratings(0);
                String id= Ratings_DB_Ref.push().getKey();

                //Ratings_DB_Ref.child(facilityIndex).setValue(ratings_);
                Ratings_DB_Ref.child(facilityIndex).setValue(ratings_);

                ratingDisplayTextView.setText("Your rating is :" + ratingRatingBar.getRating());
            }
        });



        sendComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComments();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        Comments_DB_Reference.child(facilityIndex).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                commentsList.clear();
                for(DataSnapshot commentsSnapshot: dataSnapshot.getChildren()){
                    Comments comment=commentsSnapshot.getValue(Comments.class);

                    commentsList.add(comment);
                }

                CommentAdapter adapter=new CommentAdapter(ViewFacilityActivity.this, commentsList);
                listViewComments.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    public void addRatings(){

    }
    public void addComments(){
        String comment_content=addcomment.getText().toString();
        if(!TextUtils.isEmpty(comment_content)){
            String id= Comments_DB_Reference.push().getKey();


            Comments comments_=new Comments(userid, comment_content);
            //Comments_DB_Reference=FirebaseDatabase.getInstance().getReference("facility_comments");
            //Comments_DB_Reference.child(id).setValue(comments_);

            Comments_DB_Reference.child(facilityIndex).child(id).setValue(comments_);

            addcomment.setText("");

        }
        else{
            Toast.makeText(ViewFacilityActivity.this, "Please type the content",Toast.LENGTH_LONG);
        }

    }


}
