package com.moutamid.jiji.activities;

import static android.view.LayoutInflater.from;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.jiji.R.color.lighterGrey;
import static com.moutamid.jiji.utils.Stash.toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.jiji.R;
import com.moutamid.jiji.bottomnavigationactivity.ui.home.HomeFragment;
import com.moutamid.jiji.databinding.ActivityMyAdsBinding;
import com.moutamid.jiji.model.ProductModel;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

import java.util.ArrayList;

public class MyAdsActivity extends AppCompatActivity {

    private ActivityMyAdsBinding b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        b = ActivityMyAdsBinding.inflate(getLayoutInflater());
        setContentView(b.getRoot());

        b.backbtnConversationActivity.setOnClickListener(view -> {
            finish();
        });

        Constants.databaseReference()
                .child(Constants.TYPE_PRODUCT)
                .orderByChild("uid").equalTo(Constants.auth().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            tasksArrayList.clear();
                            tasksArrayListAll.clear();

                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                                productModel.pushKey = dataSnapshot.getKey();
                                tasksArrayList.add(productModel);
                                tasksArrayListAll.add(productModel);
                            }

                            Constants.databaseReference()
                                    .child(Constants.TYPE_SERVICE)
                                    .orderByChild("uid").equalTo(Constants.auth().getUid())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists())
                                                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                                    ProductModel productModel = dataSnapshot.getValue(ProductModel.class);
                                                    productModel.pushKey = dataSnapshot.getKey();
                                                    tasksArrayList.add(productModel);
                                                    tasksArrayListAll.add(productModel);
                                                }

                                            initRecyclerView();
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            toast(error.toException().getMessage());
                                        }
                                    });

                        } else {
                            initRecyclerView();
//                            Toast.makeText(MyAdsActivity.this, "No data exist!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(MyAdsActivity.this, error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private ArrayList<ProductModel> tasksArrayList = new ArrayList<>();
    private ArrayList<ProductModel> tasksArrayListAll = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private void initRecyclerView() {
        conversationRecyclerView = b.adsRecyclerView;
        adapter = new RecyclerViewAdapterMessages();

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        //linearLayoutManager.setReverseLayout(true);
        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        b.adsRecyclerView.hideShimmerAdapter();
        conversationRecyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            conversationRecyclerView.setVisibility(View.GONE);
            b.noDataImg.setVisibility(View.VISIBLE);
        } else {
            conversationRecyclerView.setVisibility(View.VISIBLE);
            b.noDataImg.setVisibility(View.GONE);
        }

    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> implements Filterable {

        @NonNull
        @Override
        public RecyclerViewAdapterMessages.ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_item_product_admin, parent, false);
            return new RecyclerViewAdapterMessages.ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final RecyclerViewAdapterMessages.ViewHolderRightMessage holder, int position) {
            ProductModel productModel = tasksArrayList.get(position);

            with(getApplicationContext())
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
            holder.price.setText(productModel.price);

            holder.chatBtn.setOnClickListener(view -> {

                Intent intent = new Intent(MyAdsActivity.this, ConversationActivity.class);
                intent.putExtra("first", true);
                intent.putExtra("name", productModel.postedBy);
                intent.putExtra("uid", productModel.uid);

                startActivity(intent);
            });

            holder.cardView.setOnClickListener(view -> {
                Stash.put(Constants.CURRENT_PRODUCT, productModel);
                startActivity(new Intent(MyAdsActivity.this, ItemActivity.class));
            });

            holder.callBtn.setOnClickListener(view -> {
                new AlertDialog.Builder(MyAdsActivity.this)
                        .setTitle("Are you sure?")
                        .setMessage("Do you really want to delete this ad?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            Constants.databaseReference()
                                    .child(productModel.type)
                                    .child(productModel.pushKey)
                                    .removeValue();

                            tasksArrayList.remove(position);
                            tasksArrayListAll.remove(position);

                            initRecyclerView();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .show();

            });

        }

        @Override
        public int getItemCount() {
            if (tasksArrayList == null)
                return 0;
            return tasksArrayList.size();
        }

        @Override
        public Filter getFilter() {
            return new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    ArrayList<ProductModel> filteredList = new ArrayList<>();

                    if (constraint == null
                            || constraint.length() == 0
                            || constraint.toString().trim().equals("")
                            || constraint.toString() == null) {

                        filteredList.addAll(tasksArrayListAll);
                    } else {
                        String filterPattern = constraint.toString().toLowerCase().trim();

                        for (ProductModel item : tasksArrayListAll) {
                            if (item.name != null)
                                if (item.name.toLowerCase().contains(filterPattern)) {
                                    filteredList.add(item);
                                }
                        }
                    }

                    FilterResults results = new FilterResults();
                    results.values = filteredList;

                    return results;
                }

                @Override
                protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                    tasksArrayList.clear();

                    tasksArrayList.addAll((ArrayList<ProductModel>) filterResults.values);
                    notifyDataSetChanged();
                }
            };

        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView title, city, price;
            LinearLayout chatBtn, callBtn;
            MaterialCardView cardView;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                imageView = v.findViewById(R.id.image);
                title = v.findViewById(R.id.title);
                city = v.findViewById(R.id.cityName);
                price = v.findViewById(R.id.price);
                chatBtn = v.findViewById(R.id.chatBtn);
                callBtn = v.findViewById(R.id.callBtn);
                cardView = v.findViewById(R.id.layoutItemProduct);
            }
        }

    }
}