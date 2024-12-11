package com.bodhitech.it.lib_base.lib_base.modules.handlers;

import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;

import com.bodhitech.it.lib_base.lib_base.BaseEnvironment;

import java.util.ArrayList;
import java.util.List;

public class MessageManager {

    private static final String TAG = MessageManager.class.getSimpleName();

    private Messenger mMsgSender;
    private Messenger mMsgReceiver;
    private List<Message> mMessages;

    public MessageManager(MessageHandler.IOnHandleMessage callback, IBinder target){
        mMsgReceiver = new Messenger(new MessageHandler(this, callback));
        mMsgSender = new Messenger(target);
        mMessages = new ArrayList<>();
    }

    public MessageManager(MessageHandler.IOnHandleMessage callback){
        mMsgReceiver = new Messenger(new MessageHandler(this, callback));
        mMsgSender = null;
        mMessages = new ArrayList<>();
    }

    /** Getter & Setter Methods **/
    public Messenger getMsgSender() {
        return mMsgSender;
    }

    public void setMsgSender(Messenger sender) {
        this.mMsgSender = sender;
    }

    public Messenger getMsgReceiver() {
        return mMsgReceiver;
    }

    public void setMsgReceiver(Messenger receiver) {
        this.mMsgReceiver = receiver;
    }

    public List<Message> getLastMessages() {
        return mMessages;
    }

    public void addMessage(Message message) {
        this.mMessages.add(message);
    }

    /** Public Methods **/
    public void sendMessage(int what, int arg1, int arg2, Bundle msgData){
        if(mMsgSender != null && mMsgReceiver != null) {
            try {
                Message msg = Message.obtain(null, what, arg1, arg2);
                msg.replyTo = mMsgReceiver;
                if(msgData != null){
                    msg.setData(msgData);
                }
                mMsgSender.send(msg);
            } catch (RemoteException rE) {
                BaseEnvironment.onExceptionLevelLow(TAG, rE);
            }
        }
    }

    public void sendHandshake(){
        if(mMsgSender != null && mMsgReceiver != null){
            sendMessage(MessageHandler.IOnHandleMessage.MSG_HANDSHAKE, 0x0, 0x0, null);
        }
    }

    /** Public Static Methods **/
    public static Message getMessage(Handler target, int what, int arg1, int arg2, Bundle msgData){
        Message msg = Message.obtain(target, what, arg1, arg2);
        if(msgData != null){
            msg.setData(msgData);
        }
        return msg;
    }

}
