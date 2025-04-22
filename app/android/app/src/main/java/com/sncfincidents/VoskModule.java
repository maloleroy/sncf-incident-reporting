package com.sncfincidents;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Looper;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Promise;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import org.vosk.Model;
import org.vosk.Recognizer;
import org.vosk.android.StorageService;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VoskModule extends ReactContextBaseJavaModule {
    private static final String TAG = "VoskModule";
    private static final int SAMPLE_RATE = 16000;
    private static final int BUFFER_SIZE = 4096;

    private final ReactApplicationContext reactContext;
    private Model model;
    private Recognizer recognizer;
    private AudioRecord audioRecord;
    private ExecutorService executorService;
    private boolean isRecording = false;

    public VoskModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @Override
    public String getName() {
        return "VoskModule";
    }

    @ReactMethod
    public void initialize(String modelName, Promise promise) {
        try {
            String modelPath = StorageService.unpack(reactContext, modelName, "model");
            model = new Model(modelPath);
            recognizer = new Recognizer(model, SAMPLE_RATE);
            executorService = Executors.newSingleThreadExecutor();
            promise.resolve(null);
        } catch (IOException e) {
            promise.reject("INIT_ERROR", "Failed to initialize Vosk: " + e.getMessage());
        }
    }

    @ReactMethod
    public void startRecording() {
        if (isRecording) return;

        int bufferSize = AudioRecord.getMinBufferSize(
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT
        );

        audioRecord = new AudioRecord(
            MediaRecorder.AudioSource.MIC,
            SAMPLE_RATE,
            AudioFormat.CHANNEL_IN_MONO,
            AudioFormat.ENCODING_PCM_16BIT,
            bufferSize
        );

        audioRecord.startRecording();
        isRecording = true;

        executorService.execute(() -> {
            byte[] buffer = new byte[BUFFER_SIZE];
            while (isRecording) {
                int nread = audioRecord.read(buffer, 0, buffer.length);
                if (nread > 0) {
                    if (recognizer.acceptWaveForm(buffer, nread)) {
                        String result = recognizer.getResult();
                        sendEvent("onResult", result);
                    }
                }
            }
        });
    }

    @ReactMethod
    public void stopRecording() {
        if (!isRecording) return;

        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }

        sendEvent("onRecordingStopped", null);
    }

    @ReactMethod
    public void cleanup() {
        stopRecording();
        if (recognizer != null) {
            recognizer.close();
            recognizer = null;
        }
        if (model != null) {
            model.close();
            model = null;
        }
        if (executorService != null) {
            executorService.shutdown();
            executorService = null;
        }
    }

    private void sendEvent(String eventName, String result) {
        WritableMap params = Arguments.createMap();
        if (result != null) {
            params.putString("text", result);
        }
        reactContext
            .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
            .emit(eventName, params);
    }
} 