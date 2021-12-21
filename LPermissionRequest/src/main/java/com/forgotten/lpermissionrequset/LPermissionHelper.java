package com.forgotten.lpermissionrequset;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName PermissionUtil
 * @Description 权限申请工具类
 * @Author Palmer
 * @Date 2021/12/21
 **/
public class LPermissionHelper {
    public static final String TAG = "LPermissionHelper";
    /**
     * 需要动态申请的权限列表
     */
    private final List<String> permissions = new ArrayList<>();

    /**
     * 宿主Activity
     */
    private Activity mContext;

    /**
     * 权限申请回调
     */
    private LPermissionCallback permissionCallback;

    /**
     * 额外配置
     */
    private LPermissionConfig checkConfig;

    public LPermissionHelper() {
    }

    public LPermissionHelper(Activity mContext) {
        this.mContext = mContext;
    }

    public static LPermissionHelper with(Activity mContext) {
        return new LPermissionHelper(mContext);
    }

    /**
     * 添加权限列表
     *
     * @param ps 权限列表
     * @return 当前类本身
     */
    public LPermissionHelper addPermissions(String... ps) {
        for (String permission : ps) {
            if (!permissions.contains(permission))
                permissions.add(permission);
            Log.d(LPermissionHelper.TAG, "添加权限：" + permission);

        }
        return this;
    }

    /**
     * 添加权限列表
     *
     * @param ps 权限列表
     * @return 当前类本身
     */
    public LPermissionHelper addPermissions(List<String> ps) {
        for (String permission : ps) {
            if (!permissions.contains(permission))
                permissions.add(permission);
            Log.d(LPermissionHelper.TAG, "添加权限：" + permission);

        }
        return this;
    }

    /**
     * 设置结果方法回调
     *
     * @param callback 回调接口
     * @return
     */
    public LPermissionHelper onResult(LPermissionCallback callback) {
        this.permissionCallback = callback;
        return this;
    }


    /**
     * 设置是否强制所有权限必须被允许 标志位
     *
     * @param forceAllPermissionsGranted
     */
    public LPermissionHelper setForceAllGranted(boolean forceAllPermissionsGranted) {
        if (null == checkConfig) {
            checkConfig = new LPermissionConfig();
        }
        checkConfig.setForceAllPermissionsGranted(forceAllPermissionsGranted);
        Log.d(LPermissionHelper.TAG, "是否强制请求所有权限：" + forceAllPermissionsGranted);

        return this;
    }

    /**
     * 设置被拒绝后的提示
     *
     * @param forceDeniedPermissionTips 提示内容
     */
    public LPermissionHelper setForceDeniedTips(String forceDeniedPermissionTips) {
        if (null == checkConfig) {
            checkConfig = new LPermissionConfig();
        }
        checkConfig.setForceDeniedPermissionTips(forceDeniedPermissionTips);
        Log.d(LPermissionHelper.TAG, "设置拒绝后提示内容：" + forceDeniedPermissionTips);
        return this;
    }

    public void check() {
        Log.d(TAG, "check: 开始申请权限");
        LPermissionFragment.newInstance(permissions.toArray(new String[permissions.size()]), checkConfig, permissionCallback).start(mContext);
    }

    /**
     * 获取App的名称
     *
     * @param context 环境上下文
     * @return app名称字符串
     */
    public static String getAppName(Context context) {
        // 获取包信息
        try {
            PackageManager pm = context.getPackageManager();
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), 0);
            // 获取应用 信息
            ApplicationInfo applicationInfo = packageInfo.applicationInfo;
            // 获取labelRes
            int labelRes = applicationInfo.labelRes;
            // 返回App的名称
            return context.getResources().getString(labelRes);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "本应用";
    }
}