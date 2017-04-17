package com.berg.pfredes;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.chat.Chat;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smack.chat.ChatManagerListener;
import org.jivesoftware.smack.chat.ChatMessageListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;


public class MainActivity extends AppCompatActivity {
    private Button btnLive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLive = (Button) findViewById(R.id.btn_live);
        btnLive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), PlayerActivity.class));
            }
        });

        MyLoginTask task = new MyLoginTask();
        task.execute("");
    }


    private class MyLoginTask extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... strings) {

            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setUsernameAndPassword("berg", "041097")
                    .setHost("192.168.0.2")
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .setServiceName("myserver.com")
                    .setPort(5222)
                    .setDebuggerEnabled(true)
                    .build();

            AbstractXMPPConnection con1 = new XMPPTCPConnection(config);

            try {
                con1.connect();

                if (con1.isConnected()) {
                    Log.d("App", "Connection done");
                }
                con1.login();

                if (con1.isAuthenticated()) {
                    Log.d("App", "Authentication done");

                    ChatManager chatManager = ChatManager.getInstanceFor(con1);
                    chatManager.addChatListener(new ChatManagerListener() {
                        @Override
                        public void chatCreated(Chat chat, boolean createdLocally) {
                            chat.addMessageListener(new ChatMessageListener() {
                                @Override
                                public void processMessage(Chat chat, Message message) {
                                    System.out.println("Received message: " + (message != null? message.getBody() : "NULL"));
                                }
                            });

                            Log.d("App", chat.toString());
                        }
                    });
                }
            } catch (Exception e) {
                Log.d("App", e.toString());
            }

            return "";
        }

        @Override
        protected void onPostExecute(String s) {

        }
    }
}
