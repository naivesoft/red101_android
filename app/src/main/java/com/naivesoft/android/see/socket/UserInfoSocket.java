package com.naivesoft.android.see.socket;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.naivesoft.android.see.protobuf.UserInfo;

import io.socket.emitter.Emitter;

/**
 * Created by admin on 2017/11/11.
 */

public class UserInfoSocket implements Emitter.Listener {

    private final static String SOCKET_CLIENT_REPORT_USER_ACTIVE = "client_report_user_active";
    private final static String SOCKET_USER_ACTIVE_RESPONSE = "server_user_active_response";

    private Context mContext;
    private SocketManager mSocketManager;
    private ActiveACKListener mActiveACKListener;

    public UserInfoSocket(Context context, SocketManager socketManager) {
        mContext = context;
        mSocketManager = socketManager;

        mSocketManager.listenEvent(SOCKET_USER_ACTIVE_RESPONSE, this);
    }

    public boolean reportUserActive() {
        try {
            if (mSocketManager == null || TextUtils.isEmpty(mSocketManager.getSocketId())) {
                return false;
            }
            ClientInfo clientInfo = ClientInfo.getInstance(mContext);
            UserInfo.UserInfoRequest userInfoRequest = UserInfo.UserInfoRequest.newBuilder()
                    .setUserId(clientInfo.getUserId())
                    .setDeviceId(clientInfo.getDeviceId())
                    .setSocketId(mSocketManager.getSocketId())
                    .build();

            mSocketManager.sendData(SOCKET_CLIENT_REPORT_USER_ACTIVE, userInfoRequest.toByteArray());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public void setOnActiveACKListener(ActiveACKListener listener) {
        mActiveACKListener = listener;
    }

    @Override
    public void call(Object... args) {
        try {
            UserInfo.UserInfoResponse userInfoResponse = UserInfo.UserInfoResponse.parseFrom((byte[]) args[0]);
            if (userInfoResponse != null) {
                Log.v("USER_ACTIVE_RESPONSE", userInfoResponse.toString());
                ClientInfo clientInfo = ClientInfo.getInstance(mContext);
                if (!clientInfo.getUserId().equals(userInfoResponse.getUserId())) {
                    clientInfo.setUserId(userInfoResponse.getUserId());
                }
                if (mActiveACKListener != null) {
                    mActiveACKListener.onActive();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface ActiveACKListener {
        void onActive();
    }
}
