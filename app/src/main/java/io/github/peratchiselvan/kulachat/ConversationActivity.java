package io.github.peratchiselvan.kulachat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;

import java.util.ArrayList;

public class ConversationActivity extends AppCompatActivity implements KulaClient.Callback{

    public Client client;
    public TextView text;
    public String selva = "";
    public ArrayList<TdApi.Chat> chatList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        text = (TextView) findViewById(R.id.selvasoft);
        //text.setText("fk google!");
        client = KulaClient.getClient(this);
        client.send(new TdApi.GetChats(Long.MAX_VALUE,0,10),this,null);
    }

    @Override
    public void onResult(TdApi.Object object) {
        switch (object.getConstructor()){
            case TdApi.Chats.CONSTRUCTOR:
                long chatIDs[] = ((TdApi.Chats)object).chatIds;
                for (long chatID : chatIDs){
                    Log.d("lol", "onResult: "+chatID);
                    client.send(new TdApi.GetChat(chatID),ConversationActivity.this,new ExceptionHandler());
                }
                //Log.d("lol", "onResult: "+chatIDs.toString());
                break;
            case TdApi.Chat.CONSTRUCTOR:

                TdApi.Chat myChat = ((TdApi.Chat)object);
                chatList.add(myChat);
                for (TdApi.Chat chat : chatList){
                    selva += chat.title+"\n";
                }
                Log.d("lol", "onResult: "+selva);
                text.setText(selva);
                break;
            case TdApi.UpdateUser.CONSTRUCTOR:

        }
    }

    public class ExceptionHandler implements Client.ExceptionHandler{

        @Override
        public void onException(Throwable e) {
            e.printStackTrace();
        }
    }
}
