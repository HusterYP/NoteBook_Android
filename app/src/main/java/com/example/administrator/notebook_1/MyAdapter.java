package com.example.administrator.notebook_1;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Administrator on 2017/7/20.
 */

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> implements Serializable {
    private List<Event> events;

    public MyAdapter(List<Event> events) {
        this.events = events;
    }

    //增加数据
    public void addEvent(int position, Event e) {
        events.add(position, e);
        notifyItemInserted(position);
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = View.inflate(parent.getContext(), R.layout.edit_layout, null);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Event e = events.get(position);
        holder.showTime.setText(e.getTime());
        if (e.getComplete() == 0) {
            holder.complete.setImageResource(R.drawable.cry);
        }
        else {
            holder.complete.setImageResource(R.drawable.happy);
        }
        holder.content.setText(e.getContent());
        holder.priority.setText(e.getPriority());
        holder.title.setText(e.getTitle());
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView showTime;
        private TextView title;
        private TextView content;
        private TextView priority;
        private ImageView complete;

        public MyViewHolder(View itemView) {
            super(itemView);
            //设置每个View的ID，方便对应查找数据库中的数据
            if (GlobalName.sortFlag == 0) {
                itemView.setId(GlobalName.defaultCount++);
//                GlobalName.priorityCount++;
//                GlobalName.timeCount++;
            }
            else if (GlobalName.sortFlag == 1) {
                itemView.setId(GlobalName.priorityCount++);
//                GlobalName.defaultCount++;
//                GlobalName.timeCount++;
            }
            else if (GlobalName.sortFlag == 2) {
                itemView.setId(GlobalName.timeCount++);
//                GlobalName.priorityCount++;
//                GlobalName.defaultCount++;
            }
            showTime = itemView.findViewById(R.id.show_time);
            title = itemView.findViewById(R.id.title);
            content = itemView.findViewById(R.id.event_description);
            priority = itemView.findViewById(R.id.priority);
            complete = itemView.findViewById(R.id.complete);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onItemClickListener != null) {
                        onItemClickListener.OnItemClick(view, getLayoutPosition(), view.getId());
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    onItemClickListener.OnItemLongClick(view, getLayoutPosition());
                    return true;
                }
            });
        }
    }

    //设置Item的点击事件
    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener {
        void OnItemClick(View view, int pos, int ID); //单击事件

        void OnItemLongClick(View view, int pos);      //长按事件
    }

    public void SetOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
