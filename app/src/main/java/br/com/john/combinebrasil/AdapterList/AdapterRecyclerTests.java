package br.com.john.combinebrasil.AdapterList;

/**
 * Created by GTAC on 19/10/2016.
 */


import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.solver.widgets.ConstraintAnchor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.john.combinebrasil.AthletesActivity;
import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.MainActivity;
import br.com.john.combinebrasil.R;
import br.com.john.combinebrasil.Services.AllActivities;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;

public class AdapterRecyclerTests extends RecyclerView.Adapter<AdapterRecyclerTests.ViewHolder> {
    private String[] values;
    private ArrayList<TestTypes> list;
    private Context context;
    Activity act;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRecyclerTests(Context context, ArrayList<TestTypes> list, String[] values) {
        super();
        this.list = list;
        this.values = values;
        this.context = context;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName;
        public ConstraintLayout listItem;
        public ImageView imageIcon;

        public ViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.firstLine);
            imageIcon = (ImageView) v.findViewById(R.id.icon);
            listItem = (ConstraintLayout)v.findViewById(R.id.linear_list);

            //listItem.setOnClickListener((View.OnClickListener)listItemListener);
            //progressBar.setOnClickListener((View.OnClickListener) progressBarListener);
        }
    }

    public void add(int position, String item) {
        // myTitle.add(position, item);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        //int position = myTitle.indexOf(item);
        //myTitle.remove(position);
        //notifyItemRemoved(position);
    }

    public void setHomeActivity(Activity homeActivity){
        this.act = homeActivity;
    }
    public Activity getHomeActivity(){
        return act;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public AdapterRecyclerTests.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_test, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        //holder.txtHeader.setText(list.get(position).getName());
        //holder.txtFooter.setText(list.get(position).getDescription());

        holder.txtName.setText(list.get(position).getName());

       //holder.txtStatus.setText(getCountAthletes(list.get(position).getId()));

        holder.listItem.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("clique no item da lista", "true");
                Log.i("Elemento clicado", list.get(position).getId());
                MainActivity.onClickItemList(getHomeActivity(), position, list.get(position).getId());
            }
        });

        String urlImage = list.get(position).getIconImageURL().equals("") || list.get(position).getIconImageURL().isEmpty()?
                "https://image.ibb.co/ev8e1a/combine_brasil.png":
                list.get(position).getIconImageURL();

        Picasso.with(act)
                .load(urlImage)
                .fit()
                .centerCrop()
                .error(getHomeActivity().getDrawable(R.drawable.combine_brasil_azul))
                .into(holder.imageIcon);

        //chosseIcon(list.get(position).getIconImageURL(), holder.imageIcon);
    }
    //o metodo abaixo serve para remover um elemento da lista ao clicar no mesmo

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    private String getCountAthletes(String id){
        DatabaseHelper db = new DatabaseHelper(context);
        long numAthltes = db.getCountTable(Constants.TABLE_ATHLETES);
        long numTests = 0;
        ArrayList<Tests> tests = db.getTestsFromType(id);
        if(tests!=null) {
            for (Tests obj : tests) {
                if (obj.getCanSync())
                    numTests = numTests + 1;
            }
        }
        return String.valueOf(numTests +"/"+numAthltes);
    }

}