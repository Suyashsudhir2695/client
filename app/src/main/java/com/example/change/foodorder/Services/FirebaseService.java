package com.example.change.foodorder.Services;

import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Model.Token;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class FirebaseService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        super.onTokenRefresh();

        String tokenRefreshed = FirebaseInstanceId.getInstance().getToken();
        if (Common.currentUser != null)
            updateToken(tokenRefreshed);

    }

    private void updateToken(String tokenRefreshed) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference tokens = database.getReference("Tokens");
        Token token = new Token(tokenRefreshed,false);
        tokens.child(Common.currentUser.getEmail()).setValue(token);
    }
}
