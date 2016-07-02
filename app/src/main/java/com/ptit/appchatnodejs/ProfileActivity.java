package com.ptit.appchatnodejs;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.github.nkzawa.socketio.client.Socket;
import com.ptit.model.User;
import com.ptit.supporter.mToast;
import com.ptit.utils.ConvertImage;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class ProfileActivity extends AppCompatActivity implements AppBarLayout.OnOffsetChangedListener{

    private static final float PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR  = 0.8f;
    private static final float PERCENTAGE_TO_HIDE_TITLE_DETAILS     = 0.4f;
    private static final int ALPHA_ANIMATIONS_DURATION              = 200;

    private boolean mIsTheTitleVisible          = false;
    private boolean mIsTheTitleContainerVisible = true;

    private LinearLayout mTitleContainer;
    private TextView mTitle, txtUserName, txtStatus, txtBirthday, txtChangePassword;
    private ImageView imgAvata, imgBackground;
    private AppBarLayout mAppBarLayout;
    private Toolbar mToolbar;
    private EditText txtDisplayName,txtPhoneNumber;
    private RadioGroup radGender;
    private RadioButton radMale, radFemale;
    private User userLogin;
    private Button btnEditAndSave;

    private LinearLayout linearTool;  // layout contain btnSave and btnEdit

    private DatePickerDialog dialogBirthday;
    private SimpleDateFormat dateFormatter;

    private final int REQUESTCODE_CAMERA_FOR_AVATAR = 0;
    private final int REQUESTCODE_CAMERA_FOR_BACKGROUND = 1;
    private final int RESULT_LOAD_IMAGE_FOR_AVATAR = 2;
    private final int RESULT_LOAD_IMAGE_FOR_BACKGROUND = 3;
    private final int AVATAR = 4;
    private final int BACKGROUND= 5;

    private static Socket mSocket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        getDataFromAnotherActivity();
        addControls();
        addEvent();

        mAppBarLayout.addOnOffsetChangedListener(this);

        mToolbar.inflateMenu(R.menu.menu_profile);
        startAlphaAnimation(mTitle, 0, View.INVISIBLE);
    }

    private void getDataFromAnotherActivity() {
        mSocket = MainActivity.mSocket;

        userLogin = (User) getIntent().getSerializableExtra(getString(R.string.userlogin));
        userLogin.setBirthday("01-01-2010");
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

//        Bitmap bitmap = ((BitmapDrawable)imgAvata.getDrawable()).getBitmap();
//        bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
//        bitmap = CircleImageBitmap.getCircleBitmap(bitmap);
//        imgAvata.setImageBitmap(bitmap);

//        registerForContextMenu(imgAvata);
        imgAvata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                showDialogChangeImage(AVATAR);
            }
        });

        imgBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChangeImage(BACKGROUND);
            }
        });

        txtBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogDate();
            }
        });

        txtChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogChangePassword();
            }
        });
        btnEditAndSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveChangeInformationProfile();
            }
        });
    }

    private void saveChangeInformationProfile() {
        if (btnEditAndSave.getText().toString().equals(getString(R.string.edit))){
            EnableText();
            btnEditAndSave.setText(getString(R.string.save));
        }else { // SAVE

        }
    }

    private void showDialogChangePassword() {
        final Dialog dialogChangePass = new Dialog(this);
        dialogChangePass.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangePass.setContentView(R.layout.dialog_change_password);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogChangePass.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        final EditText txtOldPass, txtNewPass, txtConfirm;
        Button btnSave, btnCancel;
        ImageButton btnShowOldPass,btnShowNewPass, btnShowConfirmPass;

        txtOldPass = (EditText) dialogChangePass.findViewById(R.id.txtOldPassword);
        txtNewPass = (EditText) dialogChangePass.findViewById(R.id.txtNewPassword);
        txtConfirm= (EditText) dialogChangePass.findViewById(R.id.txtConfirmPassword);

        btnSave = (Button) dialogChangePass.findViewById(R.id.btnSave);
        btnCancel= (Button) dialogChangePass.findViewById(R.id.btnCancel);

        btnShowOldPass = (ImageButton) dialogChangePass.findViewById(R.id.btnShowOld);
        btnShowNewPass= (ImageButton) dialogChangePass.findViewById(R.id.btnShowNew);
        btnShowConfirmPass = (ImageButton) dialogChangePass.findViewById(R.id.btnShowConfirm);

        btnShowOldPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTypeText(txtOldPass);
            }
        });

        btnShowNewPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTypeText(txtNewPass);
            }
        });

        btnShowConfirmPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChangeTypeText(txtConfirm);
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (txtOldPass.getText().toString().equals(userLogin.getPassword())){
                    if (txtNewPass.getText().toString().trim().equals("")){
                        mToast.toastShort(ProfileActivity.this, getString(R.string.dontEnterNewPass));
                        txtNewPass.requestFocus();
                    }else if (txtConfirm.getText().toString().trim().equals("")){
                            mToast.toastShort(ProfileActivity.this, getString(R.string.dontEnterConfirmPass));
                            txtConfirm.requestFocus();
                    }else
                        if (txtNewPass.getText().toString().equals(txtConfirm.getText().toString())){
                            userLogin.setPassword(txtNewPass.getText().toString());
                            mSocket.emit("client-change-password", userLogin.getName(), userLogin.getPassword());
                        }else {
                            mToast.toastShort(ProfileActivity.this, getString(R.string.error_confirmPass));
                            txtConfirm.requestFocus();
                        }
                }else {
                    mToast.toastShort(ProfileActivity.this, getString(R.string.error_OldPass));
                    txtOldPass.requestFocus();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogChangePass.dismiss();
            }
        });
        dialogChangePass.setCanceledOnTouchOutside(false);
        dialogChangePass.show();
        dialogChangePass.getWindow().setAttributes(lp);
    }

    private void ChangeTypeText(EditText text) {
        if (text.getTransformationMethod() == PasswordTransformationMethod.getInstance())
            text.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        else
            text.setTransformationMethod(PasswordTransformationMethod.getInstance());

    }

    private void showDialogDate() {
        Calendar newCalendar = Calendar.getInstance();
        String []arr = userLogin.getBirthday().split("-");
        newCalendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(arr[0]));
        newCalendar.set(Calendar.MONTH, Integer.valueOf(arr[1]));
        newCalendar.set(Calendar.YEAR, Integer.valueOf(arr[2]));
        dialogBirthday = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar newDate = Calendar.getInstance();
                newDate.set(year, monthOfYear, dayOfMonth);
                txtBirthday.setText(dateFormatter.format(newDate.getTime()));
            }

        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        dialogBirthday.show();
    }

    private void showDialogChangeImage(final int request) {
        Dialog dialogChangeImage = new Dialog(this);
        dialogChangeImage.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogChangeImage.setContentView(R.layout.dialog_change_image);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogChangeImage.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;

        TextView txtSeePhoto, txtTakePhoto, txtSelectedPhoto, txtTitle;
        txtTitle = (TextView) dialogChangeImage.findViewById(R.id.txtTitle);
        txtSeePhoto = (TextView) dialogChangeImage.findViewById(R.id.txtSeePhoto);
        txtTakePhoto= (TextView) dialogChangeImage.findViewById(R.id.txtTakePhoto);
        txtSelectedPhoto = (TextView) dialogChangeImage.findViewById(R.id.txtSelectedPhotoFromGallery);

        if (request==AVATAR)
            txtTitle.setText(getString(R.string.ChangeAvatar));
        else
            txtTitle.setText(getString(R.string.ChangeBackground));

        txtSeePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogSeePhoto(request);
            }
        });
        txtTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intentCamera, request==AVATAR ? REQUESTCODE_CAMERA_FOR_AVATAR : REQUESTCODE_CAMERA_FOR_BACKGROUND);
            }
        });
        txtSelectedPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, request==AVATAR ? RESULT_LOAD_IMAGE_FOR_AVATAR: RESULT_LOAD_IMAGE_FOR_BACKGROUND);
            }
        });
        dialogChangeImage.show();
        dialogChangeImage.getWindow().setAttributes(lp);
    }

    private void showDialogSeePhoto(int request) {
        Dialog dialogSeePhoto = new Dialog(this);
        dialogSeePhoto.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogSeePhoto.setContentView(R.layout.dialog_see_photo);

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialogSeePhoto.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;


        ImageView imgPhoto = (ImageView) dialogSeePhoto.findViewById(R.id.imgPhoto);

        if (request==AVATAR)
             ConvertImage.String_to_Image(userLogin.getAvatar(), imgPhoto);
        else
            imgPhoto.setImageBitmap(((BitmapDrawable)imgBackground.getDrawable()).getBitmap());

        dialogSeePhoto.show();
        dialogSeePhoto.getWindow().setAttributes(lp);
    }

//    @Override
//    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//        super.onCreateContextMenu(menu, v, menuInfo);
//        getMenuInflater().inflate(R.menu.avatar_contextmenu, menu);
//    }
//
//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()){
//            case R.id.itemSee:
//                mToast.toastShort(ProfileActivity.this, "see photo");
//                break;
//            case R.id.itemCamera:
//                Intent intentCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                startActivityForResult(intentCamera, REQUESTCODE_CAMERA);
//                break;
//            case R.id.itemSelectedPhoto:
//                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                startActivityForResult(intent, RESULT_LOAD_IMAGE);
//        }
//        return true;
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( (requestCode == REQUESTCODE_CAMERA_FOR_AVATAR
              || requestCode == REQUESTCODE_CAMERA_FOR_BACKGROUND)  && resultCode!=0){
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");

            if (requestCode==REQUESTCODE_CAMERA_FOR_AVATAR){
                imgAvata.setImageBitmap(bitmap);
                userLogin.setAvatar(ConvertImage.Image_to_String(imgAvata));
            }
            else {
                imgBackground.setImageBitmap(bitmap);
                // user set background
            }


        }else if (requestCode==RESULT_LOAD_IMAGE_FOR_AVATAR || requestCode == RESULT_LOAD_IMAGE_FOR_BACKGROUND){
            if (null != data){
                Uri uri = data.getData();
//                imgAvata.setImageURI(uri);

//                Bitmap bitmap = ((BitmapDrawable)imgAvata.getDrawable()).getBitmap();
//                bitmap = Bitmap.createScaledBitmap(bitmap,80,80,true);
//                bitmap = CircleImageBitmap.getCircleBitmap(bitmap);

                if (requestCode == RESULT_LOAD_IMAGE_FOR_AVATAR){
                    imgAvata.setImageURI(uri);
                    userLogin.setAvatar(ConvertImage.Image_to_String(imgAvata));
                }else{
                    imgBackground.setImageURI(uri);
                    // user set background
                }

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
        imgBackground = (ImageView) findViewById(R.id.imgBackground);

        txtDisplayName = (EditText) findViewById(R.id.txtDisplayname);
        txtBirthday = (TextView) findViewById(R.id.txtBirthday);
        txtPhoneNumber = (EditText) findViewById(R.id.txtPhoneNumber);
        txtChangePassword = (TextView) findViewById(R.id.txtChangePassword);

        radGender = (RadioGroup) findViewById(R.id.radGender);
        radMale = (RadioButton) findViewById(R.id.radMale);
        radFemale= (RadioButton) findViewById(R.id.radFemale);

        linearTool = (LinearLayout) findViewById(R.id.linearTool);
        btnEditAndSave = (Button) findViewById(R.id.btnEditAndSave);

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy");

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
        radFemale.setEnabled(false);
        radMale.setEnabled(false);
    }

    public void EnableText(){
        txtDisplayName.setEnabled(true);
        txtBirthday.setEnabled(true);
        txtPhoneNumber.setEnabled(true);
        txtStatus.setEnabled(true);
        radGender.setEnabled(true);
        radFemale.setEnabled(true);
        radMale.setEnabled(true);
    }
}
