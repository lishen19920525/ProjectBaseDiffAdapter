package com.example.demo;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.util.ArrayMap;

/**
 * Project:
 * Author: LiShen
 * Time: 2018/6/22 14:13
 * //TODO 完善权限种类，不支持多权限同时申请
 */
public class PermissionHandler {
    //    private static final int REQUEST_FOR_PERMISSIONS = 12000;
    private static final int REQUEST_FOR_LOCATION_PERMISSION = 12001;
    private static final int REQUEST_FOR_STORAGE_PERMISSION = 12002;
    private static final int REQUEST_FOR_CAMERA_PERMISSION = 12003;
    private static final int REQUEST_FOR_AUDIO_PERMISSION = 12004;

    private static volatile PermissionHandler instance;
    private ArrayMap<String, Object> callbackPool;

    private PermissionHandler() {
        callbackPool = new ArrayMap<>();
    }

    public static PermissionHandler get() {
        if (instance == null) {
            synchronized (PermissionHandler.class) {
                if (instance == null) {
                    instance = new PermissionHandler();
                }
            }
        }
        return instance;
    }

    public boolean check(Context context, Type permissionType) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }
        switch (permissionType) {
            case LOCATION:
                return ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
            case STORAGE:
                return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            case CAMERA:
                return ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
            case AUDIO:
                return ActivityCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
            default:
                return true;
        }
    }

    public void request(Activity activity, Type permissionType, Callback callback) {
        if (check(activity, permissionType)) {
            if (callback != null)
                callback.onResult(true);
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            switch (permissionType) {
                case LOCATION:
                    callbackPool.put(String.valueOf(activity.hashCode()) + REQUEST_FOR_LOCATION_PERMISSION, callback);
                    activity.requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION},
                            REQUEST_FOR_LOCATION_PERMISSION);
                    break;
                case STORAGE:
                    callbackPool.put(String.valueOf(activity.hashCode()) + REQUEST_FOR_STORAGE_PERMISSION, callback);
                    activity.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_FOR_STORAGE_PERMISSION);
                    break;
                case CAMERA:
                    callbackPool.put(String.valueOf(activity.hashCode()) + REQUEST_FOR_CAMERA_PERMISSION, callback);
                    activity.requestPermissions(new String[]{Manifest.permission.CAMERA},
                            REQUEST_FOR_CAMERA_PERMISSION);
                    break;
                case AUDIO:
                    callbackPool.put(String.valueOf(activity.hashCode()) + REQUEST_FOR_AUDIO_PERMISSION, callback);
                    activity.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},
                            REQUEST_FOR_AUDIO_PERMISSION);
                    break;
            }
        }
    }

    public void onRequestPermissionsResult(Activity activity, int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_FOR_LOCATION_PERMISSION:
            case REQUEST_FOR_STORAGE_PERMISSION:
            case REQUEST_FOR_CAMERA_PERMISSION:
            case REQUEST_FOR_AUDIO_PERMISSION:
                Callback callback = (Callback) callbackPool.get(String.valueOf(activity.hashCode()) + requestCode);
                if (callback != null) {
                    callback.onResult(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED);
                    callbackPool.remove(String.valueOf(activity.hashCode()) + requestCode);
                }
                break;
        }
    }

    public enum Type {
        LOCATION,
        STORAGE,
        CAMERA,
        AUDIO
    }

    public interface Callback {
        void onResult(boolean grant);
    }
}