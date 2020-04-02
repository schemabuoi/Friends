package com.example.friends.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.friends.R;
import com.example.friends.entity.Friend;
import com.example.friends.view.util.BitmapResolver;
import com.example.friends.view.util.ImageFileHandler;
import com.example.friends.viewmodel.AddFriendViewModel;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/*
    This activity should be refactored later on and split into fragments.
    It contains too much code and responsibilities then it should.
 */
public class AddFriendActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {

    public static final String TAG = "AddFriendActivity";

    /*
        Since getting permissions is strictly connected to performing
        an action that requires those permissions I am using same
        result code for checking permission and starting activity
        connected to those permissions
    */
    public static final int CAMERA_REQUEST = 1;
    public static final int GALLERY_REQUEST = 2;

    /* Date pattern for displaying birthday */
    public static final String DATE_PATTERN = "dd/MM/yyyy";

    /*
        Menu items for dialog box with possible options of
        setting friends profile picture
    */
    public static final String TAKE_PHOTO_MENU_ITEM = "Take Photo";
    public static final String CHOOSE_PHOTO_MENU_ITEM = "Choose from Library";
    public static final String CANCEL_MENU_ITEM = "Cancel";

    private ImageView imgProfilePicture;
    private TextView etName;
    private TextView etPhone;
    private MaterialButton btnBirthday;
    private TextView etEmail;
    private TextView etWebsite;
    private ImageView imgFavorite;
    private MaterialButton btnSave;

    private AddFriendViewModel addFriendViewModel;
    private Calendar calendar;
    private SimpleDateFormat dateFormatter;

    /*
        Path to currently visible profile picture,
        either selected from gallery or taken using
        camera. Used to send along with the other
        friends data on save.
     */
    private String profilePicturePath;

    /*
        Uri to image file that is used by camera to save
        image taken by the user. It is used in onActivityResult
        to set ImageView and add image to public external storage
     */
    private Uri cameraImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);

        initializeViews();
        initializeViewModel();
        initializeDateObjects();
        setUpActionBar();
        initializeDefaultValues();
        initalizeListeners();
    }

    private void initializeDateObjects()
    {
        calendar = Calendar.getInstance();
        dateFormatter = new SimpleDateFormat(DATE_PATTERN);
    }

    private void initializeViews()
    {
        imgProfilePicture = findViewById(R.id.imgProfilePicture);
        etName = findViewById(R.id.etName);
        etPhone = findViewById(R.id.etPhone);
        btnBirthday = findViewById(R.id.btnBirthday);
        etEmail = findViewById(R.id.etEmail);
        etWebsite = findViewById(R.id.etWebsite);
        imgFavorite = findViewById(R.id.imgFavorite);
        btnSave = findViewById(R.id.btnSave);
    }

    /*
        Method for setting action bar to our own customized toolbar
     */
    private void setUpActionBar()
    {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_back);
    }

    private void initializeDefaultValues()
    {
        btnBirthday.setText(dateFormatter.format(calendar.getTime()));
        imgFavorite.setImageResource(R.drawable.ic_favorite_false);
        imgFavorite.setTag(R.drawable.ic_favorite_false);
        btnSave.setEnabled(false);
    }

    /*
        In the future this code should be replaced by using
        dagger dependency injection tool
     */
    private void initializeViewModel()
    {
        addFriendViewModel = new ViewModelProvider(this).get(AddFriendViewModel.class);
        addFriendViewModel.initialize(getApplication());
    }

    private void initalizeListeners()
    {
        imgProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openSelectPictureDialog();
            }
        });
        etName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                checkEnableSave();
            }
        });
        btnBirthday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog();
            }
        });
        imgFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickFavorite();
            }
        });
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clickSave();
            }
        });
    }

    /*
        Opens dialog for letting user select which option he wants to use
        to set friends profile picture.
     */
    private void openSelectPictureDialog()
    {
        final CharSequence[] items = initializeMenuItems();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Select Profile Picture");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(TAKE_PHOTO_MENU_ITEM))
                {
                    takePhotoCheckPermission();
                }
                else if (items[item].equals(CHOOSE_PHOTO_MENU_ITEM))
                {
                    choosePhotoCheckPermission();
                }
                else if (items[item].equals(CANCEL_MENU_ITEM))
                {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /*
        This method is needed if user does not have a camera in his
        device. Then we should not show Take Photo option in a dialog
        box
    */
    private CharSequence[] initializeMenuItems()
    {
        if(getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY))
        {
            return new CharSequence[] {TAKE_PHOTO_MENU_ITEM, CHOOSE_PHOTO_MENU_ITEM, CANCEL_MENU_ITEM};
        }
        else
        {
            return new CharSequence[] {CHOOSE_PHOTO_MENU_ITEM, CANCEL_MENU_ITEM};
        }
    }

    /*
        Checks for required permission for taking photo using camera.
        If all of the permissions are granted, invokes the takePhoto method.
        If not, asks for required permissions.

        Writing permission is not required anymore for writing and reading private
        external storage since API 18. However after taking a photo and saving it
        to private external storage by camera, we will also copy image and save it
        to public external storage to make it visible for user in gallery. This will
        be done just for better UX, since we will still use image from private storage
        across application for better protection over deleting images from gallery.
     */
    private void takePhotoCheckPermission()
    {
        List<String> permissions = new ArrayList<String>();
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
            {
                permissions.add(Manifest.permission.CAMERA);
            }
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if(permissions.size() > 0)
            {
                requestPermissions(permissions.toArray(new String[permissions.size()]), CAMERA_REQUEST);
            }
            else
            {
                takePhoto();
            }
        }
    }

    /*
        Starts activity for taking picture using camera.

        Uses ImageFileHandler to create image file object in public external storage
        area that will be passed to camera intent to save photo to the specified file.
     */
    private void takePhoto()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (intent.resolveActivity(getPackageManager()) != null)
        {
            Uri publicImageUri = null;
            try
            {
                publicImageUri = ImageFileHandler.createImageFilePublic(this);
            }
            catch (IOException ex)
            {
                Log.e(TAG, "Error occurred while creating the File");
            }
            if (publicImageUri != null) {
                cameraImageUri = publicImageUri;
                intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraImageUri);
                startActivityForResult(intent, CAMERA_REQUEST);
            }
        }

    }

    /*
        Checks for required permission for selecting photo from gallery.
        If all of the permissions are granted, invokes the choosePhoto method.
        If not, asks for required permissions.
     */
    private void choosePhotoCheckPermission()
    {
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
        {
            if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            {
                choosePhoto();
            }
            else
            {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, GALLERY_REQUEST);
            }
        }
    }

    /*
        Starts activity responsible for selecting image from the gallery
     */
    private void choosePhoto()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
        {
            String[] mimeTypes = {"image/jpeg", "image/png"};
            intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        }
        startActivityForResult(intent, GALLERY_REQUEST);
    }

    /*
        Checks if user entered all of the required data for creating a friend
        and if yes - enables the save button
     */
    private void checkEnableSave()
    {
        String name = etName.getText().toString().trim();

        if(!name.isEmpty())
        {
            btnSave.setEnabled(true);
        }
        else
        {
            btnSave.setEnabled(false);
        }
    }

    /*
        Method invoked after users clicks on imgFavorite.
        Switches favorite state from favorite true to favorite false
        and the other way.
     */
    private void clickFavorite()
    {
        Integer tag = (Integer) imgFavorite.getTag();
        if(tag == R.drawable.ic_favorite_false)
        {
            imgFavorite.setImageResource(R.drawable.ic_favorite_true);
            imgFavorite.setTag(R.drawable.ic_favorite_true);
        }
        else
        {
            imgFavorite.setImageResource(R.drawable.ic_favorite_false);
            imgFavorite.setTag(R.drawable.ic_favorite_false);
        }
    }

    /*
        Method for saving friend. Gets all data from views and
        path to image from instance variable and sends this data
        to AddFriendModel.
     */
    private void clickSave()
    {
        String name = etName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        Date birthday = getBirthday();
        String email = etEmail.getText().toString().trim();
        String website = etWebsite.getText().toString().trim();
        String picturePath = profilePicturePath;
        boolean favorite = getFavorite();

        Friend friend = new Friend(name, phone, birthday, email, website, favorite, picturePath);
        addFriendViewModel.insertFriend(friend);
        finish();
    }

    /*
        Method used for parsing information from imgFavorite.
        Checks if current image indicate that friend is favorite or not
     */
    private boolean getFavorite()
    {
        Integer tag = (Integer) imgFavorite.getTag();
        if(tag == R.drawable.ic_favorite_true)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /*
        Method used for parsing date from the btnBirthday and
        returning Date object
     */
    private Date getBirthday()
    {
        String birthdayAsString = btnBirthday.getText().toString();
        try
        {
            return birthdayAsString.isEmpty() ? null : dateFormatter.parse(birthdayAsString);
        }
        catch(ParseException e)
        {
            Log.e(TAG, "Error occurred while parsing the date");
            return null;
        }
    }

    /*
        Method used for displaying DatePicker
     */
    private void showDatePickerDialog()
    {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        datePickerDialog.show();
    }

    /*
        Using Glide library for setting the profile picture from image path
         to benefit from image caching and simplifying complex task of setting
         bitmap to achieve smooth behaviour and fast decoding
     */
    private void setProfilePicture(String pathToImage)
    {
        Glide.with(this)
                .load(pathToImage)
                .into(imgProfilePicture);
    }

    /*
        Method invoked after user performed some action by clicking
        something from the action bar
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId())
        {
            case android.R.id.home:
            {
                finish();
                return true;
            }
            default:
            {
                return super.onOptionsItemSelected(item);
            }
        }
    }

    /*
        Method used by DatePicker, invoked after user selected some date.
     */
    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Date date = new GregorianCalendar(year, month, dayOfMonth).getTime();
        btnBirthday.setText(dateFormatter.format(date));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch(requestCode)
        {
            case CAMERA_REQUEST:
            {
                if(grantResults.length > 0)
                {
                    for(int i = 0; i < grantResults.length; i++)
                    {
                        if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                        {
                            return;
                        }
                    }
                    takePhoto();
                }
                break;
            }
            case GALLERY_REQUEST:
            {
                if(grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    choosePhoto();
                }
                break;
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case CAMERA_REQUEST:
            {
                if(resultCode == Activity.RESULT_OK)
                {
                    /*
                        Here we are getting the cameraImagePath which camera intent
                        used to save photo taken by user into public storage.

                        In case user will delete image from gallery, we want to
                        copy it to private external storage and use this copy in
                        app to avoid this problem
                     */
                    Uri imageUri = cameraImageUri;
                    Bitmap bitmap = BitmapResolver.getBitmap(getContentResolver(), imageUri);
                    profilePicturePath = ImageFileHandler.saveImageToPrivateStorage(this, bitmap);
                    setProfilePicture(profilePicturePath);
                    break;
                }
            }
            case GALLERY_REQUEST:
            {
                if(resultCode == Activity.RESULT_OK)
                {
                    /*
                        In case user will delete image from gallery, we want to
                        copy it to private external storage and use this copy in
                        app to avoid this problem
                     */
                    Uri imageUri = data.getData();
                    Bitmap bitmap = BitmapResolver.getBitmap(getContentResolver(), imageUri);
                    profilePicturePath = ImageFileHandler.saveImageToPrivateStorage(this, bitmap);
                    setProfilePicture(profilePicturePath);
                    break;
                }
            }
        }
    }
}
