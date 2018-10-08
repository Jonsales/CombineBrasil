package br.com.john.combinebrasil.AdapterList;

import android.app.Activity;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.john.combinebrasil.ChooseTeamSelectiveActivity;
import br.com.john.combinebrasil.Classes.Selective;
import br.com.john.combinebrasil.Classes.Team;
import br.com.john.combinebrasil.EnterSelectiveActivity;
import br.com.john.combinebrasil.HistoricSelectiveActivity;
import br.com.john.combinebrasil.R;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;

/**
 * Created by GTAC on 19/08/2017.
 */

public class AdapterRecyclerTeam extends RecyclerView.Adapter<AdapterRecyclerTeam.ViewHolder> {
    private String[] values;
    private ArrayList<Team> list;
    private Activity activity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRecyclerTeam(Activity act, ArrayList<Team> list, String[] values) {
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
        ConstraintLayout linearBackground;
        TextView textName;
        TextView textCity;
        TextView textDate;
        ImageView imgView;
        public ViewHolder(View v) {
            super(v);
            linearBackground = (ConstraintLayout) v.findViewById(R.id.constraint_background_list);
            textName = (TextView) v.findViewById(R.id.text_name_selective_list);
            textCity = (TextView) v.findViewById(R.id.text_team_name_list);
            textDate = (TextView) v.findViewById(R.id.text_date_list);
            imgView = (ImageView) v.findViewById(R.id.image_list);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterRecyclerTeam.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_selective, parent, false);
        // set the view's size, margins, paddings and layout parameters
        AdapterRecyclerTeam.ViewHolder vh = new AdapterRecyclerTeam.ViewHolder(v);
        vh.textDate.setVisibility(View.GONE);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(AdapterRecyclerTeam.ViewHolder holder, final int position) {
        holder.linearBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(list.get(position));
            }
        });
        holder.textName.setText(list.get(position).getName());
        holder.textCity.setText(list.get(position).getCity());
        String url = list.get(position).getUrlImage().equals("")?
                "https://image.ibb.co/ev8e1a/combine_brasil.png" :
                list.get(position).getUrlImage();

        Picasso.with(activity)
                .load(url)
                .fit()
                .centerCrop()
                .error(activity.getDrawable(R.drawable.combine_brasil_azul))
                .into(holder.imgView);
    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }

    private void click(Team team){
        ChooseTeamSelectiveActivity.clickTeamSearch(activity,team);
    }

    //o metodo abaixo serve para remover um elemento da lista ao clicar no mesmo
    /*public void add(int position, String item) {
        //notifyItemInserted(position);
    }

    public void remove(String item) {
        //int position = myTitle.indexOf(item);
        //myTitle.remove(position);
        //notifyItemRemoved(position);
    }*/


}
