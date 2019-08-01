package com.example.change.foodorder.ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cepheuen.elegantnumberbutton.view.ElegantNumberButton;
import com.example.change.foodorder.Activities.Cart;
import com.example.change.foodorder.Common.Common;
import com.example.change.foodorder.Databases.Database;
import com.example.change.foodorder.Model.Order;
import com.example.change.foodorder.R;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;



public class CartAdapter extends RecyclerView.Adapter<CartViewHolder> {


    private List<Order> listData = new ArrayList<>();
    private Cart context;

    public CartAdapter(List<Order> listData, Cart context) {
        this.listData = listData;
        this.context = context;
    }

    @Override
    public CartViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View itemView = inflater.inflate(R.layout.cart_layout, parent, false);

        return new CartViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {
        Picasso.with(context).load(listData.get(position).getImage()).into(holder.imgFoodCart);

        holder.cart_item_count.setNumber(listData.get(position).getQuantity());

        holder.cart_item_count.setOnValueChangeListener(new ElegantNumberButton.OnValueChangeListener() {
            @Override
            public void onValueChange(ElegantNumberButton view, int oldValue, int newValue) {
                Order order = listData.get(position);
                order.setQuantity(String.valueOf(newValue));
                new Database(context).updateCart(order);


                int total = 0;
                List<Order> orders = new Database(context).getCarts(Common.currentUser.getEmail());
                for (Order item : orders)
                    total += (Integer.parseInt(order.getPrice())) * (Integer.parseInt(item.getQuantity()));
                Locale locale = new Locale("en","IN");
                NumberFormat format = NumberFormat.getCurrencyInstance(locale);
                holder.text_cart_item_price.setText(format.format(total));
            }
        });




        Locale locale = new Locale("en", "IN");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(listData.get(position).getPrice())) * (Integer.parseInt(listData.get(position).getQuantity()));

        holder.text_cart_item_price.setText(fmt.format(price));
        holder.text_cart_item_name.setText(listData.get(position).getProductName());


    }

    @Override
    public int getItemCount() {
        return listData.size();
    }

    public Order getItem(int pos){
        return  listData.get(pos);
    }

    public void removeItem(int position){
        listData.remove(position);
        notifyItemRemoved(position);
    }


    public void restoreItem(Order item, int position){
        listData.add(position,item);
        notifyItemInserted(position);
    }
}
