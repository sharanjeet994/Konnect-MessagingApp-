package com.example.android.konnect.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.android.konnect.Models.Messages;
import com.example.android.konnect.Models.Users;
import com.example.android.konnect.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class ChatsAdapter extends RecyclerView.Adapter {

    ArrayList<Messages> messagesArrayList;
    Context context;

    FirebaseAuth auth;

    int SENDER_VIEW_TYPE = 1;
    int RECEIVER_VIEW_TYPE = 2;

    String receiverId;

    public ChatsAdapter(ArrayList<Messages> messagesArrayList, Context context) {
        this.messagesArrayList = messagesArrayList;
        this.context = context;
    }

    public ChatsAdapter(ArrayList<Messages> messagesArrayList, Context context, String receiverId) {
        this.messagesArrayList = messagesArrayList;
        this.context = context;
        this.receiverId = receiverId;
    }

    @Override
    public int getItemViewType(int position) {

        if (messagesArrayList.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())){

            return SENDER_VIEW_TYPE;

        }
        else {
            return RECEIVER_VIEW_TYPE;
        }


    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == SENDER_VIEW_TYPE){
            View view = LayoutInflater.from(context).inflate(R.layout.sample_sender,parent,false);
            return new SenderViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(context).inflate(R.layout.sample_receiver,parent,false);
            return new ReceiverViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        Messages messages = messagesArrayList.get(position);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                new AlertDialog.Builder(context)
                        .setTitle("Delete Message ").setMessage("Are you sure ? ")
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                String senderSide = FirebaseAuth.getInstance().getUid() + receiverId;
                                database.getReference().child("chats").child(senderSide)
                                        .child(messages.getMessageId()).setValue(null);


                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();

                    }
                }).show();

                return false;
            }
        });


        if (holder.getClass() == SenderViewHolder.class){
            ((SenderViewHolder)holder).senderMessage.setText(messages.getMessage());

//            ((SenderViewHolder)holder).senderTime.setText(String.valueOf(messages.getTimeStamp()));

        }
        else{
            ((ReceiverViewHolder)holder).receiverMessage.setText(messages.getMessage());
//            ((ReceiverViewHolder)holder).receiverTime.setText(String.valueOf(messages.getTimeStamp()));

        }

    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }

    // we need 2 view holders one for receiver the other for sender

    public class ReceiverViewHolder extends RecyclerView.ViewHolder {

        TextView receiverMessage,receiverTime;

        public ReceiverViewHolder(@NonNull View itemView) {
            super(itemView);

            receiverMessage = itemView.findViewById(R.id.receiverMessage);
            receiverTime = itemView.findViewById(R.id.receiverTime);
        }
    }

    public class SenderViewHolder extends RecyclerView.ViewHolder {

        TextView senderMessage,senderTime;

        public SenderViewHolder(@NonNull View itemView) {
            super(itemView);
            senderMessage = itemView.findViewById(R.id.senderMessage);
            senderTime = itemView.findViewById(R.id.senderTime);

        }
    }


}
