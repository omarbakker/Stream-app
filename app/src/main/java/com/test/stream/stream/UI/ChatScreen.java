package com.test.stream.stream.UI;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;

import com.test.stream.stream.Controllers.UserManager;
import com.test.stream.stream.Objects.Chat.Message;
import com.test.stream.stream.Objects.Users.User;
import com.test.stream.stream.R;
import com.test.stream.stream.Controllers.ChatManager;
import com.test.stream.stream.Utilities.Callbacks.FetchUserCallback;
import com.test.stream.stream.Utilities.Callbacks.GetTextCallback;

/**
 * Created by cathe on 2016-10-14.
 * Note: Several parts of this class are based on the friendly chat tutorial from Firebase:
 * https://github.com/firebase/friendlychat. Mainly UI.
 */

public class ChatScreen extends AppCompatActivity {
    public static class MessageViewHolder extends RecyclerView.ViewHolder {
        public TextView messageTextView;
        public TextView messengerTextView;

        public MessageViewHolder(View v) {
            super(v);
            messageTextView = (TextView) itemView.findViewById(R.id.messageTextView);
            messengerTextView = (TextView) itemView.findViewById(R.id.messengerTextView);
        }
    }

    private String username;

    private Button sendButton;
    private RecyclerView messageRecyclerView;
    private RecyclerView.AdapterDataObserver dataObserver;
    private LinearLayoutManager linearLayoutManager;
    private ProgressBar progressBar;
    private EditText messageEditor;

    // Firebase instance variables
    private FirebaseRecyclerAdapter<Message, MessageViewHolder>
            mFirebaseAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if(!UserManager.getInstance().isUserLoggedin())
        {
            startActivity(new Intent(this, MainLoginScreen.class));
            finish();
            return;
        }


        // Initialize ProgressBar and RecyclerView.
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        messageRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageRecyclerView.setLayoutManager(linearLayoutManager);

        //Initialize the ChatManager
        ChatManager.getInstance().registerGroupChatByID("chatGroup1", this); //TODO: Register by project instead
        ChatManager.getInstance().registerChannel("-channel1", this); //TODO: Enter a default channel for all chats.

        UserManager.getInstance().getCurrentUser(new FetchUserCallback() {
            @Override
            public void onDataRetrieved(User result) {
                username = result.getUsername();
                registerMessageEditor();
                initializeSendButton();

            }
        });

    }

    public void registerContent() {
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Message,
                MessageViewHolder>(
                Message.class,
                R.layout.item_message,
                MessageViewHolder.class,
                ChatManager.getInstance().getChannelReference()) {

            @Override
            protected void populateViewHolder(MessageViewHolder viewHolder,
                                              Message friendlyMessage, int position) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.messageTextView.setText(friendlyMessage.getText());
                viewHolder.messengerTextView.setText(friendlyMessage.getName());
            }
        };

        registerFirebaseObserver();

        messageRecyclerView.setLayoutManager(linearLayoutManager);
        messageRecyclerView.setAdapter(mFirebaseAdapter);
    }

    private void registerFirebaseObserver() {

        dataObserver = new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int messageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        linearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (messageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    messageRecyclerView.scrollToPosition(positionStart);
                }
            }
        };

        mFirebaseAdapter.registerAdapterDataObserver(dataObserver);
    }

    private void registerMessageEditor() {
        messageEditor = (EditText) findViewById(R.id.messageEditText);
        messageEditor.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)}); //TODO: Extend limit
        messageEditor.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendButton.setEnabled(true);
                } else {
                    sendButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void initializeSendButton() {
        sendButton = (Button) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ChatManager.getInstance().writeMessage(username, messageEditor.getText().toString());
                messageEditor.setText("");
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(ChatManager.getInstance().getChannels() == null)
        {
            return false;
        }

        menu.clear();

        for(String channel: ChatManager.getInstance().getChannels().keySet())
        {
            menu.add(channel);
        }

        menu.add(R.string.create_channel);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getTitle().equals(getString(R.string.create_channel)))
        {
            CreateNewChannelDialog(new GetTextCallback() {
                @Override
                public void onInputComplete(String text) {
                    ChatManager.getInstance().createChannel(text);
                }
            });


        }
        else
        {
            String channelID = ChatManager.getInstance().getChannels().get(item.getTitle());
            ChatManager.getInstance().registerChannel(channelID, this);
        }

        return super.onOptionsItemSelected(item);
    }

    private void CreateNewChannelDialog(final GetTextCallback callback)
    {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Create a New Channel");

        final EditText input = new EditText(this);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});

        dialogBuilder.setView(input);

        dialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                callback.onInputComplete(input.getText().toString());
            }
        });
        
        dialogBuilder.show();
    }

}
