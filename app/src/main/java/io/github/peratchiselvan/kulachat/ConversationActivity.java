package io.github.peratchiselvan.kulachat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity implements KulaClient.Callback{

    public Client client;
    RecyclerView recyclerView_conversation;
    public ArrayList<TdApi.Chat> chatList = new ArrayList<>();
    ConversationAdapter conversationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        recyclerView_conversation = (RecyclerView) findViewById(R.id.recyclerview_conversation);
        recyclerView_conversation.setLayoutManager(new LinearLayoutManager(this));
        conversationAdapter = new ConversationAdapter(chatList);
        recyclerView_conversation.setAdapter(conversationAdapter);
        client = KulaClient.getClient(this);
        client.send(new TdApi.GetChats(Long.MAX_VALUE,0,10),this,null);
        //TdApi.Object object = Client.execute(new TdApi.GetChats(Long.MAX_VALUE,0,10));

    }

    @Override
    public void onResult(TdApi.Object object) {
        switch (object.getConstructor()){
            case TdApi.Chats.CONSTRUCTOR:
                long chatIDs[] = ((TdApi.Chats)object).chatIds;
                for (long chatID : chatIDs){
                    client.send(new TdApi.GetChat(chatID),ConversationActivity.this,new ExceptionHandler());
                }
                //Log.d("lol", "onResult: "+chatIDs.toString());
                break;
            case TdApi.Chat.CONSTRUCTOR:
                TdApi.Chat myChat = ((TdApi.Chat)object);
                chatList.add(myChat);
                conversationAdapter.notifyDataSetChanged();
                break;
            case TdApi.UpdateUser.CONSTRUCTOR:
                TdApi.UpdateUser updateUser = (TdApi.UpdateUser) object;
                TdApi.User user = updateUser.user;

        }
    }

    public class ExceptionHandler implements Client.ExceptionHandler{

        @Override
        public void onException(Throwable e) {
            e.printStackTrace();
        }
    }
}

class ConversationAdapter extends RecyclerView.Adapter<ConversationViewHolder>{

    ArrayList<TdApi.Chat> chatList;

    ConversationAdapter(ArrayList<TdApi.Chat> chatList){
        this.chatList = chatList;
    }

    @Override
    public ConversationViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LinearLayout linearLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.itemview_conversation,parent,false);
        return new ConversationViewHolder(linearLayout);
    }

    @Override
    public void onBindViewHolder(ConversationViewHolder holder, int position) {
        holder.name.setText(chatList.get(position).title);
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }
}

class ConversationViewHolder extends RecyclerView.ViewHolder{

    TextView name;
    public ConversationViewHolder(View itemView) {
        super(itemView);
        name = (TextView) itemView.findViewById(R.id.textView_name);
    }
}

