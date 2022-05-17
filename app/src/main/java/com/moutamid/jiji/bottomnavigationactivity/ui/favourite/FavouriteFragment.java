package com.moutamid.jiji.bottomnavigationactivity.ui.favourite;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.moutamid.jiji.databinding.FragmentFavouriteBinding;

public class FavouriteFragment extends Fragment {
    private FragmentFavouriteBinding b;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentFavouriteBinding.inflate(inflater, container, false);
        View root = b.getRoot();


        return root;
    }
}