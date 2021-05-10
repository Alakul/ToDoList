package com.example.projektzaliczeniowy;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterToDo extends BaseAdapter {

    private final Context context;
    private ArrayList<ToDoTable> toDoArrayList;
    public ArrayList<Integer> itemsSelected = new ArrayList<Integer>();

    public AdapterToDo(Context context, ArrayList<ToDoTable> toDoArrayList) {
        this.context = context;
        this.toDoArrayList = toDoArrayList;
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public Object getItem(int position)
    {
        return toDoArrayList.get(position);
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        if (view == null) {
            view= LayoutInflater.from(context).inflate(R.layout.list_layout,parent,false);
        }

        TextView task = view.findViewById(R.id.taskText);
        TextView date = view.findViewById(R.id.dateText);

        ToDoTable dataItem = (ToDoTable) getItem(position);

        ImageView check = view.findViewById(R.id.checkImage);
        if (dataItem.getChecked()==0){
            check.setImageResource(R.drawable.ic_check_gray);
        }
        else if (dataItem.getChecked()==1){
            check.setImageResource(R.drawable.ic_check_green);
        }

        ImageView priority = view.findViewById(R.id.priorityImage);
        if (dataItem.getPriority()==1){
            priority.setImageResource(R.drawable.ic_priority_high);
        }
        else if (dataItem.getPriority()==2){
            priority.setImageResource(R.drawable.ic_priority_medium);
        }
        else if (dataItem.getPriority()==3){
            priority.setImageResource(R.drawable.ic_priority_low);
        }

        task.setText(dataItem.getTask());
        date.setText(dataItem.getDate() + ", " + dataItem.getTime());

        return view;
    }

    @Override
    public int getCount()
    {
        return this.toDoArrayList.size();
    }

    public void swapItems(ArrayList<ToDoTable> items) {
        this.toDoArrayList = items;
        notifyDataSetChanged();
    }
}
