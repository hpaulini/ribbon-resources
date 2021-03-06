package com.helenpaulini.ribbon_resources.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.helenpaulini.ribbon_resources.R;
import com.helenpaulini.ribbon_resources.models.ContactInfo;
import com.helenpaulini.ribbon_resources.models.Profile;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactinfoFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactinfoFragment extends Fragment {

    public static final String TAG = "Contactinfo Fragment";

    private EditText etEmail;
    private EditText etPhone;
    private EditText etFacebook;
    private EditText etInstagram;
    private EditText etAddressLine1;
    private EditText etAddressLine2;
    private EditText etAddressLine3;
    private EditText etAddressLine4;
    private Button btnSave;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ContactinfoFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ContactinfoFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ContactinfoFragment newInstance(String param1, String param2) {
        ContactinfoFragment fragment = new ContactinfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contactinfo, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        etEmail = view.findViewById(R.id.etEmail);
        etPhone = view.findViewById(R.id.etPhone);
        etFacebook = view.findViewById(R.id.etFacebook);
        etInstagram = view.findViewById(R.id.etInstagram);
        etAddressLine1 = view.findViewById(R.id.etAdressLine1);
        etAddressLine2 = view.findViewById(R.id.etAddressLine2);
        etAddressLine3 = view.findViewById(R.id.etAddressLine3);
        etAddressLine4 = view.findViewById(R.id.etAddressLine4);
        btnSave = view.findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etEmail.getText().toString();
                String phone = etPhone.getText().toString();
                String facebook = etFacebook.getText().toString();
                String instagram = etInstagram.getText().toString();
                String address1 = etAddressLine1.getText().toString();
                String address2 = etAddressLine2.getText().toString();
                String address3 = etAddressLine3.getText().toString();
                String address4 = etAddressLine4.getText().toString();
                saveContactInfo(email, phone, facebook, instagram, address1, address2, address3, address4);
            }
        });
    }

    public void saveContactInfo(String email, String phone, String facebook, String instagram, String address1, String address2, String address3, String address4){
        ParseUser currentUser = ParseUser.getCurrentUser();
        currentUser.fetchInBackground();

        ContactInfo contact = new ContactInfo();
        contact.setUser(currentUser);

        contact.setEmail(email);
        contact.setPhone(phone);
        contact.setFacebook(facebook);
        contact.setInstagram(instagram);
        contact.setAddress(address1+"/n"+address2+"/n"+address3+", "+address4);

        contact.saveInBackground(new SaveCallback() {
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
}