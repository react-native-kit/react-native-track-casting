package com.guichaguri.trackplayer.casting.playback;

import android.content.Context;
import com.facebook.react.bridge.Promise;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.ext.cast.CastPlayer;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.framework.CastContext;
import com.google.android.gms.cast.framework.CastSession;
import com.google.android.gms.cast.framework.SessionManager;
import com.guichaguri.trackplayer.casting.CastUtils;
import com.guichaguri.trackplayer.service.MusicManager;
import com.guichaguri.trackplayer.service.models.Track;
import com.guichaguri.trackplayer.service.player.ExoPlayback;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Guichaguri
 */
public class CastPlayback extends ExoPlayback<CastPlayer> {

    private final CastContext castContext;

    public CastPlayback(Context context, MusicManager manager, CastContext castContext, CastPlayer player) {
        super(context, manager, player);
        this.castContext = castContext;
    }

    @Override
    public void add(Track track, int index, Promise promise) {
        queue.add(index, track);
        player.addItems(index, CastUtils.toMediaQueueItem(track))
                .setResultCallback(CastUtils.toResultCallback(promise));
    }

    @Override
    public void add(Collection<Track> tracks, int index, Promise promise) {
        MediaQueueItem[] trackList = new MediaQueueItem[tracks.size()];
        int i = 0;

        for(Track track : tracks) {
            trackList[i] = CastUtils.toMediaQueueItem(track);
            i++;
        }

        queue.addAll(index, tracks);
        player.addItems(index, trackList).setResultCallback(CastUtils.toResultCallback(promise));
    }

    @Override
    public void remove(List<Integer> indexes, Promise promise) {
        Collections.sort(indexes);

        for(int i = indexes.size() - 1; i >= 0; i--) {
            int index = indexes.get(i);

            queue.remove(index);

            if(i == 0) {
                player.removeItem(index).setResultCallback(CastUtils.toResultCallback(promise));
            } else {
                player.removeItem(index);
            }
        }
    }

    @Override
    public void removeUpcomingTracks() {
        int currentIndex = player.getCurrentWindowIndex();
        if(currentIndex == C.INDEX_UNSET) return;

        for (int i = queue.size() - 1; i > currentIndex; i--) {
            queue.remove(i);
            player.removeItem(i);
        }
    }

    @Override
    public float getVolume() {
        try {
            SessionManager manager = castContext.getSessionManager();
            CastSession session = manager.getCurrentCastSession();
            return session == null ? 1 : (float)session.getVolume();
        } catch(Exception ex) {
            return 1;
        }
    }

    @Override
    public void setVolume(float volume) {
        try {
            SessionManager manager = castContext.getSessionManager();
            CastSession session = manager.getCurrentCastSession();
            if(session != null) session.setVolume((double)volume);
        } catch(Exception ex) {
            // Ignore
        }
    }

}
