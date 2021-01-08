package com.example.doit.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.bumptech.glide.request.RequestOptions;
import com.example.doit.R;
import com.example.doit.model.QuestionPostData;
import com.example.doit.model.SquareLayout;
import com.example.doit.ui.EditImageNicknameFragment;
import com.google.android.gms.tasks.OnCanceledListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.callback.Callback;

public class ChoosePictureAccountAdapter extends RecyclerView.Adapter<ChoosePictureAccountAdapter.PictureViewHolder> {
    private List<StorageReference> items;
    private Uri[] uriArray;
    private boolean[] isDownloaded;
    private Context context;
    private MyPictureListener listener;
    private int lastSelectedPosition;
    public  static String currentImageSelectedName;


    public interface MyPictureListener {
        void onPictureClicked(int position, View view);
    }

    public void setListener(MyPictureListener listener) {
        this.listener=listener;
    }

    public ChoosePictureAccountAdapter(Context context, String currentImageSelectedName) {
        this.context = context;
        this.currentImageSelectedName = currentImageSelectedName;
    }

    public void setSavedImage(String profileImageName) {
        this.currentImageSelectedName = profileImageName;
    }

    public void setData(List<StorageReference> items) {
        this.items = items;
        this.uriArray = new Uri[items.size()];
        this.isDownloaded = new boolean[items.size()];
    }

    public List<StorageReference> getData() {
        return items;
    }

    public class PictureViewHolder extends RecyclerView.ViewHolder {
        ImageView imageIv;
        ProgressBar loadingBar;
        SquareLayout squareLayout;

        public PictureViewHolder(View itemView) {
            super(itemView);

            imageIv = itemView.findViewById(R.id.choose_picture_account_image);
            loadingBar = itemView.findViewById(R.id.image_loadingBar);
            squareLayout = itemView.findViewById(R.id.picture_square_layout);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    if(listener!=null)
                        listener.onPictureClicked(getAdapterPosition(), v);
                    if(((ColorDrawable)squareLayout.getBackground()).getColor() == context.getColor(R.color.transparent)){
//                        squareLayout.setBackgroundColor(context.getColor(R.color.hulo_light_blue));
                        currentImageSelectedName = items.get(getAdapterPosition()).getName();
                        notifyItemChanged(getAdapterPosition());
                        notifyItemChanged(lastSelectedPosition);
                    }
                }
            });
        }
    }

    @Override
    public PictureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.choose_picture_account_layout, parent, false);
        PictureViewHolder pictureViewHolder = new PictureViewHolder(view);
        return pictureViewHolder;
    }

    @Override
    public void onBindViewHolder(PictureViewHolder holder, int position) {
        assert items != null;

//        Uri uri = items.get(position).getDownloadUrl().getResult();

//        if(position == imageSelectedPosition){
//
//            holder.squareLayout.setBackgroundColor(context.getColor(R.color.hulo_light_blue));
//            imageSelectedID = items.get(position).getName();
//        }
//        else{
//            holder.squareLayout.setBackgroundColor(context.getColor(R.color.transparent));
//        }
        if(items.get(position).getName().equals(currentImageSelectedName)){
            holder.squareLayout.setBackgroundColor(context.getColor(R.color.toolbarColor));
//            currentImageSelectedName = items.get(position).getName();
            lastSelectedPosition = position;
        }
        else{
            holder.squareLayout.setBackgroundColor(context.getColor(R.color.transparent));
        }
//        Toast.makeText(context, items.get(position).getName(), Toast.LENGTH_SHORT).show();
        if(isDownloaded[position]) {
            holder.loadingBar.setVisibility(View.GONE);
            Glide
                    .with(context)
                    .load(uriArray[position])
                    .apply(new RequestOptions())
                    .into(holder.imageIv);
        }
        else {
            holder.loadingBar.setVisibility(View.VISIBLE);
            items.get(position).getDownloadUrl()
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            holder.loadingBar.setVisibility(View.GONE);
                            Uri uri = task.getResult();
                            if(uri!=null) {
                                Glide
                                        .with(context)
                                        .load(uri)
                                        .apply(new RequestOptions())
                                        .into(holder.imageIv);
                                uriArray[position] = uri;
                                isDownloaded[position] = true;
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            isDownloaded[position] = false;
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        if(items == null)
            return 0;
        return items.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }
}