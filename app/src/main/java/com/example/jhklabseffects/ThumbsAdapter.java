package com.example.jhklabseffects;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ThumbsAdapter extends RecyclerView.Adapter<ThumbsAdapter.ViewHolder> {

    Context context;

    private List<DataModelFilters> bitmapList;
    private setFilterChooser setfilterChooser;
    private int selectedPosition;

    ThumbsAdapter(Context context, List<DataModelFilters> bitmapList, setFilterChooser setfilterChooser) {
        this.context = context;
        this.bitmapList = bitmapList;
        this.setfilterChooser = setfilterChooser;
        selectedPosition = -1;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new ViewHolder(LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_thumbs,viewGroup,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        viewHolder.imageView.setImageBitmap(bitmapList.get(i).filterBitmap);
        viewHolder.filterName.setText(bitmapList.get(i).filterName);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setfilterChooser.onFilterClicked(viewHolder.getAdapterPosition());
                selectedPosition = viewHolder.getAdapterPosition();
                notifyDataSetChanged();
            }
        });
        if(selectedPosition == i){
            viewHolder.view.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            viewHolder.filterName.setBackgroundColor(context.getResources().getColor(R.color.colorAccent));
            viewHolder.filterName.setTextColor(context.getResources().getColor(android.R.color.white));
        }else {
            viewHolder.view.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            viewHolder.filterName.setBackgroundColor(context.getResources().getColor(android.R.color.white));
            viewHolder.filterName.setTextColor(context.getResources().getColor(android.R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return bitmapList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView filterName;
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.thumbsImageViewID);
            filterName = itemView.findViewById(R.id.filternameID);
            view = itemView.findViewById(R.id.itemSelectedFrameID);
        }
    }

    interface setFilterChooser{
        void onFilterClicked(int position);
    }

}
