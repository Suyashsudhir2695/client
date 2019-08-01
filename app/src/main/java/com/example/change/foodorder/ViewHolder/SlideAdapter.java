package com.example.change.foodorder.ViewHolder;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.change.foodorder.R;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;

    public  String[] textSplash = {
            "Select A Product",
            "Add it to cart",
            "Place Your Order"
    };

    public  int[] drawables = {
            R.drawable.ic_select_item,
            R.drawable.ic_add_to_cart,
            R.drawable.ic_order_confirm
    };
    @Override
    public int getCount() {
        return textSplash.length;
    }
    public SlideAdapter(Context context){
        this.context = context;

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == (RelativeLayout) o;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
       layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
       View view = layoutInflater.inflate(R.layout.slide_layout,container,false);
        ImageView imageView = view.findViewById(R.id.imgSlideSplash);
        TextView textView = view.findViewById(R.id.textSplashSlide);

        imageView.setImageResource(drawables[position]);
        textView.setText(textSplash[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((RelativeLayout)object);
    }
}
