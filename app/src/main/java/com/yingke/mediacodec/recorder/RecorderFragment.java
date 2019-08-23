package com.yingke.mediacodec.recorder;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.yingke.mediacodec.R;
import com.yingke.mediacodec.player.PlayerLog;
import com.yingke.mediacodec.recorder.encoder.MediaAudioEncoder;
import com.yingke.mediacodec.recorder.encoder.MediaEncoder;
import com.yingke.mediacodec.recorder.encoder.MediaMuxerManager;
import com.yingke.mediacodec.recorder.encoder.MediaVideoEncoder;
import com.yingke.mediacodec.recorder.glsurface.MediaCodecRecordGlSurfaceView;

import java.io.File;
import java.io.IOException;

/**
 * 功能：
 * </p>
 * <p>Copyright corp.netease.com 2018 All right reserved </p>
 *
 * @author tuke 时间 2019/8/10
 * @email tuke@corp.netease.com
 * <p>
 * 最后修改人：无
 * <p>
 */
public class RecorderFragment extends Fragment {

    private static final String TAG = RecorderFragment.class.getSimpleName();

    private View mRootView;
    private MediaCodecRecordGlSurfaceView mSurfaceView;
    private ImageView mStartRecordBtn;
    private ImageView mStopRecordBtn;

    private MediaMuxerManager mMediaMuxerManager;

    private String outputPath = "";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if (mRootView == null) {
            mRootView = inflater.inflate(R.layout.frag_video_recorder, null);
            initViews();
        }

        return mRootView;
    }


    private void initViews() {
        mSurfaceView = mRootView.findViewById(R.id.camera_gl_view);
        mStartRecordBtn = mRootView.findViewById(R.id.camera_start_record);
        mStopRecordBtn = mRootView.findViewById(R.id.camera_stop_record);

        mStartRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLog.e(TAG, "StartRecordBtn click");
                outputPath = "";
                startRecording();
                mStartRecordBtn.setVisibility(View.GONE);
                mStopRecordBtn.setVisibility(View.VISIBLE);
            }
        });

        mStopRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PlayerLog.e(TAG, "StopRecordBtn click");
                // 刷新相册数据库
                outputPath = mMediaMuxerManager.getOutputPath();
                if (!TextUtils.isEmpty(outputPath)) {
                    getContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(outputPath))));

                }
                stopRecording();
                mStopRecordBtn.setVisibility(View.GONE);
                mStartRecordBtn.setVisibility(View.VISIBLE);

            }
        });
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mSurfaceView != null) {
            mSurfaceView.onResume();
        }

    }

    /**
     * 开始录制，这里放在了主线程运行(实际应该放在异步线程中运行)
     */
    private void startRecording() {

        try {

            // if you record audio only, ".m4a" is also OK.
            mMediaMuxerManager = new MediaMuxerManager(".mp4");
            // 开始视频录制
            new MediaVideoEncoder(mMediaMuxerManager, mMediaEncoderListener, 720, 1280);
            // 开启音频录制
            new MediaAudioEncoder(mMediaMuxerManager, mMediaEncoderListener);
            // 视频，音频 录制初始化
            mMediaMuxerManager.prepare();
            // 视频，音频 开始录制
            mMediaMuxerManager.startRecording();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始录制
     */
    private void stopRecording() {
        if (mMediaMuxerManager != null) {
            mMediaMuxerManager.stopRecording();
            mMediaMuxerManager = null;
        }
    }

    /**
     * @return 录制输出路径
     */
    public String getOutputPath() {
        return outputPath;
    }




    @Override
    public void onPause() {
        super.onPause();
        stopRecording();
        if (mSurfaceView != null) {
            mSurfaceView.onPause();
        }

    }

    /**
     * 视频、音频 开始与结束录制的回调
     */
    private final MediaEncoder.MediaEncoderListener mMediaEncoderListener = new MediaEncoder.MediaEncoderListener() {

        @Override
        public void onPrepared(final MediaEncoder encoder) {
            // 开始录制视频
            if (encoder instanceof MediaVideoEncoder) {
                mSurfaceView.setVideoEncoder((MediaVideoEncoder) encoder);
            }
        }

        /**
         * @param encoder
         */
        @Override
        public void onStopped(final MediaEncoder encoder) {
            // 结束录制视频
            if (encoder instanceof MediaVideoEncoder) {
                mSurfaceView.setVideoEncoder(null);
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
