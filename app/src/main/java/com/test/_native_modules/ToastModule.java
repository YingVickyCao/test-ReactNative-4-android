package com.test._native_modules;

import android.util.Log;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ToastModule extends ReactContextBaseJavaModule {
    private static final String TAG = "NativeToast";

    private final static String KEY_TIME = "TIME";
    private int num;

    public ToastModule(@Nonnull ReactApplicationContext reactContext) {
        super(reactContext);
    }

    @Nonnull
    @Override
    public String getName() {  // name will represent this class in JS
        return TAG;
    }

    @Nullable
    @Override
    public Map<String, Object> getConstants() {// Option: key pre-defines values for js -> java
        final Map<String, Object> constants = new HashMap<>();
        constants.put(KEY_TIME, ++num);
        return constants;
    }

    @ReactMethod
    public void show(String time){ // js -> java
        Log.d(TAG, "show: "+time);
        Toast.makeText(getReactApplicationContext(), time, Toast.LENGTH_SHORT).show();
    }
}
