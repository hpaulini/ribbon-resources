package com.helenpaulini.ribbon_resources.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import java.io.File;
import static android.app.Activity.RESULT_OK;

import com.codepath.asynchttpclient.AsyncHttpClient;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;
import com.helenpaulini.ribbon_resources.R;
import com.helenpaulini.ribbon_resources.models.Hospital;
import com.helenpaulini.ribbon_resources.models.Profile;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import okhttp3.Headers;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ProfileFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProfileFragment extends Fragment {

    //TO DO: when the user leaves this fragment, save the instance state
    // so that the view is the same when they return

    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 42;
    public static final String TAG = "ProfileFragment";
    public static final String ARG_PAGE = "ARG_PAGE";
    public static final String API_URL = "https://services1.arcgis.com/Hp6G80Pky0om7QvQ/arcgis/rest/services/Hospitals_1/FeatureServer/0/query?where=1%3D1&outFields=*&outSR=4326&f=json";

    private ImageView ivProfile;
    private EditText etFirstName;
    private EditText etLastName;
    private EditText etCity;
    private EditText etBirthday;
    //private EditText etHospital;
    private EditText etCancerType;
    private EditText etTreatmentType;
    private EditText etBio;
    private EditText etTreatmentSart;
    private EditText etTreatmentEnd;
    private Button btnAddProfilePic;
    private CheckBox cbCurrentPatient;
    private Button btnSaveProfile;
    private Button btnNext;
    private Spinner spHospital;

    private Boolean isCurrentPatient;

    private File photoFile;
    public String photoFileName = "photo.jpg";

    public List<Hospital> hospitals = new ArrayList<>();

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ProfileFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProfileFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ProfileFragment newInstance(String param1, String param2) {
        ProfileFragment fragment = new ProfileFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public static ProfileFragment newInstance(int page) {
        Bundle args = new Bundle();
        args.putInt(ARG_PAGE, page);
        ProfileFragment fragment = new ProfileFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            ivProfile = view.findViewById(R.id.ivProfile);
            etFirstName = view.findViewById(R.id.etFirstName);
            etLastName = view.findViewById(R.id.etLastName);
            etCity = view.findViewById(R.id.etCity);
            etBirthday = view.findViewById(R.id.etBirthday);
            //etHospital = view.findViewById(R.id.etHospital);
            etCancerType = view.findViewById(R.id.etCancerType);
            etTreatmentType = view.findViewById(R.id.etTreatmentType);
            etBio = view.findViewById(R.id.etBio);
            etTreatmentSart = view.findViewById(R.id.etTreatmentStart);
            etTreatmentEnd = view.findViewById(R.id.etTreatmentEnd);
            cbCurrentPatient = view.findViewById(R.id.cbCurrentPatient);

            btnAddProfilePic = view.findViewById(R.id.btnAddProfilePic);
            btnSaveProfile = view.findViewById(R.id.btnSaveProfile);
            btnNext = view.findViewById(R.id.btnNext);
            spHospital = view.findViewById(R.id.spHospital);

            AsyncHttpClient client = new AsyncHttpClient();
            client.get(API_URL, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Headers headers, JSON json) {
                    Log.d(TAG, "onsuccess");
                    JSONObject jsonObject = json.jsonObject;
                    try {
                        JSONArray features = jsonObject.getJSONArray("features");
                        hospitals = Hospital.fromJsonArray(features); //hospitals is a list of [{key:{key1:value, key2:value, ...}}, ...]
                        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(),
                                android.R.layout.simple_spinner_item, hospitalNameList(hospitals));
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spHospital.setAdapter(adapter);
                    } catch (JSONException e) {
                        Log.e(TAG, "json exception", e);
                    }
                }
                @Override
                public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                    Log.e(TAG, "onfailure", throwable);
                }
            });

            cbCurrentPatient.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.i(TAG, "onClick checkbox");
                    if (((CheckBox) v).isChecked()) {
                        isCurrentPatient = true;
                    } else {
                        isCurrentPatient = false;
                    }
                }
            });

            btnAddProfilePic.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    launchCamera();
                }
            });

            btnSaveProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i(TAG, "Clicked save profile");
                    String firstName = etFirstName.getText().toString();
                    String lastName = etLastName.getText().toString();
                    String city = etCity.getText().toString();
                    String birthday = etBirthday.getText().toString();
                    //String hospital = etHospital.getText().toString();
                    String cancerType = etCancerType.getText().toString();
                    String treatmentType = etTreatmentType.getText().toString();
                    String bio = etBio.getText().toString();
                    String treatmentStart = etTreatmentSart.getText().toString();
                    String treatmentEnd = etTreatmentEnd.getText().toString();
                    String selectedHospital = spHospital.getSelectedItem().toString();

                    if (firstName.isEmpty() || city.isEmpty() || cancerType.isEmpty()) {
                        Toast.makeText(getContext(), "Required fields cannot be empty", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    saveProfile(firstName, lastName, city, birthday, selectedHospital, cancerType, treatmentType, bio, treatmentStart, treatmentEnd, photoFile);
                }
            });

            btnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    FragmentTransaction fts = fm.beginTransaction();
                    fts.replace(R.id.flContainer, new ContactinfoFragment());
                    fts.addToBackStack(TAG);
                    fts.commit();
                }
            });
        }

        private void launchCamera() {
            // create Intent to take a picture and return control to the calling application
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            // Create a File reference for future access
            photoFile = getPhotoFileUri(photoFileName);

            // wrap File object into a content provider
            // required for API >= 24
            // See https://guides.codepath.com/android/Sharing-Content-with-Intents#sharing-files-with-api-24-or-higher
            Uri fileProvider = FileProvider.getUriForFile(getContext(), "com.codepath.fileprovider", photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

            // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
            // So as long as the result is not null, it's safe to use the intent.
            if (intent.resolveActivity(getContext().getPackageManager()) != null) {
                // Start the image capture intent to take photo
                startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
            }
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
                if (resultCode == RESULT_OK) {
                    // by this point we have the camera photo on disk
                    Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                    // RESIZE BITMAP, see section below
                    // Load the taken image into a preview
                    ivProfile.setImageBitmap(takenImage);
                } else { // Result was a failure
                    Toast.makeText(getContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
                }
            }
        }

        // Returns the File for a photo stored on disk given the fileName
        public File getPhotoFileUri(String fileName) {
            // Get safe storage directory for photos
            // Use `getExternalFilesDir` on Context to access package-specific directories.
            // This way, we don't need to request external read/write runtime permissions.
            File mediaStorageDir = new File(getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()) {
                Log.d(TAG, "failed to create directory");
            }

            // Return the file target for the photo based on filename
            return new File(mediaStorageDir.getPath() + File.separator + fileName);
        }

        private void saveProfile(String firstName, String lastName, String city, String birthday, String selectedHospital, String cancerType, String treatmentType, String bio, String treatmentStart, String treatmentEnd, File photoFile) {
            ParseUser currentUser = ParseUser.getCurrentUser();
            currentUser.fetchInBackground();

            Profile profile = new Profile();
            profile.setUser(currentUser);

            profile.setUserType("patient");
            profile.setFirstName(firstName);
            profile.setLastName(lastName);
            profile.setBio(bio);
            profile.setCity(city);
            profile.setHospital(selectedHospital);
            profile.setCancerType(cancerType);
            profile.setTreatmentType(treatmentType);
            profile.setImage(new ParseFile(photoFile));

            profile.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Log.e(TAG, "Error while saving", e);
                        Toast.makeText(getContext(), "Error while saving", Toast.LENGTH_SHORT).show();
                    }
                    Log.i(TAG, "Profile saved successfully!!");
                }
            });
        }

        @Override
        public void onSaveInstanceState(@NonNull Bundle outState) {
            super.onSaveInstanceState(outState);
        }

        private ArrayList<String> hospitalNameList(List<Hospital> hospitals){
            ArrayList<String> hospitalNameList = new ArrayList<>();
            for(int i=0; i<hospitals.size(); i++){
                hospitalNameList.add(hospitals.get(i).getName());
            }
            return hospitalNameList;
        }
    }