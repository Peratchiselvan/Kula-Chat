package io.github.peratchiselvan.kulachat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.drinkless.td.libcore.telegram.Client;
import org.drinkless.td.libcore.telegram.TdApi;
import org.drinkless.td.libcore.telegram.TdApi.*;


public class MainActivity extends AppCompatActivity implements KulaClient.Callback {

    private final static String TAG = "MainActivity";
    public Client client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TdApi.GetAuthorizationState AuthState = new TdApi.GetAuthorizationState();
        client = KulaClient.getClient(this);
        //client.setUpdatesHandler(new UpdatesHandler());
        client.send(AuthState,this);
    }


        public void onResult(TdApi.Object object) {
            switch (object.getConstructor()) {
                case TdApi.UpdateAuthorizationState.CONSTRUCTOR:
                    Log.d(TAG, "onResult: UpdateAuthState");
                    onAuthStateUpdated(((TdApi.UpdateAuthorizationState) object).authorizationState);
                    break;
                case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                    Log.d(TAG, "onResult: TDlibParams");
                    TdApi.TdlibParameters authStateRequest = new TdApi.TdlibParameters();
                    authStateRequest.apiId = 193316;
                    authStateRequest.apiHash = "69f1baef48f39fc3a966a4d648b4c909";
                    authStateRequest.useMessageDatabase = true;
                    authStateRequest.useSecretChats = true;
                    authStateRequest.systemLanguageCode = "en";
                    authStateRequest.databaseDirectory = getApplicationContext().getFilesDir().getAbsolutePath();
                    authStateRequest.deviceModel = "Moto";
                    authStateRequest.systemVersion = "7.0";
                    authStateRequest.applicationVersion = "0.1";
                    authStateRequest.enableStorageOptimizer = true;
                    client.send(new TdApi.SetTdlibParameters(authStateRequest), this);
                    break;
                case AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                    client.send(new CheckDatabaseEncryptionKey(), this);
                    TdApi.GetAuthorizationState AuthState = new TdApi.GetAuthorizationState();
                    client.send(AuthState,this);
                    break;
                case AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
                    client.send(new SetAuthenticationPhoneNumber("+917418189531", false, true), this);
                    break;
                case AuthorizationStateWaitCode.CONSTRUCTOR:
                    Intent AuthCodeIntent = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(AuthCodeIntent);
                    finish();
                    break;
                case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                    Intent conversationIntent = new Intent(MainActivity.this, ConversationActivity.class);
                    //conversationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(conversationIntent);
                    finish();
            }
        }

        private void onAuthStateUpdated(TdApi.AuthorizationState authorizationState) {
            switch (authorizationState.getConstructor()) {
                case TdApi.AuthorizationStateWaitTdlibParameters.CONSTRUCTOR:
                    Log.d(TAG, "onResult: TDlibParams");
                    TdApi.TdlibParameters authStateRequest = new TdApi.TdlibParameters();
                    authStateRequest.apiId = 193316;
                    authStateRequest.apiHash = "69f1baef48f39fc3a966a4d648b4c909";
                    authStateRequest.useMessageDatabase = true;
                    authStateRequest.useSecretChats = true;
                    authStateRequest.systemLanguageCode = "en";
                    authStateRequest.databaseDirectory = getApplicationContext().getFilesDir().getAbsolutePath();
                    authStateRequest.deviceModel = "Moto";
                    authStateRequest.systemVersion = "7.0";
                    authStateRequest.applicationVersion = "0.1";
                    authStateRequest.enableStorageOptimizer = true;
                    client.send(new TdApi.SetTdlibParameters(authStateRequest), this);
                    break;
                case AuthorizationStateWaitEncryptionKey.CONSTRUCTOR:
                    client.send(new CheckDatabaseEncryptionKey(), this);
                    break;
                case AuthorizationStateWaitPhoneNumber.CONSTRUCTOR:
                    client.send(new SetAuthenticationPhoneNumber("+917418189531", false, true),this);
                    break;
                case AuthorizationStateWaitCode.CONSTRUCTOR:
                    Intent AuthCodeIntent = new Intent(MainActivity.this, AuthActivity.class);
                    startActivity(AuthCodeIntent);
                    finish();
                    break;
                case TdApi.AuthorizationStateReady.CONSTRUCTOR:
                    Intent conversationIntent = new Intent(MainActivity.this, ConversationActivity.class);
                    //conversationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(conversationIntent);
                    finish();
            }

    }
}

