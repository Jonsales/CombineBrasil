package br.com.john.combinebrasil.AdapterList;

import android.app.Activity;
import android.content.Context;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.Classes.Tests;
import br.com.john.combinebrasil.MainActivity;
import br.com.john.combinebrasil.R;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.Services.DatabaseHelper;
import br.com.john.combinebrasil.Services.Services;

/**
 * Created by GTAC on 20/08/2017.
 */
public class AdapterRecyclerTestsResults extends RecyclerView.Adapter<AdapterRecyclerTestsResults.ViewHolder> {
    private String[] values;
    private ArrayList<Tests> list;
    private Context context;
    Activity act;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRecyclerTestsResults(Context context, ArrayList<Tests> list, String[] values) {
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
        public TextView textResult;
        public ConstraintLayout listItem;

        public ViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.text_test);
            textResult = (TextView) v.findViewById(R.id.text_result);
            listItem = (ConstraintLayout)v.findViewById(R.id.linear_list);
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
    public AdapterRecyclerTestsResults.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_item_tests_results, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.txtName.setText(list.get(position).getAthlete());

        if(list.get(position).getType().toLowerCase().equals("corrida")||list.get(position).getType().toLowerCase().equals("tempo"))
            holder.textResult.setText(String.valueOf(Services.convertInTime(list.get(position).getFirstValue())));
        else if (list.get(position).getType().toLowerCase().equals("repeticao")|| list.get(position).getType().toLowerCase().equals("repeticao por tempo"))
            holder.textResult.setText(String.valueOf(list.get(position).getFirstValue()));
        else
            holder.textResult.setText(String.valueOf(Services.convertCentimetersinMeters(list.get(position).getFirstValue())));

    }
    //o metodo abaixo serve para remover um elemento da lista ao clicar no mesmo

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

}
