package com.guichaguri.trackplayer.casting;

import com.facebook.react.bridge.Promise;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.MediaMetadata;
import com.google.android.gms.cast.MediaQueueItem;
import com.google.android.gms.cast.MediaTrack;
import com.google.android.gms.cast.framework.media.RemoteMediaClient.MediaChannelResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.images.WebImage;
import com.guichaguri.trackplayer.service.models.Track;
import java.util.Collections;

/**
 * @author Guichaguri
 */
public class CastUtils {

    public static ResultCallback<MediaChannelResult> toResultCallback(Promise promise) {
        return mediaChannelResult -> {
            //TODO convert custom data
            promise.resolve(mediaChannelResult.getCustomData());
        };
    }

    public static MediaQueueItem toMediaQueueItem(Track track) {
        MediaMetadata metadata = new MediaMetadata();
        metadata.addImage(new WebImage(track.artwork));
        metadata.putString(MediaMetadata.KEY_TITLE, track.title);
        metadata.putString(MediaMetadata.KEY_ARTIST, track.artist);
        metadata.putString(MediaMetadata.KEY_ALBUM_TITLE, track.album);

        MediaTrack castTrack = new MediaTrack.Builder(track.queueId, MediaTrack.TYPE_AUDIO)
                .setContentId(track.id)
                .setContentType("") //TODO
                .setName(track.title)
                .build();

        MediaInfo info = new MediaInfo.Builder(track.id)
                .setStreamType(MediaInfo.STREAM_TYPE_NONE) // TODO
                .setContentType("") // TODO
                .setMetadata(metadata)
                .setStreamDuration(track.duration)
                .setMediaTracks(Collections.singletonList(castTrack))
                .build();

        return new MediaQueueItem.Builder(info)
                .setPlaybackDuration(track.duration)
                .build();
    }

}
