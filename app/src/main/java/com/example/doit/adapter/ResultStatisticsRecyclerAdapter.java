package com.example.doit.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doit.R;
import com.example.doit.model.Statistic;
import com.example.doit.model.StatisticElement;
import com.example.doit.model.UserData;
import com.example.doit.ui.EditImageNicknameFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class ResultStatisticsRecyclerAdapter extends RecyclerView.Adapter<ResultStatisticsRecyclerAdapter.RecyclerViewHolder> {

    @Nullable
    private List<StatisticElement> listData;
    private UserVotersRecyclerListener listener;
    private Activity activity;

    public ResultStatisticsRecyclerAdapter(Activity activity) {
        this.activity = activity;
    }

    public void setData(List<StatisticElement> data) {
        if(data!=null) {
            listData = new ArrayList<>(data);
        }
    }

    public List<StatisticElement> getData() {
        return listData;
    }

    public void setRecyclerListener(UserVotersRecyclerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.statistic_element_content, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    //Will be call for every item..
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        assert listData != null;

        holder.position.setText(String.valueOf(listData.get(position).getPosition()));
        holder.text.setText(listData.get(position).getTitleElement());
        holder.value.setText(listData.get(position).getValue());

        Glide
                .with(activity)
                .load(listData.get(position).getImageUri())
                .apply(new RequestOptions())
                .into(holder.image);

    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView position;
        private TextView text;
        private TextView value;
        private ImageView image;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            position = itemView.findViewById(R.id.position_cell);
            text = itemView.findViewById(R.id.text_cell);
            value = itemView.findViewById(R.id.value_cell);
            image = itemView.findViewById(R.id.image_cell);

            itemView.setOnClickListener(view -> {
                if(listener != null) {
                    assert listData != null;
                    listener.onItemClick(getAdapterPosition(), view);
                }
            });
        }
    }

    public static interface UserVotersRecyclerListener {
        void onItemClick(int position, View clickedView);
    }
}

