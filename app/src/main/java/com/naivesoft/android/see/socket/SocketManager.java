package com.naivesoft.android.see.socket;

import android.util.Log;
import android.util.Pair;

import java.net.URISyntaxException;
import java.util.concurrent.LinkedBlockingQueue;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by admin on 2017/11/11.
 */

public class SocketManager {

    private final static String SERVER_URL = "http://192.168.3.8:8081";
    private static SocketManager sInstance = new SocketManager();
    private LinkedBlockingQueue<Pair<String, Object[]>> mSendDataQueue = new LinkedBlockingQueue<>();
    private Object mConnectLock = new Object();
    private Emitter.Listener mConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v("HomeActivity", "socket connect ");
            synchronized (mConnectLock) {
                mConnectLock.notifyAll();
            }
        }
    };
    private Emitter.Listener mDisConnectListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.v("HomeActivity", "socket disconnect ");
        }
    };
    private Socket mSocket;
    private Thread mSendDataThread = new Thread(new Runnable() {
        @Override
        public void run() {
            while (true) {
                try {
                    Pair<String, Object[]> data = mSendDataQueue.take();
                    if (!isConnected()) {
                        synchronized (mConnectLock) {
                            mConnectLock.wait();
                        }
                    }
                    mSocket.emit(data.first, data.second);
                } catch (IllegalMonitorStateException e) {
                    break;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    });

    {
        try {
            mSocket = IO.socket(SERVER_URL);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private SocketManager() {
    }

    public static SocketManager getInstance() {
        return sInstance;
    }

    public String getSocketId() {
        return mSocket.id();
    }

    public void connect() {
        mSocket.connect();
        listenEvent("connect", mConnectListener);
        listenEvent("disconnect", mDisConnectListener);
        if (!mSendDataThread.isAlive()) {
            mSendDataThread.start();
        }
    }

    public boolean isConnected() {
        return mSocket.connected();
    }

    public void sendData(String eventName, Object... data) {
        try {
            mSendDataQueue.put(new Pair<>(eventName, data));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void listenEvent(String eventName, Emitter.Listener listener) {
        mSocket.on(eventName, listener);
    }

    public void disConnect() {
        mSocket.disconnect();
    }

}
