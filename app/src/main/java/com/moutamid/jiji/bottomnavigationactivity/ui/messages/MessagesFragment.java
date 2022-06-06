package com.moutamid.jiji.bottomnavigationactivity.ui.messages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.moutamid.jiji.R;
import com.moutamid.jiji.activities.ConversationActivity;
import com.moutamid.jiji.model.ChatModel;
import com.moutamid.jiji.utils.Constants;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagesFragment extends Fragment {

    private static final String TAG = "ChatFragment";

    private ArrayList<ChatModel> chatsArrayList = new ArrayList<>();

    private RecyclerView conversationRecyclerView;
    private RecyclerViewAdapterMessages adapter;

    private com.moutamid.jiji.databinding.FragmentMessagesBinding b;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = com.moutamid.jiji.databinding.FragmentMessagesBinding.inflate(inflater, container, false);
        View root = b.getRoot();

        b.chatsRecyclerview.showShimmerAdapter();

        Constants.databaseReference().child("chats").child(Constants.auth().getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            initRecyclerView();
                            return;
                        }

                        chatsArrayList.clear();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            ChatModel model = dataSnapshot.getValue(ChatModel.class);
                            model.setUid(dataSnapshot.getKey());
                            chatsArrayList.add(model);
                        }

                        initRecyclerView();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(requireContext(), error.toException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        return root;
    }

    private void initRecyclerView() {
        conversationRecyclerView = b.chatsRecyclerview;
        adapter = new RecyclerViewAdapterMessages();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());

        conversationRecyclerView.setLayoutManager(linearLayoutManager);
        conversationRecyclerView.setHasFixedSize(true);
        conversationRecyclerView.setNestedScrollingEnabled(false);

        b.chatsRecyclerview.hideShimmerAdapter();
        conversationRecyclerView.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            b.noDataImg.setVisibility(View.VISIBLE);
        }

    }

    private class RecyclerViewAdapterMessages extends RecyclerView.Adapter
            <RecyclerViewAdapterMessages.ViewHolderRightMessage> {

        @NonNull
        @Override
        public ViewHolderRightMessage onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_chats_fragment, parent, false);
            return new ViewHolderRightMessage(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolderRightMessage holder, int position) {
            ChatModel chatModel = chatsArrayList.get(position);
            Log.e(TAG, "onBindViewHolder: 1234: name: " + chatModel.getName());
//            Log.e(TAG, "onBindViewHolder: 1234: url: " + chatModel.getImageUrl());
            holder.chatName.setText(chatModel.getName());
            holder.lastMessage.setText(chatModel.getLastMcg());

//            String url = chatModel.getImageUrl().equals("Error") ? "https://cdn.pixabay.com/photo/2019/08/06/00/46/black-and-white-4387130_960_720.jpg"
//                    : chatModel.getImageUrl();
//            with(getActivity())
//                    .load(url)
//                    .apply(new RequestOptions()
//                            .placeholder(R.color.grey)
//                            .error(R.color.grey)
//                    )
//                    .diskCacheStrategy(DiskCacheStrategy.DATA)
//                    .into(holder.profileImage);

            holder.parentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), ConversationActivity.class);
                    intent.putExtra("name", chatModel.getName());
//                    intent.putExtra("url", chatModel.getImageUrl());
                    intent.putExtra("uid", chatModel.getUid());
                    startActivity(intent);
                }
            });
        }

        @Override
        public int getItemCount() {
            if (chatsArrayList == null)
                return 0;
            return chatsArrayList.size();
        }

        public class ViewHolderRightMessage extends RecyclerView.ViewHolder {

            TextView chatName, lastMessage;
            CircleImageView profileImage;
            CardView parentLayout;

            public ViewHolderRightMessage(@NonNull View v) {
                super(v);
                chatName = v.findViewById(R.id.username_textview_chats);
                lastMessage = v.findViewById(R.id.last_message_chats);
                profileImage = v.findViewById(R.id.profile_image_chats);
                parentLayout = v.findViewById(R.id.parent_layout);

            }
        }

    }

}