package com.moutamid.jiji.bottomnavigationactivity.ui.home;

import static android.view.LayoutInflater.from;
import static com.bumptech.glide.Glide.with;
import static com.bumptech.glide.load.engine.DiskCacheStrategy.DATA;
import static com.moutamid.jiji.R.color.lighterGrey;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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
import java.util.Collections;
import java.util.Comparator;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding b;

    enum SORT {RELEVANT, LOWEST, HIGHEST}

    enum CATEGORY {PRODUCTS, SERVICES}

    SORT sortType = SORT.RELEVANT;
    CATEGORY categoryType = CATEGORY.PRODUCTS;
    private ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentHomeBinding.inflate(inflater, container, false);
        View root = b.getRoot();

        progressDialog = new ProgressDialog(requireContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Loading...");

        getData(Constants.TYPE_PRODUCT);

        b.productsBtn.setOnClickListener(view -> {
            categoryType = CATEGORY.PRODUCTS;
            getData(Constants.TYPE_PRODUCT);
            b.productsBtn.setCardBackgroundColor(getResources().getColor(R.color.default_green));
            b.productsBtnTv.setTextColor(getResources().getColor(R.color.white));

            b.servicesCatBtn.setCardBackgroundColor(getResources().getColor(R.color.grey2));
            b.servicesCatBtnTv.setTextColor(getResources().getColor(R.color.black));

            int scrollTo = ((View) b.trendingRecyclerView.getParent().getParent()).getTop() + b.trendingRecyclerView.getTop();
            b.homeScrollView.smoothScrollTo(0, scrollTo);
        });

        b.servicesCatBtn.setOnClickListener(view -> {
            categoryType = CATEGORY.SERVICES;
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

        Dialog dialog = new Dialog(requireActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_layout_search);
        dialog.setCancelable(true);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(dialog.getWindow().getAttributes());
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;

        dialog.findViewById(R.id.closeDialogBtn).setOnClickListener(view1 -> {
            dialog.dismiss();
        });

        TextView categoryText = dialog.findViewById(R.id.categoryText);
        TextView sortText = dialog.findViewById(R.id.sortText);
        EditText specializationEt = dialog.findViewById(R.id.specialiazationEt);

        dialog.findViewById(R.id.categoryBtn).setOnClickListener(v -> {
            AlertDialog dialog1;

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            final CharSequence[] items = new CharSequence[]{
                    "Toyota", "Nissan", "Honda", "Mazda", "Suzuki",
                    "BMW", "LEXUS", "AUDI", "Land rover", "Mercedes Benz", "Mitsubishi", "Isuzu", "Hino",
                    "Chevloret", "Volkswagen", "Jeep", "Subaru", "Porsche"};

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    categoryText.setText(items[position].toString());

                    /*progressDialog.show();

                    new Thread(() -> {
                        tasksArrayList.clear();
                        tasksArrayList = tasksArrayListAll;

                        for (int i = 0; i <= tasksArrayList.size() - 1; i++) {
                            ProductModel model1 = tasksArrayList.get(i);
                            if (!model1.category.equals(items[position].toString())) {
                                tasksArrayList.remove(i);
                            }

                        }

                        requireActivity().runOnUiThread(() -> {
                            progressDialog.dismiss();
                        });

                    }).start();*/
                }
            });

            dialog1 = builder.create();
            dialog1.show();
        });

        dialog.findViewById(R.id.sortBtn).setOnClickListener(v -> {
            AlertDialog dialog1;

            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            final CharSequence[] items = new CharSequence[]{
                    "Relevant", "Lowest price", "Highest price"};

            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int position) {
                    sortText.setText(items[position]);
                    if (position == 0) {
                        sortType = SORT.RELEVANT;
                    }
                    if (position == 1) {
                        sortType = SORT.LOWEST;
                    }
                    if (position == 2) {
                        sortType = SORT.HIGHEST;
                    }
                }
            });

            dialog1 = builder.create();
            dialog1.show();
        });

        dialog.findViewById(R.id.doneBtnDialog).setOnClickListener(view -> {
            if (categoryType == CATEGORY.PRODUCTS) {
                if (categoryText.equals("Category")) {
                    Toast.makeText(requireContext(), "Please select a category!", Toast.LENGTH_SHORT).show();
                    return;
                }
            } else {
                if (specializationEt.getText().toString().isEmpty()) {
                    Toast.makeText(requireContext(), "Please type a specialization!", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            EditText priceMin = dialog.findViewById(R.id.priceMin);
            EditText priceMax = dialog.findViewById(R.id.priceMax);

            if (priceMin.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter minimum price!", Toast.LENGTH_SHORT).show();
                return;
            }
            if (priceMin.getText().toString().isEmpty()) {
                Toast.makeText(requireContext(), "Please enter maximum price!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressDialog.show();

            new Thread(() -> {
                tasksArrayList.clear();
                tasksArrayList.addAll(tasksArrayListAll);

                // CATEGORY
                for (int i = 0; i <= tasksArrayList.size() - 1; i++) {
                    ProductModel model1 = tasksArrayList.get(i);
                    if (!model1.category.equals(categoryText.getText().toString())) {
                        tasksArrayList.remove(i);
                    }
                }

                // PRICE MINIMUM
                for (int i = 0; i <= tasksArrayList.size() - 1; i++) {
                    ProductModel model1 = tasksArrayList.get(i);
                    if (Integer.parseInt(model1.price) < Integer.parseInt(priceMin.getText().toString())) {
                        tasksArrayList.remove(i);
                    }
                }

                // PRICE MAXIMUM
                for (int i = 0; i <= tasksArrayList.size() - 1; i++) {
                    ProductModel model1 = tasksArrayList.get(i);
                    if (Integer.parseInt(model1.price) > Integer.parseInt(priceMax.getText().toString())) {
                        tasksArrayList.remove(i);
                    }
                }

                if (sortType == SORT.RELEVANT) {
                    Collections.shuffle(tasksArrayList);
                }
                if (sortType == SORT.LOWEST) {
                    Collections.sort(tasksArrayList, new Comparator<ProductModel>() {
                        @Override
                        public int compare(ProductModel productModel, ProductModel t1) {
                            return productModel.price.compareTo(t1.price);
                        }
                    });
                }
                if (sortType == SORT.HIGHEST) {
                    Collections.sort(tasksArrayList, new Comparator<ProductModel>() {
                        @Override
                        public int compare(ProductModel productModel, ProductModel t1) {
                            return productModel.price.compareTo(t1.price);
                        }
                    });
                    Collections.reverse(tasksArrayList);
                }

                requireActivity().runOnUiThread(() -> {
                    b.adsAmountTv.setText(adapter.getItemCount() + " Ads");
                    b.removeFilterBtn.setVisibility(View.VISIBLE);
                    dialog.dismiss();
                    adapter.notifyDataSetChanged();
                    progressDialog.dismiss();
                });

            }).start();
        });

        b.removeFilterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                b.removeFilterBtn.setVisibility(View.GONE);
                tasksArrayList.clear();
                tasksArrayList.addAll(tasksArrayListAll);

                adapter.notifyDataSetChanged();

            }
        });

        b.menuBtnHome.setOnClickListener(view -> {
            if (categoryType == CATEGORY.SERVICES) {
                dialog.findViewById(R.id.categoryBtn).setVisibility(View.GONE);
                specializationEt.setVisibility(View.VISIBLE);
            } else {
                dialog.findViewById(R.id.categoryBtn).setVisibility(View.VISIBLE);
                specializationEt.setVisibility(View.GONE);
            }

            dialog.show();
            dialog.getWindow().setAttributes(layoutParams);
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
//                Toast.makeText(requireContext(), "" + productModel.number, Toast.LENGTH_SHORT).show();
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