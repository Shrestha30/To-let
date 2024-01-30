package com.example.to_let;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
//adapter for recycler view
public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.TimelineViewHolder> {
       //using ArrayList
    ArrayList<TimeLineDataModel> TimeLineDataHolder;

    public TimelineAdapter(ArrayList<TimeLineDataModel> timeLineDataModel) {
        TimeLineDataHolder = timeLineDataModel;
    }

    @NonNull
    @Override
    public TimelineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.single_post_design,parent,false);
         return new TimelineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TimelineViewHolder holder, int position) {
      holder.imageView.setImageResource(TimeLineDataHolder.get(position).getImage());
      holder.name.setText(TimeLineDataHolder.get(position).getName());
      holder.postdetails.setText(TimeLineDataHolder.get(position).getPost_details());
    }

    @Override
    public int getItemCount() {
        return TimeLineDataHolder.size();
    }

    class TimelineViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView name,postdetails;

        public TimelineViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView=itemView.findViewById(R.id.timeline_post_desplay_image);
            name=itemView.findViewById(R.id.timeline_post_display_name);
            postdetails=itemView.findViewById(R.id.timeline_Post_details_);
        }
    }

}
