package com.moutamid.jiji.bottomnavigationactivity.ui.profile;

import static com.moutamid.jiji.utils.Stash.toast;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.moutamid.jiji.activities.MyAdsActivity;
import com.moutamid.jiji.bottomnavigationactivity.BottomNavigationActivity;
import com.moutamid.jiji.databinding.FragmentProfileBinding;
import com.moutamid.jiji.model.UserModel;
import com.moutamid.jiji.utils.Constants;
import com.moutamid.jiji.utils.Stash;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding b;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        b = FragmentProfileBinding.inflate(inflater, container, false);
        View root = b.getRoot();

        UserModel userModel = (UserModel) Stash.getObject(Constants.CURRENT_USER_MODEL, UserModel.class);

        b.nameEt.setText(userModel.name);
        b.numberEt.setText(userModel.number);
        b.emailEt.setText(userModel.email);

        b.nameEt.addTextChangedListener(textChangeListener(Constants.TYPE_NAME));
        b.numberEt.addTextChangedListener(textChangeListener(Constants.TYPE_NUMBER));
        b.emailEt.addTextChangedListener(textChangeListener(Constants.TYPE_EMAIL));
        b.passwordEt.addTextChangedListener(passwordTextChangeListener());

        b.myAdsBtn.setOnClickListener(view -> {
            startActivity(new Intent(requireActivity(), MyAdsActivity.class));
        });

        b.logout.setOnClickListener(view -> {
            Stash.clearAll();
            Constants.auth().signOut();
            Intent intent = new Intent(requireContext(), BottomNavigationActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            requireActivity().finish();
            requireActivity().startActivity(intent);
        });

        b.doneBtn.setOnClickListener(view -> {
            if (b.passwordEt.getText().toString().isEmpty())
                return;

            Constants.auth().getCurrentUser().updatePassword(b.passwordEt.getText().toString())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                toast("Done");
                                b.passwordEt.setText("");
                            } else {
                                toast(task.getException().getMessage());
                            }
                        }
                    });

        });

        return root;
    }

    private TextWatcher textChangeListener(String et) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                b.doneBtn.setVisibility(View.GONE);
                if (charSequence.toString().isEmpty())
                    return;
                if (et.equals(Constants.TYPE_NAME)) {
                    Constants.databaseReference()
                            .child(Constants.USERS)
                            .child(Constants.auth().getUid())
                            .child("name")
                            .setValue(charSequence.toString());
                }
                if (et.equals(Constants.TYPE_NUMBER)) {
                    Constants.databaseReference()
                            .child(Constants.USERS)
                            .child(Constants.auth().getUid())
                            .child("number")
                            .setValue(charSequence.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }

    private TextWatcher passwordTextChangeListener() {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                b.doneBtn.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        };
    }
}