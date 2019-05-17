package com.dmitry.pickletax;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;


public class RecyclerAdapterClassrooms extends RecyclerView.Adapter<RecyclerAdapterClassrooms.ViewHolder> {
    private ArrayList<Classroom> classrooms;
    private LayoutInflater inflater;

    RecyclerAdapterClassrooms(Context context, ArrayList<Classroom> classrooms) {
        this.inflater = LayoutInflater.from(context);
        this.classrooms = classrooms;
    }

    @NonNull
    @Override
    public RecyclerAdapterClassrooms.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.recyclerview_item_classroom, parent, false);
        return new RecyclerAdapterClassrooms.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerAdapterClassrooms.ViewHolder holder, int position) {
        Classroom classroom = classrooms.get(position);
        holder.classroomNameTextView.setText(classroom.getName());
        holder.classroomTypeTextView.setText(classroom.getType());
    }

    @Override
    public int getItemCount() {
        return classrooms.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView classroomNameTextView;
        private TextView classroomTypeTextView;

        public ViewHolder(View view) {
            super(view);
            classroomNameTextView = (TextView) view.findViewById(R.id.recyclerview_item_classroom_textview_classroom_name);
            classroomTypeTextView = (TextView) view.findViewById(R.id.recyclerview_item_classroom_textview_classroom_type);
        }
    }
}
