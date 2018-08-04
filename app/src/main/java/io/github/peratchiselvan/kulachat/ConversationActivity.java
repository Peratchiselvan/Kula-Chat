package io.github.peratchiselvan.kulachat;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.io.File;
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
        client.send(new TdApi.GetChats(Long.MAX_VALUE,0,1000),this,null);
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
                client.send(new TdApi.DownloadFile(myChat.photo.small.id, 1),ConversationActivity.this,null);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        conversationAdapter.refresh();
                    }
                });
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        client.close();
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
    public void onBindViewHolder(final ConversationViewHolder holder, final int position) {
        holder.name.setText(chatList.get(position).title);
        if (chatList.get(position).photo != null) {
            File imgFile = new File(chatList.get(position).photo.small.local.path);
            if(imgFile.exists()){
                Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                holder.profile.setImageBitmap(myBitmap);

            }

        }else {
            holder.profile.setImageResource(R.drawable.ic_launcher_background);
        }
        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent chatIntent = new Intent(holder.layout.getContext(),ChatActivity.class);
                chatIntent.putExtra("id",chatList.get(position).id);
                holder.layout.getContext().startActivity(chatIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void refresh(){
        notifyDataSetChanged();
    }

}

class ConversationViewHolder extends RecyclerView.ViewHolder{

    LinearLayout layout;
    TextView name;
    ImageView profile;
    public ConversationViewHolder(View itemView) {
        super(itemView);
        layout = (LinearLayout) itemView;
        name = (TextView) itemView.findViewById(R.id.textView_name);
        profile= (ImageView) itemView.findViewById(R.id.dp);
    }
}

