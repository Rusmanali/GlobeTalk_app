package com.example.globatalk.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.widget.Toast;
import com.example.globatalk.databinding.FragmentProfileBinding;
import com.example.globatalk.util.StreakManager;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String name = user.getDisplayName();
            String email = user.getEmail();
            binding.tvName.setText(name != null && !name.isEmpty() ? name : "Language Learner");
            binding.tvEmail.setText(email != null ? email : "Anonymous");
        }

        binding.tvStreak.setText(String.valueOf(StreakManager.getStreak(getContext())));
        binding.tvPoints.setText(String.valueOf(android.preference.PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("total_points", 0)));

        binding.btnEditProfile.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Edit Profile coming soon!", Toast.LENGTH_SHORT).show()
        );

        binding.btnSettings.setOnClickListener(v -> 
            Toast.makeText(getContext(), "Settings coming soon!", Toast.LENGTH_SHORT).show()
        );

        binding.btnLogout.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return binding.getRoot();
    }
}
