package com.example.doit.adapter;

import android.app.Activity;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.doit.R;
import com.example.doit.model.LocalHelper;
import com.example.doit.model.QuestionFireStore;
import com.example.doit.model.UserData;
import com.example.doit.ui.EditImageNicknameFragment;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.List;

public class UserSearchRecyclerAdapter extends RecyclerView.Adapter<UserSearchRecyclerAdapter.RecyclerViewHolder> implements Filterable {

    @Nullable
    private List<UserData> listData, listDataFull;
    private UserSearchRecyclerListener listener;
    private LocalHelper localHelper;
    private Activity activity;
    private String currentLanguage;

    public UserSearchRecyclerAdapter(Activity activity) {
        this.activity = activity;
        this.localHelper = new LocalHelper(activity);
        this.currentLanguage = localHelper.getLocale();
    }

    public void setData(List<UserData> data) {
        if(data!=null) {
            listData = new ArrayList<>();
            listDataFull = new ArrayList<UserData>(data);
        }
    }

    public List<UserData> getData() {
        return listData;
    }

    public void setRecyclerListener(UserSearchRecyclerListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_card_content, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    //Will be call for every item..
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        assert listData != null;


        holder.nickname.setText(listData.get(position).getNickName());

        FirebaseStorage.getInstance().getReference(EditImageNicknameFragment.PROFILE_IMAGES_FOLDER)
                .child(listData.get(position).getProfileImageName()).getDownloadUrl()
                .addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide
                                .with(activity)
                                .load(uri)
                                .apply(new RequestOptions())
                                .into(holder.image);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }


    class RecyclerViewHolder extends RecyclerView.ViewHolder
    {
        private TextView nickname;
        private ImageView image;

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            nickname = itemView.findViewById(R.id.user_search_cell);
            image = itemView.findViewById(R.id.image_user_search_cell);

            itemView.setOnClickListener(view -> {
                if(listener != null) {
                    assert listData != null;
                    listener.onItemClick(getAdapterPosition(), view, listData.get(getAdapterPosition()));
                }
            });
        }
    }

    public static interface UserSearchRecyclerListener {
        void onItemClick(int position, View clickedView, UserData userData);
    }

    @Override
    public Filter getFilter() {
        return filter;
    }

    private Filter filter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<UserData> filterList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filterList.clear();
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (UserData userData : listDataFull) {
                    if (userData.getNickName().toLowerCase().contains(filterPattern)) {
                        filterList.add(userData);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(listData != null){
                listData.clear();
                listData.addAll((List)results.values);
                notifyDataSetChanged();
            }
        }
    };
}

