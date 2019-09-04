package com.example.chatroom.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.chatroom.Chat_room;
import com.example.chatroom.MessageActivity;
import com.example.chatroom.R;

import org.apache.http.conn.ConnectTimeoutException;

import java.util.ArrayList;

public class Chat_Adapter extends RecyclerView.Adapter<Chat_Adapter.ViewHolder> {
    @NonNull
    private Context context;
    private ArrayList<Chat_room> chat_group;
    public static String group_key="group";
    public Chat_Adapter(ArrayList<Chat_room> chat_room, Context context){
        System.out.println(chat_room.size());
        this.chat_group=chat_room;
        this.context=context;

    }
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view= LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.chat_layout,viewGroup,false);

        return new Chat_Adapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final Chat_room group =chat_group.get(i);
        viewHolder.textView.setText(group.name);
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessageActivity.class);
                intent.putExtra(group_key,group);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return chat_group.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView=itemView.findViewById(R.id.group_name);

        }
    }
}
