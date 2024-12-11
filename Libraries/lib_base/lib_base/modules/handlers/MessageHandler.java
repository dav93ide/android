package com.bodhitech.it.lib_base.lib_base.modules.handlers;

import android.os.Handler;
import android.os.Message;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class MessageHandler extends Handler {

    public interface IOnHandleMessage{
        // Message Whats
        int MSG_HANDSHAKE = 0x1;

        void onHandleMessage(Message msg);
    }

    private MessageManager mMessageManager;
    private IOnHandleMessage mCallback;
    private List<Message> mMessages;

    public MessageHandler(MessageManager manager, IOnHandleMessage callback){
        mMessageManager = manager;
        mCallback = callback;
        mMessages = new ArrayList<>();
    }

    public MessageHandler(IOnHandleMessage callback){
        mCallback = callback;
        mMessages = new ArrayList<>();
    }

    /** Getter & Setter Methods **/
    public List<Message> getMessages() {
        return mMessages;
    }

    /** Override Handler Methods **/
    @Override
    public void handleMessage(@NonNull Message msg){
        addMessage(msg);
        switch(msg.what){
            case IOnHandleMessage.MSG_HANDSHAKE:
                if(mMessageManager != null && mMessageManager.getMsgSender() == null){
                    if(msg.replyTo != null){
                        mMessageManager.setMsgSender(msg.replyTo);
                    }
                    mMessageManager.sendHandshake();
                }
                break;
            default:
                if(mCallback != null){
                    mCallback.onHandleMessage(msg);
                }
                break;
        }
    }

    /** Public Methods **/
    public Message getLastMessage(){
        return getMessage(mMessages.size() - 0x1);
    }

    public Message getMessage(int index){
        if(mMessages != null && mMessages.size() > 0x0){
            return mMessages.get(index);
        } else {
            return null;
        }
    }

    /** Private Methods **/
    private void addMessage(Message message) {
        mMessages.add(message);
    }

}
