package com.guichaguri.trackplayer.casting;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.gms.cast.framework.CastContext;
import com.guichaguri.trackplayer.casting.playback.CastPlayback;
import com.guichaguri.trackplayer.service.MusicBinder;
import com.guichaguri.trackplayer.service.MusicManager;
import com.guichaguri.trackplayer.service.MusicService;
import com.guichaguri.trackplayer.service.Utils;
import java.util.ArrayDeque;

public class CastModule extends ReactContextBaseJavaModule implements ServiceConnection {

    private MusicBinder binder;
    private ArrayDeque<Runnable> initCallbacks = new ArrayDeque<>();
    private boolean connecting = false;

    public CastModule(ReactApplicationContext context) {
        super(context);
    }

    @Override
    public String getName() {
        return "TrackCasting";
    }

    @Override
    public void onCatalystInstanceDestroy() {
        ReactContext context = getReactApplicationContext();
        if(context != null) context.unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        binder = (MusicBinder)service;
        connecting = false;

        // Triggers all callbacks
        while(!initCallbacks.isEmpty()) {
            binder.post(initCallbacks.remove());
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName name) {
        binder = null;
        connecting = false;
    }

    /**
     * Waits for a connection to the service and/or runs the {@link Runnable} in the player thread
     */
    private void waitForConnection(Runnable r) {
        if(binder != null) {
            binder.post(r);
            return;
        } else {
            initCallbacks.add(r);
        }

        if(connecting) return;

        ReactApplicationContext context = getReactApplicationContext();

        // Binds the service to get a MediaWrapper instance
        Intent intent = new Intent(context, MusicService.class);
        context.startService(intent);
        intent.setAction(Utils.CONNECT_INTENT);
        context.bindService(intent, this, 0);

        connecting = true;
    }

    @ReactMethod
    public void setOptions(ReadableMap map) {
        CastContext context = CastContext.getSharedInstance(getReactApplicationContext());
        context.setReceiverApplicationId(map.getString("applicationId"));
    }

    public void createPlayer() {
        waitForConnection(() -> {
            Context context = getReactApplicationContext();
            CastContext castContext = CastContext.getSharedInstance(context);
            CastPlayer player = new CastPlayer(castContext);

            MusicManager manager = binder.getManager();
            CastPlayback playback = new CastPlayback(context, manager, castContext, player);
            manager.switchPlayback(playback);
        });
    }

}