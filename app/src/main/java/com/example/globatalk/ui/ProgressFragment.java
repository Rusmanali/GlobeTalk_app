package com.example.globatalk.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.globatalk.databinding.FragmentProgressBinding;
import android.preference.PreferenceManager;

public class ProgressFragment extends Fragment {
    private FragmentProgressBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentProgressBinding.inflate(inflater, container, false);
        
        // Load real data if available
        int points = PreferenceManager.getDefaultSharedPreferences(getContext()).getInt("total_points", 0);
        // For demonstration, deriving some stats from points
        int words = points / 10; 
        int accuracy = 75 + (points % 25); // Just a placeholder formula
        
        binding.tvTotalWords.setText(String.valueOf(words));
        binding.tvAccuracy.setText(accuracy + "%");

        return binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
