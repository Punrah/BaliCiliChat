package com.example.puniaraharja.balicilichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.puniaraharja.balicilichat.helper.SessionManager;
import com.example.puniaraharja.balicilichat.helper.UserGlobal;
import com.example.puniaraharja.balicilichat.helper.UserSQLiteHandler;
import com.example.puniaraharja.balicilichat.persistence.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;



public class ProfileFragment extends Fragment {

TextView logout,name,email;
    private DatabaseReference mDatabase;
    private UserSQLiteHandler db;
    private SessionManager session;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View myInflater = inflater.inflate(R.layout.fragment_profile, container, false);

        db = new UserSQLiteHandler(getActivity().getApplicationContext());
        // session manager
        session = new SessionManager(getActivity());
        logout = (TextView) myInflater.findViewById(R.id.logout);
        name = (TextView) myInflater.findViewById(R.id.name);
        email = (TextView) myInflater.findViewById(R.id.username);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        name.setText(UserGlobal.getUser(getActivity()).name);

        email.setText(UserGlobal.getUser(getActivity()).email);

        return myInflater;
    }


    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        startActivity(intent);
        getActivity().finish();
    }


}
