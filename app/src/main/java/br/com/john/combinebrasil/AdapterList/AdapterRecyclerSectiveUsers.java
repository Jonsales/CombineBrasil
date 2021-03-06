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

import br.com.john.combinebrasil.Classes.SelectiveUsers;
import br.com.john.combinebrasil.EnterSelectiveActivity;
import br.com.john.combinebrasil.HistoricSelectiveActivity;
import br.com.john.combinebrasil.R;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;

/**
 * Created by GTAC on 18/04/2017.
 */

public class AdapterRecyclerSectiveUsers extends RecyclerView.Adapter<AdapterRecyclerSectiveUsers.ViewHolder> {
    private String[] values;
    private ArrayList<SelectiveUsers> list;
    private Activity activity;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRecyclerSectiveUsers(Activity act, ArrayList<SelectiveUsers> list, String[] values) {
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
        TextView textNameSelective;
        TextView textTeamName;
        TextView textNameAdmin;
        TextView textDate;
        ImageView imgView;
        public ViewHolder(View v) {
            super(v);
            linearBackground = (ConstraintLayout) v.findViewById(R.id.constraint_background_list);
            textNameSelective = (TextView) v.findViewById(R.id.text_name_selective_list);
            textNameAdmin = (TextView) v.findViewById(R.id.text_team_name_admin);
            textTeamName = (TextView) v.findViewById(R.id.text_team_name_list);
            textDate = (TextView) v.findViewById(R.id.text_date_list);
            imgView = (ImageView) v.findViewById(R.id.image_list);
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterRecyclerSectiveUsers.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_selective, parent, false);
        // set the view's size, margins, paddings and layout parameters
        AdapterRecyclerSectiveUsers.ViewHolder vh = new AdapterRecyclerSectiveUsers.ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(AdapterRecyclerSectiveUsers.ViewHolder holder, final int position) {
        holder.linearBackground.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                click(list.get(position));
            }
        });
        holder.textNameSelective.setText(list.get(position).getTitle());
        holder.textTeamName.setText(list.get(position).getTeam());
        holder.textNameAdmin.setVisibility(View.VISIBLE);
        holder.textNameAdmin.setText(this.activity.getString(R.string.admin_selective)+": "+list.get(position).getNameAdmin());
        holder.textDate.setText(Services.convertDate(list.get(position).getDate()));

        String url = list.get(position).getUrlImage().equals("") ?
                "https://image.ibb.co/ev8e1a/combine_brasil.png" :
                list.get(position).getUrlImage();
        Picasso.with(activity)
                .load(url)
                .fit()
                .centerCrop()
                .error(activity.getDrawable(R.drawable.combine_brasil_azul))
                .into(holder.imgView);

        DatabaseHelper db = new DatabaseHelper(activity);

        holder.textTeamName.setText(db.getNameTeamByIdTeam(list.get(position).getTeam()));

    }


    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return list.size();
    }

    private void click(SelectiveUsers selective){
        if(activity.getClass().getSimpleName().equals("EnterSelectiveActivity"))
            EnterSelectiveActivity.onClickItemList(activity, selective);
        else if (activity.getClass().getSimpleName().equals("HistoricSelectiveActivity"))
            HistoricSelectiveActivity.onClickSelective(activity, selective);
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
