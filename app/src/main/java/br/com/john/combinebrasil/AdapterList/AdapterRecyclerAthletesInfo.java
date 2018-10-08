package br.com.john.combinebrasil.AdapterList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedTransformationBuilder;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.ArrayList;

import br.com.john.combinebrasil.AthletesActivity;
import br.com.john.combinebrasil.Classes.Athletes;
import br.com.john.combinebrasil.HistoricPlayersSelectiveActivity;
import br.com.john.combinebrasil.R;
import br.com.john.combinebrasil.Services.ImageConverter;
import br.com.john.combinebrasil.Services.ImageLoadedCallback;
import br.com.john.combinebrasil.Services.Services;

/**
 * Created by GTAC on 21/07/2017.
 */

public class AdapterRecyclerAthletesInfo extends RecyclerView.Adapter<AdapterRecyclerAthletesInfo.ViewHolder> {
    private String[] values;
    private ArrayList<Athletes> list;
    private Activity activity;
    private static ImageView imageView;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRecyclerAthletesInfo(Activity act, ArrayList<Athletes> list, String[] values) {
        super();
        this.list = list;
        this.values = values;
        this.activity = act;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        ConstraintLayout constraintItem;
        TextView textNamePlayer;
        ProgressBar progressBar;
        ImageView imgPlayer;
        TextView textInfo;
        public ViewHolder(View v) {
            super(v);
            constraintItem = (ConstraintLayout) v.findViewById(R.id.constraint_item_player);
            textNamePlayer = (TextView) v.findViewById(R.id.text_name_player_list);
            imgPlayer = (ImageView) v.findViewById(R.id.img_players);
            progressBar = (ProgressBar) v.findViewById(R.id.progress_bar);
            textInfo = (TextView) v.findViewById(R.id.text_info_player_list);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterRecyclerAthletesInfo.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_player, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.constraintItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(position);
            }
        });
        holder.textNamePlayer.setText(list.get(position).getName());
        holder.textInfo.setVisibility(View.GONE);
        String url = (list.get(position).getURLImage() != null && (!list.get(position).getURLImage().isEmpty()))?
                list.get(position).getURLImage() :
                "https://image.ibb.co/f7ddCQ/icon_user.png";
        imageView = holder.imgPlayer;

        Transformation transformation = new RoundedTransformationBuilder()
                .borderColor(Color.BLACK)
                .borderWidthDp(5)
                .cornerRadiusDp(10)
                .oval(true)
                .build();

        Picasso.with(activity)
            .load(url)
            .transform(transformation)
            .into(holder.imgPlayer,  new ImageLoadedCallback(null) {
                @Override
                public void onSuccess() {
                   // if(list.get(position).getURLImage() != null && (!list.get(position).getURLImage().isEmpty()))
                   //     roundedImage(imageView);
                }
                @Override
                public void onError(){
                }
            });


    }

    private void roundedImage(ImageView imageTeam){
        Bitmap bitmap = ((BitmapDrawable)imageTeam.getDrawable()).getBitmap();
        bitmap = Services.getRoundedCornerBitmap(bitmap);
        imageTeam.setImageBitmap(bitmap);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }

    private void click(int position){
        HistoricPlayersSelectiveActivity.onClickListItem(activity, position);
    }
}