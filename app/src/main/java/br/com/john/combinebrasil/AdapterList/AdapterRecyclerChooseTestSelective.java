package br.com.john.combinebrasil.AdapterList;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import junit.framework.Test;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Random;

import br.com.john.combinebrasil.Classes.TestTypes;
import br.com.john.combinebrasil.R;
import br.com.john.combinebrasil.Services.Constants;
import br.com.john.combinebrasil.TestSelectiveActivity;

/**
 * Created by GTAC on 24/04/2017.
 */


public class AdapterRecyclerChooseTestSelective extends RecyclerView.Adapter<AdapterRecyclerChooseTestSelective.ViewHolder> {
    private String[] values;
    public static String[] valuesID;
    public static ArrayList<TestTypes> list;
    private Activity act;
    private static ArrayList<ViewHolder> holderList;
    private static ArrayList<TestTypes> testsChoose;

    // Provide a suitable constructor (depends on the kind of dataset)
    public AdapterRecyclerChooseTestSelective(Activity act, ArrayList<TestTypes> list, String[] values) {
        super();
        this.list = list;
        this.values = values;
        this.act = act;
        testsChoose = new ArrayList<TestTypes>();
        holderList = new ArrayList<ViewHolder>();
        valuesID = new String[list.size()];

    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView txtName;
        public TextView txtInfoDefaultTest;
        public ConstraintLayout listItem, constraintInfoEquivalentTest;
        public ImageView imageIcon;
        public CheckBox checkTest;


        public ViewHolder(View v) {
            super(v);
            txtName = (TextView) v.findViewById(R.id.text_name_test);
            checkTest = (CheckBox) v.findViewById(R.id.check_box_test);
            imageIcon = (ImageView) v.findViewById(R.id.icon);
            listItem = (ConstraintLayout) v.findViewById(R.id.linear_list);
            txtInfoDefaultTest = (TextView) v.findViewById(R.id.text_info_test_default);

            listItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickedItemList(ViewHolder.this.getAdapterPosition());
                }
            });
        }
    }

    public void add(int position, String item) {
        notifyItemInserted(position);
    }

    // Create new views (invoked by the layout manager)
    @Override
    public AdapterRecyclerChooseTestSelective.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_list_test_choose, parent, false);
        // set the view's size, margins, paddings and layout parameters
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //chosseIcon(list.get(holder.getPosition()).getIconImageURL(), holder.imageIcon, false);
        holder.txtName.setText(list.get(holder.getPosition()).getName());

        holder.txtInfoDefaultTest.setText(act.getResources().getString(R.string.test_protocol));
        holder.txtInfoDefaultTest.setVisibility(View.VISIBLE);


        Picasso.with(act)
                .load(list.get(position).getIconImageURL())
                .fit()
                .centerCrop()
                .error(act.getDrawable(R.drawable.combine_brasil_azul))
                .into(holder.imageIcon);
        //in some cases, it will prevent unwanted situations
        holder.checkTest.setOnCheckedChangeListener(null);

        //if true, your checkbox will be selected, else unselected
        holder.checkTest.setChecked(list.get(position).isSelected());

        holder.checkTest.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                list.get(holder.getAdapterPosition()).setSelected(isChecked);
                TestSelectiveActivity.checkClick(act, list.get(position).getId());
                TestSelectiveActivity.showOrHideRemove();
            }
        });
        if(holder!=null && !holderList.contains(holder))
            holderList.add(holder);
    }

    @Override
    public int getItemCount() {
        return (null != list ? list.size() : 0);
    }

    private void clickedItemList(int position){
        TestSelectiveActivity.showInfoTest(act, list.get(position));
    };
}