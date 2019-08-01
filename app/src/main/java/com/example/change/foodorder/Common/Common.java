package com.example.change.foodorder.Common;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.change.foodorder.Model.User;
import com.example.change.foodorder.Remote.APIService;
import com.example.change.foodorder.Remote.IGoogleService;
import com.example.change.foodorder.Remote.RetrofitClient;
import com.example.change.foodorder.Remote.GoogleRetrofitClient;

import retrofit2.Retrofit;

public class Common {
    public static String currentKey;
    public static User currentUser;
    public static final String DELETE = "Delete";
    public static final String USER = "User";
    public static final String PWD = "Password";
    private static final String BASE_URL = "https://fcm.googleapis.com/";
    private static final String GOOGLE_URL = "https://maps.googleapis.com/";
    public static String PHONE = "userPhone";
    public static String ordernumber = String.valueOf(System.currentTimeMillis());

    public static APIService getApiService(){
        return RetrofitClient.getClient(BASE_URL).create(APIService.class);
    }

    public static IGoogleService getGoogleService(){
        return GoogleRetrofitClient.getGoogleClient(GOOGLE_URL).create(IGoogleService.class);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float scaleX = newWidth / (float) bitmap.getWidth();
        float scaleY = newHeight / (float) bitmap.getHeight();

        float pivotX=0,pivotY=0;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(scaleX,scaleY,pivotX,pivotY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bitmap,0,0,new Paint(Paint.FILTER_BITMAP_FLAG));

        return scaledBitmap;

    }


    public static String codeToStatus(String status) {
        if (status.equals("0"))
            return "Placed";
        else if (status.equals("1"))
            return "Preparing";
        else if (status.equals("2"))
            return "Shipped";
        else
            return "Out For Delivery";

    }

    public static boolean isConnected(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if (infos != null){
                for (int i=0;i<infos.length;i++){
                    if (infos[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;

    }
}
