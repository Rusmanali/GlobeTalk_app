package com.example.globatalk.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.globatalk.R;
import com.example.globatalk.data.Word;
import com.google.android.material.button.MaterialButton;
import java.util.ArrayList;
import java.util.List;

public class WordAdapter extends RecyclerView.Adapter<WordAdapter.WordViewHolder> {

    private List<Word> words = new ArrayList<>();
    private final OnPronounceClickListener pronounceListener;
    private OnLearnedClickListener learnedListener;

    public interface OnPronounceClickListener {
        void onPronounceClick(String text, String language);
    }

    public interface OnLearnedClickListener {
        void onLearnedClick(Word word);
    }

    public WordAdapter(OnPronounceClickListener listener) {
        this.pronounceListener = listener;
    }

    public void setOnLearnedClickListener(OnLearnedClickListener listener) {
        this.learnedListener = listener;
    }

    public void setWords(List<Word> words) {
        this.words = words;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_word, parent, false);
        return new WordViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        Word word = words.get(position);
        holder.textOriginal.setText(word.getOriginalText());
        holder.textTranslated.setText(word.getTranslatedText());
        
        holder.btnLearned.setIconResource(word.isLearned() ? 
                android.R.drawable.checkbox_on_background : 
                android.R.drawable.checkbox_off_background);
        
        holder.btnPronounce.setOnClickListener(v -> {
            if (pronounceListener != null) {
                pronounceListener.onPronounceClick(word.getOriginalText(), word.getLanguage());
            }
        });

        holder.btnLearned.setOnClickListener(v -> {
            if (learnedListener != null) {
                learnedListener.onLearnedClick(word);
            }
        });
    }

    @Override
    public int getItemCount() {
        return words.size();
    }

    static class WordViewHolder extends RecyclerView.ViewHolder {
        TextView textOriginal, textTranslated;
        View btnPronounce;
        MaterialButton btnLearned;

        public WordViewHolder(@NonNull View itemView) {
            super(itemView);
            textOriginal = itemView.findViewById(R.id.text_original);
            textTranslated = itemView.findViewById(R.id.text_translated);
            btnPronounce = itemView.findViewById(R.id.btn_pronounce);
            btnLearned = itemView.findViewById(R.id.btn_learned);
        }
    }
}
