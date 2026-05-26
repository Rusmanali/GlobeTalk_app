package com.example.globatalk.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.globatalk.R;
import com.example.globatalk.data.Lesson;
import java.util.ArrayList;
import java.util.List;

public class LessonAdapter extends RecyclerView.Adapter<LessonAdapter.LessonViewHolder> {
    public interface OnLessonClickListener {
        void onLessonClick(Lesson lesson);
    }

    private OnLessonClickListener listener;
    private List<Lesson> lessons = new ArrayList<>();

    public void setOnLessonClickListener(OnLessonClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public LessonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_lesson, parent, false);
        return new LessonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonViewHolder holder, int position) {
        Lesson lesson = lessons.get(position);
        holder.tvOrder.setText(String.valueOf(lesson.getOrder()));
        holder.tvTitle.setText(lesson.getTitle());
        holder.tvDesc.setText(lesson.getDescription());
        holder.ivStatus.setImageResource(lesson.isCompleted() ? 
                android.R.drawable.checkbox_on_background : 
                android.R.drawable.checkbox_off_background);
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onLessonClick(lesson);
        });
    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    public void setLessons(List<Lesson> lessons) {
        this.lessons = lessons;
        notifyDataSetChanged();
    }

    static class LessonViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrder, tvTitle, tvDesc;
        ImageView ivStatus;

        public LessonViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrder = itemView.findViewById(R.id.tvLessonOrder);
            tvTitle = itemView.findViewById(R.id.tvLessonTitle);
            tvDesc = itemView.findViewById(R.id.tvLessonDesc);
            ivStatus = itemView.findViewById(R.id.ivStatus);
        }
    }
}
