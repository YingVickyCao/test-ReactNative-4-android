package com.test.v3;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.ReactRootView;
import com.facebook.react.common.LifecycleState;
import com.facebook.react.modules.core.DefaultHardwareBackBtnHandler;
import com.facebook.react.shell.MainReactPackage;
import com.test.BuildConfig;
import com.test._native_modules.ToastPackage;

public class ReactFragment extends Fragment implements DefaultHardwareBackBtnHandler, IOnKeyUp,IBackPressed {
    public static final String TAG = ReactFragment.class.getSimpleName();

    private final int OVERLAY_PERMISSION_REQ_CODE = 1;

    private ReactRootView mReactRootView;

    /**
     * ReactInstanceManager:
     * 1) 一个ReactInstanceManager可以在多个 activities 或 fragments 间共享。
     * 2) 传递 activity 生命周期回调 给ReactInstanceManager
     */
    private ReactInstanceManager mReactInstanceManager;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainActivity) {
            ((MainActivity) context).setOnKeyUp(this);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Context context = getActivity();
        if (context instanceof MainActivity) {
            ((MainActivity) context).setOnKeyUp(null);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getActivity())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getActivity().getPackageName()));
                startActivityForResult(intent, OVERLAY_PERMISSION_REQ_CODE);
            }
        }

        mReactRootView = new ReactRootView(getActivity());
        mReactInstanceManager = ReactInstanceManager.builder()
                .setApplication(getActivity().getApplication())
                .setCurrentActivity(getActivity())
                .setBundleAssetName("index.android.bundle")
                .setJSMainModulePath("index")
                .addPackage(new MainReactPackage())
                .addPackage(new ToastPackage())
                .setUseDeveloperSupport(BuildConfig.DEBUG)
                .setInitialLifecycleState(LifecycleState.RESUMED)
                .build();
        // 注意这里的MyReactNativeApp必须对应“index.js”中的 AppRegistry.registerComponent()”的第一个参数
        mReactRootView.startReactApplication(mReactInstanceManager, "test", null);

        mReactRootView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_UP) {
                    onBackPressed();
                    return true;
                }
                return false;
            }
        });
        return mReactRootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == OVERLAY_PERMISSION_REQ_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (!Settings.canDrawOverlays(getActivity())) {
                    // SYSTEM_ALERT_WINDOW permission not granted
                }
            }
        }

        // Integrating Native Modules which use startActivityForResult
        mReactInstanceManager.onActivityResult(getActivity(), requestCode, resultCode, data);
    }

    // 把后退按钮事件传递给 React Native
    @Override
    public void onBackPressed() {
        if (mReactInstanceManager != null) {
            Log.d(TAG, "onBackPressed: ");
            mReactInstanceManager.onBackPressed();
        } else {
            getActivity().onBackPressed();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        // Android studio Emulator, Ctrl+M ->  dev menu
        Log.d(TAG, "onKeyUp: ");
        if (keyCode == KeyEvent.KEYCODE_MENU && null != mReactInstanceManager) {
            Log.d(TAG, "onKeyUp: Menu");
            mReactInstanceManager.showDevOptionsDialog();
            return true;
        }
        return false;
    }

    // When JavaScript doesn't handle the back button press, invokeDefaultOnBackPressed method will be called. By default this simply finishes Activity.
    @Override
    public void invokeDefaultOnBackPressed() {
        getActivity().onBackPressed();
    }

//    @Override
//    public void onPointerCaptureChanged(boolean hasCapture) {
//
//    }


    @Override
    public void onPause() {
        super.onPause();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostPause(getActivity());
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostResume(getActivity(), this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mReactInstanceManager != null) {
            mReactInstanceManager.onHostDestroy(getActivity());
        }
        if (mReactRootView != null) {
            mReactRootView.unmountReactApplication();
        }
    }
}