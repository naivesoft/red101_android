package com.naivesoft.android.see.socket;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.amap.api.maps.model.LatLng;
import com.naivesoft.android.see.mapblock.MapBlockHelper;
import com.naivesoft.android.see.mapblock.MapBlockMessageUtil;
import com.naivesoft.android.see.model.UserPositionInfo;
import com.naivesoft.android.see.protobuf.PositionChange;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import io.socket.emitter.Emitter;

/**
 * Created by admin on 2017/11/11.
 */

public class RealTimePositionSocket {

    private final static String SOCKET_CLIENT_REPORT_POSITION = "client_report_position";
    private final static String SOCKET_CLIENT_REPORT_LOOK_BLOCK_AND_GET_KISSERS = "client_report_look_block_and_get_kissers";
    private final static String SOCKET_SERVER_NOTIFY_KISSERS = "server_notify_kissers";

    private Context mContext;
    private SocketManager mSocketManager;
    private RealTimePositionChangeListener mListener;
    private Emitter.Listener mNotifyKissersListener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            try {
                PositionChange.PositionChangedResponse positionChangedResponse = PositionChange.PositionChangedResponse.parseFrom((byte[]) args[0]);
                if (positionChangedResponse != null) {
                    Log.v("HomeActivity", "server_notify_kissers, count: " + positionChangedResponse.getBlockListDataCount());
                    if (positionChangedResponse.getBlockListDataCount() > 0) {
                        List<UserPositionInfo> userPositionInfos = new ArrayList<>();
                        for (PositionChange.PositionChangedResponse.ActivePointInMap activePointInMap : positionChangedResponse.getBlockListDataList()) {
                            userPositionInfos.add(MapBlockMessageUtil.getUserPositionModel(activePointInMap.getUserIdWithPosition()));
                        }
                        if (mListener != null) {
                            mListener.onKissersPositionChange(userPositionInfos);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public RealTimePositionSocket(Context context, SocketManager socketManager, RealTimePositionChangeListener listener) {
        mContext = context;
        mSocketManager = socketManager;
        mListener = listener;

        mSocketManager.listenEvent(SOCKET_SERVER_NOTIFY_KISSERS, mNotifyKissersListener);
    }


    public boolean sendClientPosition(LatLng currentPosition) {

        if (currentPosition == null) {
            return false;
        }
        long lon_long = Math.round(currentPosition.longitude * Math.pow(10, 6));
        long lat_long = Math.round(currentPosition.latitude * Math.pow(10, 6));
        Set<String> currentBlockList = MapBlockHelper.getInBlockList(lon_long, lat_long);

        try {
            if (mSocketManager == null || TextUtils.isEmpty(mSocketManager.getSocketId())) {
                return false;
            }
            if (TextUtils.isEmpty(ClientInfo.getInstance(mContext).getUserId())) {
                return false;
            }
            PositionChange.PositionChangedRequest positionChangedRequest = PositionChange.PositionChangedRequest.newBuilder()
                    .setSocketId(mSocketManager.getSocketId())
                    .setUserId(ClientInfo.getInstance(mContext).getUserId())
                    .setUserIdWithPosition(MapBlockMessageUtil.getUserPositionData(ClientInfo.getInstance(mContext).getUserId(), currentPosition.longitude, currentPosition.latitude, "online"))
                    .setDeviceId(ClientInfo.getInstance(mContext).getDeviceId())
                    .setLongitude(currentPosition.longitude)
                    .setLatitude(currentPosition.latitude)
                    .addAllCurrentBlock(currentBlockList)
                    .build();

            mSocketManager.sendData(SOCKET_CLIENT_REPORT_POSITION, positionChangedRequest.toByteArray());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean sendLookBlockListAndGetKissers(Set<String> lookBlockList) {

        try {
            if (mSocketManager == null || TextUtils.isEmpty(mSocketManager.getSocketId())) {
                return false;
            }
            if (TextUtils.isEmpty(ClientInfo.getInstance(mContext).getUserId())) {
                return false;
            }
            PositionChange.PositionChangedRequest positionChangedRequest = PositionChange.PositionChangedRequest.newBuilder()
                    .setSocketId(mSocketManager.getSocketId())
                    .setUserId(ClientInfo.getInstance(mContext).getUserId())
                    .setDeviceId(ClientInfo.getInstance(mContext).getDeviceId())
                    .addAllLookBlock(lookBlockList)
                    .build();

            mSocketManager.sendData(SOCKET_CLIENT_REPORT_LOOK_BLOCK_AND_GET_KISSERS, positionChangedRequest.toByteArray());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public interface RealTimePositionChangeListener {
        void onKissersPositionChange(List<UserPositionInfo> userPositionInfos);
    }
}
