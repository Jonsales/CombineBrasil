package br.com.john.combinebrasil.AdapterList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.R;
import br.com.john.combinebrasil.Services.SquaredImageView;

/**
 * Created by GTAC on 29/05/2017.
 */

public class AdapterCoverflow extends PagerAdapter {
    Activity act;
    ArrayList<Team> teams;

    public AdapterCoverflow(Activity act, ArrayList<Team> teams){
        this.act = act;
        this.teams = teams;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        LinearLayout linear = new LinearLayout(act);
        linear.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewPager.LayoutParams.MATCH_PARENT);
        linear.setLayoutParams(lp);

        SquaredImageView imgCarousel = new SquaredImageView (act);
        imgCarousel.setLayoutParams(lp);

        String urlImage = teams.get(position).getUrlImage().equals("") || teams.get(position).getUrlImage().isEmpty()?
                "https://image.ibb.co/ev8e1a/combine_brasil.png":
                teams.get(position).getUrlImage();

        Picasso.with(act)
                .load(urlImage)
                .fit()
                .centerCrop()
                .error(act.getDrawable(R.drawable.combine_brasil_azul))
                .into(imgCarousel);

        TextView view = new TextView(act);
        view.setText(teams.get(position).getName());
        view.setGravity(Gravity.CENTER);
        view.setTypeface(null, Typeface.BOLD);
        view.setSingleLine(true);

        linear.addView(imgCarousel);
        linear.addView(view);
        container.addView(linear);
        return linear;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        setPrimaryItem((View) container, position, object);
    }

    @Override
    public int getCount() {
        return teams.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return (view == object);
    }
}