package com.moutamid.jiji.bottomnavigationactivity.ui.favourite;

import static android.view.LayoutInflater.from;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.jiji.R.color.lighterGrey;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.jiji.R;
import com.moutamid.jiji.activities.ConversationActivity;
import com.moutamid.jiji.activities.ItemActivity;
import com.moutamid.jiji.databinding.FragmentFavouriteBinding;
import com.moutamid.jiji.model.ProductModel;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

import java.util.ArrayList;

public class FavouriteFragment extends Fragment {
    private FragmentFavouriteBinding b;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentFavouriteBinding.inflate(inflater, container, false);
        View root = b.getRoot();

        b.trendingRecyclerView.showShimmerAdapter();

        Constants.databaseReference()
                .child(Constants.USERS)
                .child(Constants.auth().getUid())
                .child(Constants.STARS)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {

                            tasksArrayList.clear();
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                                productModel.pushKey = dataSnapshot.getKey();
                                tasksArrayList.add(productModel);
                            }


                        }
                        initRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

        return root;
    }

    private ArrayList<ProductModel> tasksArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private void initRecyclerView() {

        conversationRecyclerView = b.trendingRecyclerView;
        //conversationRecyclerView.addItemDecoration(new DividerItemDecoration(conversationRecyclerView.getContext(), DividerItemDecoration.VERTICAL));
        adapter = new RecyclerViewAdapterMessages();
        //        LinearLayoutManager layoutManagerUserFriends = new LinearLayoutManager(mContext, LinearLayoutManager.HORIZONTAL, false);
        //    int numberOfColumns = 3;
        //int mNoOfColumns = calculateNoOfColumns(getApplicationContext(), 50);
        //  recyclerView.setLayoutManager(new GridLayoutManager(this, mNoOfColumns));
        if (isAdded()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
            //linearLayoutManager.setReverseLayout(true);
            conversationRecyclerView.setLayoutManager(linearLayoutManager);
            conversationRecyclerView.setHasFixedSize(true);
            conversationRecyclerView.setNestedScrollingEnabled(false);

            b.trendingRecyclerView.hideShimmerAdapter();
            conversationRecyclerView.setAdapter(adapter);
        }
        if (adapter.getItemCount() == 0) {
            b.noDataImg.setVisibility(View.VISIBLE);
        }

    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public RecyclerViewAdapterMessages.ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_item_product, parent, false);
            return new RecyclerViewAdapterMessages.ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewAdapterMessages.ViewHolderRightMessage holder, int position) {
            ProductModel productModel = tasksArrayList.get(position);

            with(requireContext())
                    .asBitmap()
                    .load(productModel.image1)
                    .apply(new RequestOptions()
                            .placeholder(lighterGrey)
                            .error(lighterGrey)
                    )
                    .diskCacheStrategy(DATA)
                    .into(holder.imageView);

            holder.title.setText(productModel.name);
            holder.city.setText(productModel.city == null ? "Nairobi" : productModel.city);
            holder.number.setText(productModel.number);

            holder.chatBtn.setOnClickListener(view -> {

                Intent intent = new Intent(requireContext(), ConversationActivity.class);
                intent.putExtra("first", true);
                intent.putExtra("name", productModel.postedBy);
                intent.putExtra("uid", productModel.uid);

                startActivity(intent);
            });

            holder.cardView.setOnClickListener(view -> {
                Stash.put(Constants.CURRENT_PRODUCT, productModel);
                startActivity(new Intent(requireContext(), ItemActivity.class));
            });

            holder.callBtn.setOnClickListener(view -> {
                Intent intentDial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + productModel.number));
                startActivity(intentDial);
            });

        }

        @Override
        public int getItemCount() {
            if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView title, city, number;
            LinearLayout chatBtn, callBtn;
            MaterialCardView cardView;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                imageView = v.findViewById(R.id.image);
                title = v.findViewById(R.id.title);
                city = v.findViewById(R.id.cityName);
                number = v.findViewById(R.id.price);
                chatBtn = v.findViewById(R.id.chatBtn);
                callBtn = v.findViewById(R.id.callBtn);
                cardView = v.findViewById(R.id.layoutItemProduct);
            }
        }

    }

}