package com.stairway.spotlight.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.stairway.spotlight.AccessTokenManager;
import com.stairway.spotlight.R;
import com.stairway.spotlight.MessageService;
import com.stairway.spotlight.models.AccessToken;
import com.stairway.spotlight.models.MessageResult;

import org.jivesoftware.smackx.chatstates.ChatState;

import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by vidhun on 05/07/16.
 */
public class BaseActivity extends AppCompatActivity{

    public static boolean isAppWentToBg = false;
    public static boolean isWindowFocused = false;
    public static boolean isBackPressed = false;

    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AccessToken accessToken = AccessTokenManager.getInstance().load();
        if(accessToken==null)
            throw new IllegalStateException("Base activity should be only initialized on user session");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.statusBar));
        }

        //Register MessageReceiver
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if(intent.getAction().equals(MessageService.XMPP_ACTION_RCV_MSG)) {
                    MessageResult s = (MessageResult) intent.getSerializableExtra(MessageService.XMPP_RESULT_MESSAGE);
                    Logger.d(this, "Message received: "+s);
                    onMessageReceived(s);
                } else if(intent.getAction().equals(MessageService.XMPP_ACTION_RCV_STATE)) {
                    String from = intent.getStringExtra(MessageService.XMPP_RESULT_FROM);
                    ChatState chatState = (ChatState) intent.getSerializableExtra(MessageService.XMPP_RESULT_STATE);
                    onChatStateReceived(from, chatState);
                } else if(intent.getAction().equals(MessageService.XMPP_ACTION_RCV_RECEIPT)) {
                    String chatId = intent.getStringExtra(MessageService.XMPP_RESULT_CHAT_ID);
                    String deliveryReceiptId = intent.getStringExtra(MessageService.XMPP_RESULT_RECEIPT_ID);
                    MessageResult.MessageStatus messageStatus = MessageResult.MessageStatus.valueOf(intent.getStringExtra(MessageService.XMPP_RESULT_MSG_STATUS));
                    onMessageStatusReceived(chatId, deliveryReceiptId, messageStatus);
                } else if(intent.getAction().equals(MessageService.ACTION_INTERNET_CONNECTION_STATUS)) {
                    boolean isConnectionAvailable = intent.getBooleanExtra(MessageService.ACTION_INTERNET_CONNECTION_STATUS, false);
                    onNetworkStatus(isConnectionAvailable);
                }
            }
        };
    }

    @Override
    protected void onStart() {
        onApplicationToForeground();
        IntentFilter filter = new IntentFilter(MessageService.XMPP_ACTION_RCV_STATE);
        filter.addAction(MessageService.XMPP_ACTION_RCV_MSG);
        filter.addAction(MessageService.XMPP_ACTION_RCV_RECEIPT);
        filter.addAction(MessageService.ACTION_INTERNET_CONNECTION_STATUS);
        LocalBroadcastManager.getInstance(this).registerReceiver((receiver), filter);
        super.onStart();
    }

    @Override
    protected void onStop() {
        onApplicationToBackground();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        isWindowFocused = hasFocus;
        if (isBackPressed && !hasFocus) {
            isBackPressed = false;
            isWindowFocused = true;
        }
        super.onWindowFocusChanged(hasFocus);
    }

    public void onApplicationToBackground() {
        if (!isWindowFocused) {
            isAppWentToBg = true;
            Logger.d(this, "onApplicationToBackground()");
            Logger.d(this, "Stopping MessageService");
//            stopService(new Intent(this, MessageService.class));
        }
    }

    private void onApplicationToForeground() {
        if (isAppWentToBg) {
            isAppWentToBg = false;
            Logger.d(this, "onApplicationToForeground()");
            Logger.d(this, "Starting MessageService");
//            Intent intent = new Intent(this, MessageService.class);
//            intent.putExtra(MessageService.TAG_ACTIVITY_NAME, this.getClass().getName());
//            startService(intent);
        }
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }


    public void onMessageReceived(MessageResult messageId){
        Logger.d(this, "MessageId "+messageId);
    }

    public void onNetworkStatus(boolean isAvailable) {
        if(isAvailable)
            Logger.d(this, "Internet available");
        else
            Logger.d(this, "Internet not available");
    }

    public void onChatStateReceived(String from, ChatState chatState) { Logger.d(this, "chatState: "+chatState.name()+", from "+from);}

    public void onMessageStatusReceived(String chatId, String deliveryReceiptId, MessageResult.MessageStatus messageStatus) {}
}
