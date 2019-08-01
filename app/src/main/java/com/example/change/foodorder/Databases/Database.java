package com.example.change.foodorder.Databases;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

import com.example.change.foodorder.Model.Favorites;
import com.example.change.foodorder.Model.Order;
import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteAssetHelper {
    private static final String DB_NAME = "foodOrderDB";
    private static final int DB_VER = 1;

    public Database(Context context) {
        super(context, DB_NAME, null, DB_VER);
    }

    public List<Order> getCarts(String userEmail) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"UserEmail","ProductName", "ProductId", "Quantity", "Price", "Discount","Image"};
        String sqlTable = "OrderDetail";

        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, "UserEmail=?", new String[]{userEmail}, null, null, null);

        final List<Order> result = new ArrayList<>();

        if (c.moveToFirst()) {

            do {
                result.add(new Order(c.getString(c.getColumnIndex("UserEmail")),
                        c.getString(c.getColumnIndex("ProductId")),
                        c.getString(c.getColumnIndex("ProductName")),
                        c.getString(c.getColumnIndex("Quantity")),
                        c.getString(c.getColumnIndex("Price")),
                        c.getString(c.getColumnIndex("Discount")),
                        c.getString(c.getColumnIndex("Image")

                        ))


                );
            }
            while (c.moveToNext());
        }
        return result;

    }

    public boolean ifFoodExists(String foodId,String userEmail){
        boolean f = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = null;
        String query = "SELECT * FROM OrderDetail WHERE UserEmail='"+userEmail+"' AND ProductId='"+foodId+"';";

        cursor = db.rawQuery(query,null);
        if (cursor.getCount() > 0)
            f = true;
        else
            f = false;
        cursor.close();
        return f;
    }

    public void addToCart(Order order) {
        SQLiteDatabase db = getWritableDatabase();
        String query = String.format("INSERT INTO OrderDetail(UserEmail,ProductId,ProductName,Quantity,Price,Discount,Image) VALUES('%s','%s','%s','%s','%s','%s','%s');",
               order.getUserEmail(),
                order.getProductId(),
                order.getProductName(),
                order.getQuantity(),
                order.getPrice(),
                order.getDiscount(),
                order.getImage()
        );

        db.execSQL(query);
    }

    public void emptyCart(String userEmail) {

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserEmail='%s'",userEmail);
        db.execSQL(query);

    }

    //Favorites

    public void addFavorites(Favorites food){

        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("INSERT INTO Favorites(FoodId,UserEmail, FoodName,FoodPrice,FoodMenuId,FoodImage,FoodDiscount,FoodDesc) VALUES('%s','%s','%s','%s','%s','%s','%s','%s')",
                food.getFoodId(),
                food.getUserEmail(),
                food.getFoodName(),
                food.getFoodPrice(),
                food.getFoodMenuId(),
                food.getFoodImage(),
                food.getFoodDiscount(),
                food.getFoodDesc()
                );
        db.execSQL(query);

    }

    public void removeFavorites(String s,String s2){

        SQLiteDatabase db = getReadableDatabase();
        String query = "DELETE FROM Favorites WHERE FoodId='" + s + "' AND UserEmail='"+s2+"'";
        db.execSQL(query);

    }

    public boolean isFavorites(String s,String s2){

        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT * FROM Favorites WHERE FoodId='" + s + "' AND UserEmail='"+s2+"'";
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();

        return true;

    }


    public int getCartCount(String userEmail) {
        int count  = 0;
        SQLiteDatabase db = getReadableDatabase();
        String query = "SELECT COUNT(*)  FROM OrderDetail WHERE UserEmail='"+userEmail+"' ";
        Cursor cursor = db.rawQuery(query,null);

        if (cursor.moveToFirst() ){
            do {
                count = cursor.getInt(0);
            }while (cursor.moveToNext());

        }
        return count;




    }

    public void updateCart(Order order) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "UPDATE OrderDetail SET Quantity='" + order.getQuantity()+ "' WHERE UserEmail='" + order.getUserEmail()+"' AND ProductId='"+order.getProductId()+"';";
        db.execSQL(query);

    }
    public void incCart(String userEmail, String foodId) {
        SQLiteDatabase db = getReadableDatabase();
        String query = "UPDATE OrderDetail SET Quantity= Quantity+1 WHERE UserEmail='" + userEmail+"' AND ProductId='"+foodId+"';";
        db.execSQL(query);

    }

    public void removeFromCart(String productId, String email) {
        SQLiteDatabase db = getReadableDatabase();
        String query = String.format("DELETE FROM OrderDetail WHERE UserEmail='%s' AND ProductId='%s'",email,productId);
        db.execSQL(query);

    }

    public List<Favorites> getFavorites(String userEmail) {

        SQLiteDatabase db = getReadableDatabase();
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();

        String[] sqlSelect = {"FoodId","UserEmail", "FoodName", "FoodPrice", "FoodMenuId","FoodImage","FoodDiscount","FoodDesc"};
        String sqlTable = "Favorites";

        qb.setTables(sqlTable);

        Cursor c = qb.query(db, sqlSelect, "UserEmail=?", new String[]{userEmail}, null, null, null);

        final List<Favorites> result = new ArrayList<>();

        if (c.moveToFirst()) {

            do {
                result.add(new Favorites(c.getString(c.getColumnIndex("FoodId")),
                        c.getString(c.getColumnIndex("UserEmail")),
                        c.getString(c.getColumnIndex("FoodName")),
                        c.getString(c.getColumnIndex("FoodPrice")),
                        c.getString(c.getColumnIndex("FoodMenuId")),
                        c.getString(c.getColumnIndex("FoodImage")),
                        c.getString(c.getColumnIndex("FoodDiscount")),
                        c.getString(c.getColumnIndex("FoodDesc")


                        ))


                );
            }
            while (c.moveToNext());
        }
        return result;

    }


}
