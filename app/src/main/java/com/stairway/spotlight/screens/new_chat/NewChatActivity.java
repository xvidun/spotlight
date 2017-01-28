package com.stairway.spotlight.screens.new_chat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.stairway.data.config.Logger;
import com.stairway.data.source.user.UserSessionResult;
import com.stairway.spotlight.R;
import com.stairway.spotlight.core.BaseActivity;
import com.stairway.spotlight.core.di.component.ComponentContainer;
import com.stairway.spotlight.core.lib.AndroidUtils;
import com.stairway.spotlight.screens.message.MessageActivity;
import com.stairway.spotlight.screens.new_chat.di.NewChatViewModule;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

/**
 * Created by vidhun on 08/01/17.
 */

public class NewChatActivity extends BaseActivity implements NewChatContract.View, NewChatAdapter.ContactClickListener{
    @Bind(R.id.rv_contact_list)
    RecyclerView contactList;

    @Bind(R.id.tb_new_chat)
    Toolbar toolbar;

    @Bind(R.id.et_new_chat_search1)
    EditText toolbarSearch;

    @Bind(R.id.tv_new_chat_title)
    TextView toolbarTitle;

    @Inject
    NewChatPresenter newChatPresenter;

    NewChatAdapter newChatAdapter;

    @Bind(R.id.ll_new_chat)
    LinearLayout newChatLayout;

    private PopupWindow addContactPopupWindow;
    private View addContactPopupView;
    private UserSessionResult userSession;
    private boolean showSoftInput;

    private static final String KEY_SHOW_SOFT_INPUT = "KEY_SHOW_SOFT_INPUT";

    public static Intent callingIntent(Context context, boolean showSoftInput) {
        Intent intent = new Intent(context, NewChatActivity.class);
        intent.putExtra(KEY_SHOW_SOFT_INPUT, showSoftInput);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_chat);

        Intent receivedIntent = getIntent();
        if(!receivedIntent.hasExtra(KEY_SHOW_SOFT_INPUT))
            return;

        showSoftInput = receivedIntent.getBooleanExtra(KEY_SHOW_SOFT_INPUT, false);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        if(showSoftInput) {
            toolbarSearch.setVisibility(View.VISIBLE);
            toolbarTitle.setVisibility(View.GONE);
            AndroidUtils.showSoftInput(this, toolbarSearch);
        } else {
            AndroidUtils.hideSoftInput(this);
            toolbarSearch.setVisibility(View.GONE);
            toolbarTitle.setVisibility(View.VISIBLE);
        }

        newChatPresenter.attachView(this);
        newChatPresenter.initContactList();

        contactList.setLayoutManager(new LinearLayoutManager(this));

        Activity newChatActivity = this;
        contactList.setOnFlingListener(new RecyclerView.OnFlingListener() {
            @Override
            public boolean onFling(int velocityX, int velocityY) {
                AndroidUtils.hideSoftInput(newChatActivity);
                return false;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if((id == android.R.id.home)) {
            AndroidUtils.hideSoftInput(this);
            super.onBackPressed();
            return true;
        } else if(id == R.id.action_add_contact) {
            showAddContactPopup();
        } else if(id == R.id.action_search) {
            toolbarSearch.setVisibility(View.VISIBLE);
            AndroidUtils.showSoftInput(this,toolbarSearch);
            toolbarTitle.setVisibility(View.GONE);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(showSoftInput)
            getMenuInflater().inflate(R.menu.new_chat_toolbar, menu);
        else
            getMenuInflater().inflate(R.menu.contacts_toolbar, menu);
        return true;
    }

    public void showContactAddedSuccess(String name, String username, boolean isExistingContact) {
        addContactPopupWindow.dismiss();
        newChatPresenter.initContactList();
        AndroidUtils.hideSoftInput(this);

        String message;
        if(isExistingContact)
            message = name+" is already in your contacts on iChat.";
        else
            message = name+" is added to your contacts on iChat.";

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);
        View addedContactView = inflater.inflate(R.layout.added_contact_popup, null);
        PopupWindow addedPopupWindow = new PopupWindow(
                addedContactView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );
        if(Build.VERSION.SDK_INT>=21)
            addedPopupWindow.setElevation(5.0f);
        addedPopupWindow.showAtLocation(newChatLayout, Gravity.CENTER,0,0);

        RelativeLayout out = (RelativeLayout) addedContactView.findViewById(R.id.fl_added_contact);
        out.setOnClickListener(view -> {
            addedPopupWindow.dismiss();
        });

        Button sendMessage = (Button) addedContactView.findViewById(R.id.btn_send_message);
        sendMessage.setOnClickListener(v1 -> {
            addedPopupWindow.dismiss();
            startActivity(MessageActivity.callingIntent(this, username));
        });

        TextView resultMessage = (TextView) addedContactView.findViewById(R.id.tv_add_result_message);
        resultMessage.setText(message);
    }

    @Override
    public void showInvalidIDError() {
        if(addContactPopupWindow.isShowing()) {
            ProgressBar pb = (ProgressBar) addContactPopupView.findViewById(R.id.pb_add_contact);
            pb.setVisibility(View.GONE);
            showAlertDialog("Please enter a valid iChat ID.", R.layout.alert);
        }
    }

    private void showAddContactPopup() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(LAYOUT_INFLATER_SERVICE);

        addContactPopupView = inflater.inflate(R.layout.add_contact_popup,null);
        addContactPopupWindow = new PopupWindow(
                addContactPopupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
                true
        );
        if(Build.VERSION.SDK_INT>=21)
            addContactPopupWindow.setElevation(5.0f);
        addContactPopupWindow.showAtLocation(newChatLayout, Gravity.CENTER,0,0);
        addContactPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);

        FrameLayout outLayout = (FrameLayout) addContactPopupView.findViewById(R.id.fl_add_contact);
        outLayout.setOnClickListener(v -> {
            addContactPopupWindow.dismiss();
            AndroidUtils.hideSoftInput(this);
        });

        EditText enterId = (EditText) addContactPopupView.findViewById(R.id.et_add_contact);
        enterId.requestFocus();
        AndroidUtils.showSoftInput(this, enterId);

        Button addButton = (Button) addContactPopupView.findViewById(R.id.btn_add_contact);
        addButton.setOnClickListener(v -> {
            if(enterId.getText().length()>0) {
                newChatPresenter.addContact(enterId.getText().toString(), userSession.getAccessToken());
                ProgressBar pb = (ProgressBar) addContactPopupView.findViewById(R.id.pb_add_contact);
                pb.setVisibility(View.VISIBLE);
            }
        });

        // popup not working in older versions
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.KITKAT)
            newChatLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener(){
                public void onGlobalLayout(){
                    int heightDiff = newChatLayout.getRootView().getHeight()- newChatLayout.getHeight();
                    // IF height diff is more then 150, consider keyboard as visible.
                    Logger.d(this, "DIFF: "+heightDiff);
                    RelativeLayout content = (RelativeLayout) addContactPopupView.findViewById(R.id.rl_add_contact_content);
                    FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)content.getLayoutParams();
                    if(heightDiff>150) {
                        int px = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, -110, getResources().getDisplayMetrics());
                        params.setMargins(0, px, 0, 0);
                        content.setLayoutParams(params);
                    } else {
                        params.setMargins(0, 0, 0, 0);
                        content.setLayoutParams(params);
                    }
                }
            });
    }

    public void showAlertDialog(String message, int layout) {
        //TODO: Something wrong. 16?
        final int WIDTH = 294, HEIGHT = 98;

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(layout, null);

        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, WIDTH+16, getResources().getDisplayMetrics());
        int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT+16, getResources().getDisplayMetrics());
        alertDialog.getWindow().setLayout(width, height);

        TextView messageText = (TextView) dialogView.findViewById(R.id.tv_alert_message);
        messageText.setText(message);
        Button ok = (Button) dialogView.findViewById(R.id.btn_alert_ok);
        ok.setOnClickListener(v -> alertDialog.dismiss());
    }

    @Override
    public void onContactItemClicked(String userId) {
        AndroidUtils.hideSoftInput(this);
        startActivity(MessageActivity.callingIntent(this, userId));
    }

    @Override
    public void displayContacts(List<NewChatItemModel> newChatItemModel) {
        newChatAdapter = new NewChatAdapter(this, this, newChatItemModel);
        contactList.setAdapter(newChatAdapter);
    }

    @OnTextChanged(R.id.et_new_chat_search1)
    public void onSearchChanged() {
        newChatAdapter.filterList(toolbarSearch.getText().toString());
    }

    @Override
    protected void injectComponent(ComponentContainer componentContainer) {
        componentContainer.userSessionComponent().plus(new NewChatViewModule()).inject(this);
        userSession = componentContainer.userSessionComponent().getUserSession();
    }
}