package com.example.doit.adapter;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doit.R;
import com.example.doit.model.SquareLayout;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class ChooseStatisticsAdapter extends RecyclerView.Adapter<ChooseStatisticsAdapter.PictureViewHolder> {
    private List<String> statistics;
    private Context context;
    private MyStatisticsListener listener;


    public interface MyStatisticsListener {
        void onStatisticsClicked(int position, View view);
    }

    public void setListener(MyStatisticsListener listener) {
        this.listener=listener;
    }

    public ChooseStatisticsAdapter(Context context) {
        this.context = context;
    }


    public void setData(List<String> statistics) {
        this.statistics = statistics;
    }

    public List<String> getData() {
        return statistics;
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        public PictureViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.choose_statistics);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(listener!=null)
                        listener.onStatisticsClicked(getAdapterPosition(), v);
                }
            });
        }
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_statistics_layout, parent, false);
        PictureViewHolder pictureViewHolder = new PictureViewHolder(view);
        return pictureViewHolder;
    }

    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        assert statistics != null;

        holder.textView.setText(statistics.get(position));
    }

    @Override
    public int getItemCount() {
        if(statistics == null)
            return 0;
        return statistics.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}