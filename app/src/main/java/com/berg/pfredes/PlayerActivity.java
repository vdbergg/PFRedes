package com.berg.pfredes;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.format.DateFormat;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.database.FirebaseListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import hani.momanii.supernova_emoji_library.Helper.EmojiconTextView;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.source.TrackGroup;
import com.google.android.exoplayer2.source.chunk.MediaChunk;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;

import tv.icomp.vod.vodplayer.VodPlayer;
import tv.icomp.vod.vodplayer.trackselector.evaluator.source.Evaluator;

public class PlayerActivity extends AppCompatActivity {
    private VodPlayer vodPlayer;
    private ListView listOfMessage;

    private static int SIGN_IN_REQUEST_CODE = 1;
    private FirebaseListAdapter<ChatMessage> adapter;
    RelativeLayout activity_main;

    //Add Emojicon
    EmojiconEditText emojiconEditText;
    ImageView emojiButton,submitButton;
    EmojIconActions emojIconActions;


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_sign_out) {
            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Snackbar.make(activity_main,"You have been signed out.", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SIGN_IN_REQUEST_CODE) {
            if(resultCode == RESULT_OK) {
                Snackbar.make(activity_main,"Successfully signed in.Welcome!", Snackbar.LENGTH_SHORT).show();
                displayChatMessage();
            }
            else{
                Snackbar.make(activity_main,"We couldn't sign you in.Please try again later", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        String url = "http://liveabr2.sambatech.com.br/abr/amazonsatabr1_3e9c859611a5e7fbedc785bd33c418b5/livestream.m3u8";
        SimpleExoPlayerView exoPlayerView = (SimpleExoPlayerView) findViewById(R.id.sv_exo_player);
        vodPlayer = new VodPlayer(getApplicationContext(), exoPlayerView);
        vodPlayer.setVodPlayerListener(new VodPlayer.VodPlayerListener() {
            @Override
            public void onPrepared() {
                vodPlayer.play();
            }

            @Override
            public void onLoadingError(IOException e) {

            }

            @Override
            public void onChangeLoaderState(int i) {

            }

            @Override
            public long getSeekTo() {
                return 0;
            }
        });

        Evaluator evaluator = new Evaluator() {
            @Override
            public int getSelectedTrack(long l, MediaChunk mediaChunk, TrackGroup trackGroup, int i, BandwidthMeter bandwidthMeter) {
                return 0;
            }
        };
        vodPlayer.buildPlayer(url, C.TYPE_HLS, evaluator);

        activity_main = (RelativeLayout)findViewById(R.id.activity_player);

        //Add Emoji
        emojiButton = (ImageView)findViewById(R.id.emoji_button);
        submitButton = (ImageView)findViewById(R.id.submit_button);
        emojiconEditText = (EmojiconEditText)findViewById(R.id.emojicon_edit_text);
        emojIconActions = new EmojIconActions(getApplicationContext(),activity_main,emojiButton,emojiconEditText);
        emojIconActions.ShowEmojicon();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseDatabase.getInstance().getReference().push().setValue(new ChatMessage(emojiconEditText.getText().toString(),
                        FirebaseAuth.getInstance().getCurrentUser().getEmail()));
                emojiconEditText.setText("");
                emojiconEditText.requestFocus();
            }
        });

        //Check if not sign-in then navigate Signin page
        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),SIGN_IN_REQUEST_CODE);
        }
        else {
            Snackbar.make(activity_main,"Welcome "+FirebaseAuth.getInstance().getCurrentUser().getEmail(),Snackbar.LENGTH_SHORT).show();
            //Load content
            displayChatMessage();
        }
    }



    private void displayChatMessage() {

        listOfMessage = (ListView)findViewById(R.id.list_of_message);
        Log.d("Firebase", FirebaseDatabase.getInstance().getReference().toString());
        adapter = new FirebaseListAdapter<ChatMessage>(this,ChatMessage.class,R.layout.list_item,FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, ChatMessage model, int position) {

                //Get references to the views of list_item.xml
                TextView messageText, messageUser, messageTime;
                messageText = (EmojiconTextView) v.findViewById(R.id.message_text);
                messageUser = (TextView) v.findViewById(R.id.message_user);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                messageText.setText(model.getMessageText());
                messageUser.setText(model.getMessageUser());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm:ss)", model.getMessageTime()));
                scrollToLast(position);

            }
        };
        listOfMessage.setAdapter(adapter);
    }

    private void cleanDBFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.removeValue();
    }

    public void scrollToLast(final int position) {
        listOfMessage.clearFocus();
        listOfMessage.post(new Runnable() {
            @Override
            public void run() {
                listOfMessage.setSelection(position);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (vodPlayer != null && vodPlayer.getExoPlayer() != null) vodPlayer.getExoPlayer().release();
    }
}