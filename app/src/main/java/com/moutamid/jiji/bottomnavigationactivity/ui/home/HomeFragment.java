package com.moutamid.jiji.bottomnavigationactivity.ui.home;

import static android.view.LayoutInflater.from;

import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.jiji.R.color.lighterGrey;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

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
import com.moutamid.jiji.databinding.FragmentHomeBinding;
import com.moutamid.jiji.model.ProductModel;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding b;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(inflater, container, false);
        View root = b.getRoot();

        getData(Constants.TYPE_PRODUCT);

        b.productsBtn.setOnClickListener(view -> {
            getData(Constants.TYPE_PRODUCT);
            b.productsBtn.setCardBackgroundColor(getResources().getColor(R.color.default_green));
            b.productsBtnTv.setTextColor(getResources().getColor(R.color.white));

            b.servicesCatBtn.setCardBackgroundColor(getResources().getColor(R.color.grey2));
            b.servicesCatBtnTv.setTextColor(getResources().getColor(R.color.black));

            int scrollTo = ((View) b.trendingRecyclerView.getParent().getParent()).getTop() + b.trendingRecyclerView.getTop();
            b.homeScrollView.smoothScrollTo(0, scrollTo);
        });

        b.servicesCatBtn.setOnClickListener(view -> {
            getData(Constants.TYPE_SERVICE);
            b.productsBtn.setCardBackgroundColor(getResources().getColor(R.color.grey2));
            b.productsBtnTv.setTextColor(getResources().getColor(R.color.black));

            b.servicesCatBtn.setCardBackgroundColor(getResources().getColor(R.color.default_green));
            b.servicesCatBtnTv.setTextColor(getResources().getColor(R.color.white));

            int scrollTo = ((View) b.trendingRecyclerView.getParent().getParent()).getTop() + b.trendingRecyclerView.getTop();
            b.homeScrollView.smoothScrollTo(0, scrollTo);
        });

        b.searchEtHome.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (adapter != null)
                    adapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return root;
    }

    private void getData(String DATA) {
        b.trendingRecyclerView.showShimmerAdapter();
        Constants.databaseReference()
                .child(DATA)
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

                            initRecyclerView();
                            b.adsAmountTv.setText(snapshot.getChildrenCount() + " Ads");
                        } else {
                            Toast.makeText(requireContext(), "No data exist!", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        b.trendingRecyclerView.hideShimmerAdapter();
                        Toast.makeText(requireContext(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private ArrayList<ProductModel> tasksArrayList = new ArrayList<>();
    private ArrayList<ProductModel> tasksArrayListAll = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private void initRecyclerView() {
        conversationRecyclerView = b.trendingRecyclerView;
        adapter = new RecyclerViewAdapterMessages();
        if (isAdded()) {
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(requireActivity());
            //linearLayoutManager.setReverseLayout(true);
            conversationRecyclerView.setLayoutManager(linearLayoutManager);
            conversationRecyclerView.setHasFixedSize(true);
            conversationRecyclerView.setNestedScrollingEnabled(false);

            conversationRecyclerView.setAdapter(adapter);
            b.trendingRecyclerView.hideShimmerAdapter();
        }
    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> implements Filterable {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = from(parent.getContext()).inflate(R.layout.layout_item_product, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
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
            holder.price.setText(productModel.price);

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