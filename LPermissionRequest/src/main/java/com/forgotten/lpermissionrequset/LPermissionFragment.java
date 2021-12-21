package com.forgotten.lpermissionrequset;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName LPermissionFragment
 * @Description 用
 * @Author Palmer
 * @Date 2021/12/21
 **/
public class LPermissionFragment extends Fragment {

    public static final int PERMISSION_REQUEST_CODE = 1001;
    public static final int REQUEST_PERMISSION_SETTING = 1002;
    private static final String PERMISSIONS_KEY_NAME = "permissions";
    private static final String PERMISSIONS_CONFIG_KEY_NAME = "checkConfig";
    /**
     * 父Activity的环境上下文
     */
    private Activity mContext;


    /**
     * 需要申请的权限列表
     */
    private String[] mPermissions = null;

    /**
     * 权限检查配置
     */
    private LPermissionConfig checkConfig;

    /**
     * 强制禁止权限时提示
     */
    private String forceDeniedPermissionTips = "";
    /**
     * 强制禁止权限时展示对话框
     */
    private AlertDialog mPermissionDialog;
    /**
     * 权限申请回调
     */
    private LPermissionCallback permissionCallback;

    /**
     * fragment创建新实例
     *
     * @param permissions 要申请的权限列表
     * @param config      配置设置
     * @param callback    回调方法
     * @return 创建好的fragment实例
     */
    public static LPermissionFragment newInstance(String[] permissions, LPermissionConfig config, LPermissionCallback callback) {
        // 设置传递参数
        Bundle args = new Bundle();
        args.putStringArray(PERMISSIONS_KEY_NAME, permissions);
        args.putParcelable(PERMISSIONS_CONFIG_KEY_NAME, config);
        // 新建一个fragment对象并设置参数
        LPermissionFragment fragment = new LPermissionFragment();
        fragment.setArguments(args);
        // 设置回调接口
        fragment.setPermissionCallback(callback);
        return fragment;
    }

    public void setPermissionCallback(LPermissionCallback permissionCallback) {
        this.permissionCallback = permissionCallback;
    }

    public void start(Activity activity) {
        if (activity != null) {
            mContext = activity;
            // 判断主线程looper是否等于当前looper
            if (Looper.getMainLooper() != Looper.myLooper()) {
                Log.d(LPermissionHelper.TAG, "失败：Looper.getMainLooper() != Looper.myLooper()");
                return;
            }

            // 将该fragment添加到宿主activity中
            activity.getFragmentManager().beginTransaction().add(this, activity.getClass().getName() + "Permission").commit();
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        forceDeniedPermissionTips = "已禁用相关权限，请前往设置->应用->【" + LPermissionHelper.getAppName(mContext) + "】->权限中打开相关权限";

        Bundle arguments = getArguments();
        if (arguments != null) {
            mPermissions = arguments.getStringArray(PERMISSIONS_KEY_NAME);
            checkConfig = arguments.getParcelable(PERMISSIONS_CONFIG_KEY_NAME);
        }

        // 当系统版本达到6.0及以上时，才申请权限
        if ((Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)) {
            requestPermission();
        } else {
            // 低于6.0不需要动态申请权限，直接回调成功方法
            requestSuccess();
        }
    }


    private void requestSuccess() {
        Log.d(LPermissionHelper.TAG, "requestSuccess: 申请权限成功");
        if (permissionCallback != null) {
            permissionCallback.onRequestSuccess();
        }
        mContext.getFragmentManager().beginTransaction().remove(this).commit();
    }

    private void requestFailed(String[] grantedPermissions, String[] deniedPermissions, String[] forceDeniedPermissions) {
        Log.d(LPermissionHelper.TAG, "requestFailed: 申请权限失败");
        if (permissionCallback != null) {
            permissionCallback.onRequestFailed(grantedPermissions, deniedPermissions, forceDeniedPermissions);
        }
        mContext.getFragmentManager().beginTransaction().remove(this).commit();
    }


    /**
     * 获取权限
     */
    @TargetApi(Build.VERSION_CODES.M)
    public void requestPermission() {
        //记录未授权的权限
        List<String> deniedPermissions = new ArrayList<>();
        for (String permission : mPermissions) {
            int check = ContextCompat.checkSelfPermission(getActivity(), permission);
            if (check == PackageManager.PERMISSION_GRANTED) {
                /*授权通过 do nothing*/
            } else {
                // 授权还未通过，保存到未授权数组
                deniedPermissions.add(permission);
            }
        }
        if (deniedPermissions.size() != 0) {
            // 有权限没有通过，将未授权权限列表 进行请求授权
            this.requestPermissions(deniedPermissions.toArray(new String[deniedPermissions.size()]), PERMISSION_REQUEST_CODE);
        } else {
            // 授权全部通过
            requestSuccess();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 如果请求码等于权限请求码
        if (requestCode == PERMISSION_REQUEST_CODE) {
            // 记录 拒绝并且不再提醒的权限
            List<String> forceDeniedPermissions = new ArrayList<>();
            // 记录 拒绝的权限
            List<String> normalDeniedPermissions = new ArrayList<>();
            // 已同意的权限
            List<String> grantedPermissions = new ArrayList<>();
            for (int i = 0; i < grantResults.length; i++) {
                int grantResult = grantResults[i];
                String permission = permissions[i];
                if (grantResult == PackageManager.PERMISSION_GRANTED) {
                    // 授权通过的权限 nothing to do
                    grantedPermissions.add(permission);
                    Log.d(LPermissionHelper.TAG, permission + " 权限已通过");
                } else {
                    // 被拒绝的权限
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(mContext, permission)) {
                        // 不再提醒并且拒绝的权限
                        forceDeniedPermissions.add(permission);
                        Log.d(LPermissionHelper.TAG, permission + " 权限已被强制拒绝");
                    } else {
                        normalDeniedPermissions.add(permission);
                        Log.d(LPermissionHelper.TAG, permission + " 权限已被拒绝");

                    }
                }
            }

            if (forceDeniedPermissions.size() == 0 && normalDeniedPermissions.size() == 0) {
                // 权限全部授权通过
                requestSuccess();
            } else {
                // 权限部分授权通过 如果用户希望一直提示授权直到给权限为止 那么就一直去请求权限
                if (checkConfig != null && checkConfig.isForceAllPermissionsGranted()) {
                    if (normalDeniedPermissions.size() != 0) {
                        // 如果还有普通拒绝的权限，继续申请权限
                        requestPermission();
                    } else {
                        // 展示打开设置页对话框
                        showSystemPermissionsSettingDialog(grantedPermissions, normalDeniedPermissions, forceDeniedPermissions);
                    }
                } else {
                    for (String permission : this.mPermissions) {
                        if (grantedPermissions.contains(permission)
                                || normalDeniedPermissions.contains(permission)
                                || forceDeniedPermissions.contains(permission)) {
                            /* 检查权限是不是都包含在了这三种情况 */
                        } else {
                            /* 如果三者都不包含该权限 表明这个权限不是危险权限 可直接授权 */
                            grantedPermissions.add(permission);
                        }
                    }
                    requestFailed(grantedPermissions.toArray(new String[grantedPermissions.size()]),
                            normalDeniedPermissions.toArray(new String[normalDeniedPermissions.size()]),
                            forceDeniedPermissions.toArray(new String[forceDeniedPermissions.size()]));
                }
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION_SETTING) {
            // 如果是从设置页返回 继续请求权限
            requestPermission();
        }
    }


    private void showSystemPermissionsSettingDialog(List<String> grantedPermissions, List<String> normalDeniedPermissions, List<String> forceDeniedPermissions) {
        if (mPermissionDialog == null) {
            mPermissionDialog = new AlertDialog.Builder(mContext)
                    .setTitle(mContext.getString(R.string.permissions_check_warn))
                    .setMessage((checkConfig == null || "".equals(checkConfig.getForceDeniedPermissionTips())) ? forceDeniedPermissionTips : checkConfig.getForceDeniedPermissionTips())
                    .setPositiveButton(mContext.getString(R.string.permissions_check_ok), (dialog, which) -> {
                        // 打开设置页面
                        openSettingPage();
                    })
                    .setNegativeButton("取消", (dialog, which) -> {
                        // 关闭页面或者做其他操作
                        requestFailed(grantedPermissions.toArray(new String[grantedPermissions.size()]),
                                normalDeniedPermissions.toArray(new String[normalDeniedPermissions.size()]),
                                forceDeniedPermissions.toArray(new String[forceDeniedPermissions.size()]));
                    })
                    .create();
        }
        // 展示对话框
        mPermissionDialog.show();
    }


    /**
     * 打开设置页，方便用户授权
     */
    private void openSettingPage() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", mContext.getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, REQUEST_PERMISSION_SETTING);
    }

}