package com.example.globatalk.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.example.globatalk.databinding.FragmentChatBinding;
import com.google.ai.client.generativeai.GenerativeModel;
import com.google.ai.client.generativeai.java.GenerativeModelFutures;
import com.google.ai.client.generativeai.type.Content;
import com.google.ai.client.generativeai.type.GenerateContentResponse;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import java.util.concurrent.Executors;

public class AIChatFragment extends Fragment {
    private static final String TAG = "AIChatFragment";
    private FragmentChatBinding binding;
    private ChatAdapter adapter;
    private GenerativeModelFutures model;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentChatBinding.inflate(inflater, container, false);
        
        // Initialize Gemini Model
        // NOTE: Replace "YOUR_API_KEY" with your actual Gemini API Key from Google AI Studio
        // You can get one for free at https://aistudio.google.com/
        String apiKey = "AIzaSyCIITY6-qKrg9vGwjA17PcQ6eIjuN8K4jg";
        GenerativeModel gm = new GenerativeModel("gemini-1.5-flash", apiKey);
        model = GenerativeModelFutures.from(gm);
        
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new ChatAdapter();
        binding.rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvChat.setAdapter(adapter);

        binding.btnSend.setOnClickListener(v -> {
            String query = binding.etChatInput.getText().toString().trim();
            if (!query.isEmpty()) {
                sendMessage(query);
            }
        });
        
        // Initial welcome message
        adapter.addMessage(new ChatAdapter.ChatMessage("Hi! I'm your AI Language Tutor. Ask me any questions about grammar, vocabulary, or practice a conversation!", false));
    }

    private void sendMessage(String userText) {
        adapter.addMessage(new ChatAdapter.ChatMessage(userText, true));
        binding.etChatInput.setText("");
        binding.rvChat.scrollToPosition(adapter.getItemCount() - 1);
        
        binding.chatProgress.setVisibility(View.VISIBLE);

        Content content = new Content.Builder()
                .addText("You are a helpful language learning assistant. Answer briefly. " + userText)
                .build();

        ListenableFuture<GenerateContentResponse> response = model.generateContent(content);
        Futures.addCallback(response, new FutureCallback<GenerateContentResponse>() {
            @Override
            public void onSuccess(GenerateContentResponse result) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        binding.chatProgress.setVisibility(View.GONE);
                        adapter.addMessage(new ChatAdapter.ChatMessage(result.getText(), false));
                        binding.rvChat.scrollToPosition(adapter.getItemCount() - 1);
                    });
                }
            }

            @Override
            public void onFailure(@NonNull Throwable t) {
                if (isAdded()) {
                    requireActivity().runOnUiThread(() -> {
                        binding.chatProgress.setVisibility(View.GONE);
                        Log.e(TAG, "AI Error: " + t.getMessage());
                        adapter.addMessage(new ChatAdapter.ChatMessage("Sorry, I'm having trouble connecting right now. Please check your API key.", false));
                    });
                }
            }
        }, Executors.newSingleThreadExecutor());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
