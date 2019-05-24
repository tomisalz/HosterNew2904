package com.example.hoster;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

public class edit_profile extends AppCompatActivity {
    private TextView name;
    private TextView uni;
    private TextView langs;
    private TextView loc;
    private ImageButton accept;
    private ImageButton cancel;
    private CircleImageView edit_profile_pic;
    private final int PICK_IMAGE_REQUEST = 1;
    Uri imageUri;
    StorageReference mSorageRef;
    DatabaseReference mDataBaseRef;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDataBaseRef = Server.getInstance().mDb.getReference();
        mSorageRef = Server.getInstance().storage.getReference("ProfilePics");

        getWindow().setStatusBarColor(this.getResources().getColor(R.color.TextGrey));

        setContentView(R.layout.activity_edit_profile);

        final String uId = MainActivity.userId;

        final User[] user = new User[1]; // server will insert our user here

        Toolbar toolbar = (Toolbar) findViewById(R.id.profile_toolbar);
        setSupportActionBar(toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.TextYellow));

        name = findViewById(R.id.editable_name_of_profile);
        name.setHint(MainActivity.user.getDisplayName());
        uni = findViewById(R.id.editable_uni_name);
        uni.setHint(MainActivity.user.getEmail());
        edit_profile_pic = (CircleImageView) findViewById(R.id.edit_profilepic);

        // TODO - ADD location to student, currently not in object
        //

        langs = findViewById(R.id.editable_profile_langs);
        Server.getInstance().getUser(uId, user, name, uni, langs);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);



        //clicks
        accept = findViewById(R.id.accept);
        cancel = findViewById(R.id.cancel);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = getIntent().getExtras();
                Intent profileIntent = new Intent(getApplicationContext(), Profile.class);

                b.putString("userId", MainActivity.userId);
                profileIntent.putExtras(b);
                //Todo to get connection to sever and edit profile fields from there
                String newName = name.getText().toString();
                String newUni = uni.getText().toString();

                String[] stringLangs = langs.getText().toString().split(",");

                ArrayList<String> newLangs = new ArrayList<>(Arrays.asList(stringLangs));

                Server.getInstance().editUser(user[0], newName,null, newUni, newLangs );

                


                startActivity(profileIntent);
                finish();

            }
        });

        edit_profile_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);

                startActivityForResult(Intent.createChooser(gallery, "Select Picture"), PICK_IMAGE_REQUEST);



            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle b = new Bundle();
                b.putString("userId", MainActivity.userId);

                Intent profileIntent = new Intent(getApplicationContext(), Profile.class);
                profileIntent.putExtras(b); //Put your id to your next Intent
                startActivity(profileIntent);
            }
        });


    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {

            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                edit_profile_pic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private String getFileExtension(Uri uri){
        ContentResolver cR = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
