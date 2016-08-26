package com.reraisedesign.whygosolo;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.text.Text;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.IOException;
import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * Created by JackMadden on 08/08/2016.
 */
public class SignUpActivity extends AppCompatActivity {

    /**
     * A dummy authentication store containing known user names and passwords.
     * TODO: remove after connecting to a real authentication system.
     */

    //TODO: address boxes appear when you selected other. have them validate input.
    // TODO: TCs to have a link that on it taking you to a website with them on.
    // TODO: Sign In button doesn't appear until everything is set. Upload Image locally.
    // TODO: Set Up the async task. Does the user need to use both address1 and 2?
    private static final String[] DUMMY_CREDENTIALS = new String[]{
            "Jim:Foo:foo@example.com:hello:liverpool", "Brian:Bar:bar@example.com:world:moorfields"
    };
    private static final String[] UNIVERSITY_EMAIL_SUFFIX = new String[]{
        "@liverpool.ac.uk","@hope.ac.uk","@ljmu.ac.uk","@sae.edu","@lipa.ac.uk","wmc.ac.uk","edgehill.ac.uk"
    };
    private final int PICK_IMAGE_REQUEST = 1;
    private final int PICK_ACCOMODATION_REQUEST = 2;

    /**
     * Keep track of the login task to ensure we can cancel it if requested.
     */
    private UserCreationTask mAuthTask = null;

    private RelativeLayout mSignUpLayout;
    private CircularImageView mUserImageView;
    private TextView mUploadImgTextView;
    private EditText mFirstName;
    private EditText mLastName;
    private EditText mEmailView;
    private EditText mPasswordView;
    private TextView mSwitchTextView;
    private Switch mSwitchTnCs;
    private Button mSignUpBtn;
    private Bitmap bitmap;
    private Button mMapBtn;
    private TextView mStudentResidence;
    private TextView mLat;
    private TextView mLng;
    private Drawable oldDrawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        mSignUpLayout = (RelativeLayout) findViewById(R.id.sign_up_layout);
        mUserImageView = (CircularImageView) findViewById(R.id.user_img);
        mUploadImgTextView = (TextView) findViewById(R.id.upload_img);
        mFirstName = (EditText) findViewById(R.id.first_name);
        mLastName = (EditText) findViewById(R.id.last_name);
        mEmailView = (EditText) findViewById(R.id.email);
        mPasswordView = (EditText) findViewById(R.id.password);
        mSwitchTextView = (TextView) findViewById(R.id.switch_text_view);
        mSwitchTnCs = (Switch) findViewById(R.id.tc_switch);
        mSignUpBtn = (Button) findViewById(R.id.create_account);
        mMapBtn = (Button) findViewById(R.id.map_button);
        mStudentResidence = (TextView) findViewById(R.id.accomodation);
        mLat = (TextView) findViewById(R.id.latitude);
        mLng = (TextView) findViewById(R.id.longitude);

        //used to check that the user has selected an image of there own rather than the default one
        oldDrawable = mUserImageView.getDrawable();

        mSignUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(view);
            }
        });
        mSwitchTnCs.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b == true){
                    mSignUpBtn.setVisibility(View.VISIBLE);
                }
                else{
                    mSignUpBtn.setVisibility(View.INVISIBLE);
                }
            }
        });
        mSwitchTextView.setMovementMethod(new LinkMovementMethod().getInstance());
        mUploadImgTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view){
                uploadImage(view);
            }
        });
    }

    /**
     * Method which validates all of the fields, if there are any errors then the user is informed.
     * if there aren't any errors then the account is create and the user is moved onto the
     * SignInActivity.
     * @param v
     */
    public void createAccount(View v){
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mFirstName.setError(null);
        mLastName.setError(null);
        mEmailView.setError(null);
        mPasswordView.setError(null);
        mSwitchTnCs.setError(null);
        mStudentResidence.setError(null);

        // Store values at the time of the login attempt.
        String firstName = mFirstName.getText().toString();
        String lastName = mLastName.getText().toString();
        String email = mEmailView.getText().toString();
        String residence = mStudentResidence.getText().toString();

        String password = mPasswordView.getText().toString();
        Boolean termsAndConditions = mSwitchTnCs.isChecked();

        boolean cancel = false;
        View focusView = null;

        // Checks if the user entered their first name
        if(TextUtils.isEmpty(firstName)){
            mFirstName.setError(getString(R.string.error_field_required));
        }

        //Checks if the user entered their last name
        if(TextUtils.isEmpty(lastName)){
            mLastName.setError(getString(R.string.error_field_required));
        }

        // Check for a valid password, if the user entered one.
        if(TextUtils.isEmpty(password)){
            mPasswordView.setError(getString(R.string.error_field_required));
            focusView = mPasswordView;
            cancel = true;
        }
        else if (!isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address, if the user entered one.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if(TextUtils.isEmpty(residence)){
            mStudentResidence.setError(getString(R.string.error_field_required));
        }

        if(mUserImageView.getDrawable() == null || oldDrawable == mUserImageView.getDrawable()){
            mUploadImgTextView.setError(getString(R.string.error_field_required));
        }
        else{
            mUploadImgTextView.setError(null);
        }

        //If everything is valid and filled out, create the user account.
        //TODO: add functionality to create account.
        if(doesFieldHaveError(mFirstName,mLastName,mEmailView,mPasswordView,mStudentResidence, mUploadImgTextView)) {
            Toast.makeText(this, "Account Creation successful", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, SignInActivity.class);
            startActivity(intent);
        }
        else{
            Toast.makeText(this,"One or more Errors Occured", Toast.LENGTH_LONG).show();
        }
    }

    /**
     * A method which lets the user upload an image to app from there gallery.
     * @param view
     */
    public void uploadImage(View view){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_ACCOMODATION_REQUEST) {
            if(resultCode == RESULT_OK) {
                if (data != null) {
                    String title = data.getStringExtra("EXTRA_TITLE");
                    double lat = data.getDoubleExtra("EXTRA_LAT", 1);
                    double lng = data.getDoubleExtra("EXTRA_LONG", 2);
                    String latString = Double.toString(lat);
                    String lngString = Double.toString(lng);
                    mStudentResidence.setText(title);
                    mLat.setText(latString);
                    mLng.setText(lngString);

                } else {
                    Toast.makeText(this, "data is null", Toast.LENGTH_SHORT).show();
                }
            }
        }
        else{
            Log.d("MapCodes","Request:" + requestCode + "Result Code:" + resultCode);
        }

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                mUserImageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * A method that takes the user to the SignInActivity if they already have an account.
     * @param v
     */
    public void signInIntent(View v){

        Intent intent = new Intent(this, SignInActivity.class);
        startActivity(intent);
    }

    /**
     * A method that checks the given email is a valid one.
     * @param email
     * @return
     */
    private boolean isEmailValid(String email) {

        for (String suffix : UNIVERSITY_EMAIL_SUFFIX) {
           if(email.contains(suffix)){
               return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
           }
        }
        return false;
    }

    /**
     * A method that takes if a password is valid.
     * @param password
     * @return
     */
    private boolean isPasswordValid(String password) {
        return password.length()>=8;
    }

    /**
     * Checks if there are any errors in the required fields.
     *
     * @param fields
     * @return true if there are no errors. false if there are any.
     */
    public static boolean doesFieldHaveError(TextView... fields) {
        int i = 0;
        for (TextView t : fields) {
            if (t.getError() != null) {
                i++;
            }
        }
        if(i>0){
            return false;
        }
        else{
            return true;
        }
    }
    public void getMapIntent(View view){
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("FROM_ACTIVITY","SignUp");
        startActivityForResult(intent, PICK_ACCOMODATION_REQUEST);
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user. Needs to be completed.
     */
    public class UserCreationTask extends AsyncTask<Void, Void, Boolean> {

        private final String mFirstName;
        private final String mLastName;
        private final String mEmail;
        private final String mPassword;
        private final String mAccomodation;

        UserCreationTask(String firstName,String lastName, String email, String password,
                         String accomodation) {
            mFirstName = firstName;
            mLastName = lastName;
            mEmail = email;
            mPassword = password;
            mAccomodation = accomodation;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // TODO: attempt authentication against a network service.

            try {
                // Simulate network access.
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                return false;
            }

            for (String credential : DUMMY_CREDENTIALS) {
                String[] pieces = credential.split(":");
                if (pieces[0].equals(mFirstName) && pieces[1].equals(mLastName) && pieces[2]
                        .equals(mEmail)&& pieces[4].equals(mAccomodation) ) {
                    // Account exists, return true if the password matches.
                    return pieces[3].equals(mPassword);
                }
            }

            // TODO: register the new account here.
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;

            if (success) {
                finish();
            } else {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
        }
    }
}
