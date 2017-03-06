package com.stairway.spotlight.screens.user_profile;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stairway.spotlight.R;
import com.stairway.spotlight.core.BaseActivity;
import com.stairway.spotlight.core.lib.AndroidUtils;
import com.stairway.spotlight.core.lib.ImageUtils;
import com.stairway.spotlight.screens.shared_media.SharedMediaActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
public class UserProfileActivity extends BaseActivity {
    @Bind(R.id.iv_userprofile_dp)
    ImageView profileDP;

    @Bind(R.id.tb_user_profile)
    Toolbar toolbar;

    @Bind(R.id.user_profile_message)
    FloatingActionButton fab;

    @Bind(R.id.tv_contact_name)
    TextView contactNameView;

    @Bind(R.id.tv_user_profile_id)
    TextView userIdView;

    private static int RESULT_LOAD_IMAGE = 1;
    private String username;
    private String contactName;
    private String userId;

    private static String KEY_USER_NAME = "UserProfileActivity.USER_NAME";
    private static String KEY_CONTACT_NAME = "UserProfileActivity.CONTACT_NAME";
    public static Intent callingIntent(Context context, String userId, String contactName) {
        Intent intent = new Intent(context, UserProfileActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra(KEY_USER_NAME, userId);
        intent.putExtra(KEY_CONTACT_NAME, contactName);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        ButterKnife.bind(this);
        Intent receivedIntent = getIntent();
        if(!receivedIntent.hasExtra(KEY_USER_NAME))
            return;

        username = receivedIntent.getStringExtra(KEY_USER_NAME);
        contactName = receivedIntent.getStringExtra(KEY_CONTACT_NAME);
        userId = "airtel";

        contactNameView.setText(contactName);
        userIdView.setText(userId);

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        profileDP.setImageDrawable(ImageUtils.getDefaultProfileImage(contactName, username, 25.5));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_toolbar, menu);

        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            onBackPressed();
            return true;
        } else if(id == R.id.action_block_contact) {
            LinearLayout parent = new LinearLayout(this);

            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setPadding((int)AndroidUtils.px(24), (int)AndroidUtils.px(8), (int)AndroidUtils.px(24), 0);

            TextView textView1 = new TextView(this);
            textView1.setText("Are you sure want to block this contact?");
            textView1.setTextColor(ContextCompat.getColor(this, R.color.textColor));
            textView1.setTextSize(16);

            parent.addView(textView1);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setPositiveButton("OK", ((dialog, which) -> {}));
            builder.setNegativeButton("CANCEL", ((dialog, which) -> {}));
            builder.setView(parent);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if(id == R.id.action_delete_contact) {
            LinearLayout parent = new LinearLayout(this);

            parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
            parent.setOrientation(LinearLayout.VERTICAL);
            parent.setPadding((int)AndroidUtils.px(24), (int)AndroidUtils.px(8), (int)AndroidUtils.px(24), 0);

            TextView textView1 = new TextView(this);
            textView1.setText("Are you sure want to delete this contact?");
            textView1.setTextColor(ContextCompat.getColor(this, R.color.textColor));
            textView1.setTextSize(16);

            parent.addView(textView1);

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getResources().getString(R.string.app_name));
            builder.setPositiveButton("OK", ((dialog, which) -> {}));
            builder.setNegativeButton("CANCEL", ((dialog, which) -> {}));
            builder.setView(parent);
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        } else if(id == R.id.action_add_shortcut) {

        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.user_profile_message)
    public void onMessageClicked() {
        onBackPressed();
    }

    @OnClick(R.id.profile_first_line)
    public void onUserIDClicked() {
        LinearLayout parent = new LinearLayout(this);

        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding((int) AndroidUtils.px(16),(int)AndroidUtils.px(8), 0, (int)AndroidUtils.px(8));

        TextView textView1 = new TextView(this);
        textView1.setText("Copy");
        textView1.setTextColor(ContextCompat.getColor(this, R.color.textColor));
        textView1.setTextSize(16);
        textView1.setGravity(Gravity.CENTER_VERTICAL);
        textView1.setHeight((int)AndroidUtils.px(48));

        parent.addView(textView1);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(parent);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        textView1.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) this.getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("userId", userId);
            clipboard.setPrimaryClip(clip);
            alertDialog.dismiss();
        });
    }

    @OnClick(R.id.user_profile_shared_media)
    public void onSharedMediaClicked() {
        startActivity(SharedMediaActivity.callingIntent(this, userId));
    }

    @OnClick(R.id.user_profile_notifications)
    public void onNotificationsClicked() {
        TextView t1, t2, t3, t4, t5;
        LinearLayout parent = new LinearLayout(this);

        parent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayoutCompat.LayoutParams.MATCH_PARENT, LinearLayoutCompat.LayoutParams.WRAP_CONTENT));
        parent.setOrientation(LinearLayout.VERTICAL);
        parent.setPadding((int)AndroidUtils.px(24), (int)AndroidUtils.px(8), 0, (int)AndroidUtils.px(8));

        t1 = new TextView(this);
        t1.setText("Turn On");
        t1.setTextColor(ContextCompat.getColor(this, R.color.textColor));
        t1.setTextSize(16);
        t1.setHeight((int)AndroidUtils.px(48));
        t1.setGravity(Gravity.CENTER_VERTICAL);
        t1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_volume_up_dark, 0, 0, 0);
        t1.setCompoundDrawablePadding((int)AndroidUtils.px(24));

//        t2 = new TextView(this);
//        t2.setText("Mute for 1 hour");
//        t2.setTextColor(ContextCompat.getColor(this, R.color.textColor));
//        t2.setTextSize(16);
//        t2.setHeight((int)AndroidUtils.px(48));
//        t2.setGravity(Gravity.CENTER_VERTICAL);
//
//        t3 = new TextView(this);
//        t3.setText("Mute for 2 days");
//        t3.setTextColor(ContextCompat.getColor(this, R.color.textColor));
//        t3.setTextSize(16);
//        t3.setHeight((int)AndroidUtils.px(48));
//        t3.setGravity(Gravity.CENTER_VERTICAL);
//
//        t4 = new TextView(this);
//        t4.setText("Customize");
//        t4.setTextColor(ContextCompat.getColor(this, R.color.textColor));
//        t4.setTextSize(16);
//        t4.setHeight((int)AndroidUtils.px(48));
//        t4.setGravity(Gravity.CENTER_VERTICAL);

        t5 = new TextView(this);
        t5.setText("Turn Off");
        t5.setTextColor(ContextCompat.getColor(this, R.color.textColor));
        t5.setTextSize(16);
        t5.setHeight((int)AndroidUtils.px(48));
        t5.setGravity(Gravity.CENTER_VERTICAL);
        t5.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_volume_off_dark, 0, 0, 0);
        t5.setCompoundDrawablePadding((int)AndroidUtils.px(24));

        parent.addView(t1);
        parent.addView(t5);
//        parent.addView(t3);
//        parent.addView(t4);
//        parent.addView(t5);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Notifications");
        builder.setView(parent);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

        t1.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
        t5.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
//        t3.setOnClickListener(v -> {
//            alertDialog.dismiss();
//        });
//        t4.setOnClickListener(v -> {
//            alertDialog.dismiss();
//        });
//        t5.setOnClickListener(v -> {
//
//        });
    }

//    @OnClick(R.id.iv_userprofile_dp)
//    public void onProfileClicked() {
//        Logger.d(this, "Profile clicked");
//        Intent loadIntent = new Intent(Intent.ACTION_PICK,
//                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//        startActivityForResult(loadIntent, RESULT_LOAD_IMAGE);
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
//            Uri selectedImage = data.getData();
//            Logger.d(this, selectedImage.toString());
//            String[] filePathColumn = { MediaStore.Images.Media.DATA };
//
//            Cursor cursor = getContentResolver().query(selectedImage,
//                    filePathColumn, null, null, null);
//            cursor.moveToFirst();
//
//            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
//            String picturePath = cursor.getString(columnIndex);
//            cursor.close();
//
//            profileDP.setImageBitmap(BitmapFactory.decodeFile(picturePath));
//        }
//    }
}