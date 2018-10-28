package cau.cse.capstone.focus;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by kyi42 on 2018-10-28.
 */

public class ReAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPicture;
        TextView tv_data;

        MyViewHolder(View view){
            super(view);
            ivPicture = view.findViewById(R.id.iv_picture);
            tv_data = view.findViewById(R.id.tv_data);
        }
    }

    private ArrayList<ItemInfo> InfoArrayList;
    ReAdapter(ArrayList<ItemInfo> foodInfoArrayList){
        this.InfoArrayList = foodInfoArrayList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item , parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        MyViewHolder myViewHolder = (MyViewHolder) holder;

        myViewHolder.ivPicture.setImageResource(InfoArrayList.get(position).drawableId);
        myViewHolder.tv_data.setText(InfoArrayList.get(position).data);
    }

    @Override
    public int getItemCount() {
        return InfoArrayList.size();
    }
}
