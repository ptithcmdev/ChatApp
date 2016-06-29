package com.ptit.appchatnodejs;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ptit.model.User;
import com.ptit.supporter.mToast;
import com.ptit.utils.CircleImageBitmap;
import com.ptit.utils.ConvertImage;

public class ProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.9f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.3f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle, txtUserName, txtStatus;
    private ImageView imgAvata;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private EditText txtDisplayName, txtBirthday,txtPhoneNumber;
    private RadioGroup radGender;
    private RadioButton radMale, radFemale;
    private User userLogin;
    private Button btnEdit, btnSave;

    private LinearLayout linearTool;  // layout contain btnSave and btnEdit

    private final int REQUESTCODE_CAMERA = 0;
    private final int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        userLogin = (User) getIntent().getSerializableExtra(getString(R.string.userlogin));

        addControls();
        addEvent();

        mAppBarLayout.addOnOffsetChangedListener(this);

        mToolbar.inflateMenu(R.menu.menu_profile);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }

    private void addEvent() {

        txtUserName.setText(userLogin.getName());
//        txtStatus.setText(userLogin);
        mTitle.setText(userLogin.getName());
        if (userLogin.isGender()) { // if male
            radMale.setChecked(true);
            radFemale.setChecked(false);
        }else{
            radFemale.setChecked(true);
            radMale.setChecked(false);
        }

        ConvertImage.String_to_Image(userLogin.getAvatar(), imgAvata);

        Bitmap bitmap = ((BitmapDrawable)imgAvata.getDrawable()).getBitmap();
        bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
        bitmap = CircleImageBitmap.getCircleBitmap(bitmap);
        imgAvata.setImageBitmap(bitmap);

        registerForContextMenu(imgAvata);

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EnableText();
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.avatar_contextmenu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.itemSee:
                mToast.toastShort(ProfileActivity.this, "see photo");
                break;
            case R.id.itemCamera:
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera, REQUESTCODE_CAMERA);
                break;
            case R.id.itemSelectedPhoto:
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, RESULT_LOAD_IMAGE);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUESTCODE_CAMERA){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
            bitmap = CircleImageBitmap.getCircleBitmap(bitmap);
            imgAvata.setImageBitmap(bitmap);

            userLogin.setAvatar(ConvertImage.Image_to_String(imgAvata));

        }else if (requestCode==RESULT_LOAD_IMAGE){
            if (null != data){
                Uri uri = data.getData();
                imgAvata.setImageURI(uri);

                Bitmap bitmap = ((BitmapDrawable)imgAvata.getDrawable()).getBitmap();
                bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
                bitmap = CircleImageBitmap.getCircleBitmap(bitmap);
                imgAvata.setImageBitmap(bitmap);

                userLogin.setAvatar(ConvertImage.Image_to_String(imgAvata));
            }
        }


    }

    private void addControls() {
        mToolbar        = (Toolbar) findViewById(R.id.main_toolbar);
        mTitle          = (TextView) findViewById(R.id.main_textview_title);

        mTitleContainer = (LinearLayout) findViewById(R.id.main_linearlayout_title);
        mAppBarLayout   = (AppBarLayout) findViewById(R.id.main_appbar);

        txtUserName = (TextView) findViewById(R.id.txtUserName);
        txtStatus = (TextView) findViewById(R.id.txtStatus);
        imgAvata = (ImageView) findViewById(R.id.imgAvatar);

        txtDisplayName = (EditText) findViewById(R.id.txtDisplayname);
        txtBirthday = (EditText) findViewById(R.id.txtBirthday);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);

        radGender = (RadioGroup) findViewById(R.id.radGender);
        radMale = (RadioButton) findViewById(R.id.radMale);
        radFemale= (RadioButton) findViewById(R.id.radFemale);

        linearTool = (LinearLayout) findViewById(R.id.linearTool);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnEdit = (Button) findViewById(R.id.btnEdit);

        DisableText();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_profile, menu);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int offset) {
        int maxScroll = appBarLayout.getTotalScrollRange();
        float percentage = (float) Math.abs(offset) / (float) maxScroll;

        handleAlphaOnTitle(percentage);
        handleToolbarTitleVisibility(percentage);
    }

    private void handleToolbarTitleVisibility(float percentage) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {

            if(!mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleVisible = true;
                mToolbar.setBackgroundTintList(null);
            }

        } else {

            if (mIsTheTitleVisible) {
                startAlphaAnimation(mTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleVisible = false;
                mToolbar.setBackgroundTintList(getResources().getColorStateList(R.color.toolbarTint));
            }
        }
    }

    private void handleAlphaOnTitle(float percentage) {
        if (percentage >= PERCENTAGE_TO_HIDE_TITLE_DETAILS) {
            if(mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE);
                mIsTheTitleContainerVisible = false;
            }

        } else {

            if (!mIsTheTitleContainerVisible) {
                startAlphaAnimation(mTitleContainer, ALPHA_ANIMATIONS_DURATION, View.VISIBLE);
                mIsTheTitleContainerVisible = true;
            }
        }
    }

    public static void startAlphaAnimation (View v, long duration, int visibility) {
        AlphaAnimation alphaAnimation = (visibility == View.VISIBLE)
                ? new AlphaAnimation(0f, 1f)
                : new AlphaAnimation(1f, 0f);

        alphaAnimation.setDuration(duration);
        alphaAnimation.setFillAfter(true);
        v.startAnimation(alphaAnimation);
    }

    public void DisableText(){
        txtDisplayName.setEnabled(false);
        txtBirthday.setEnabled(false);
        txtPhoneNumber.setEnabled(false);
        txtStatus.setEnabled(false);
        radGender.setEnabled(false);

    }

    public void EnableText(){
        txtDisplayName.setEnabled(true);
        txtBirthday.setEnabled(true);
        txtPhoneNumber.setEnabled(true);
        txtStatus.setEnabled(true);
        radGender.setEnabled(true);
    }
}
