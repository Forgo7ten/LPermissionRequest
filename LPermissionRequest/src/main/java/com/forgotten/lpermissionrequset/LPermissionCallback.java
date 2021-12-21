package com.forgotten.lpermissionrequset;

/**
 * @ClassName LPermissionCallback
 * @Description 权限申请回调接口
 * @Author Palmer
 * @Date 2021/12/21
 **/
public interface LPermissionCallback {
    /**
     * 请求权限通过 回调方法
     */
    void onRequestSuccess();

    /**
     * 请求权限未通过 回调方法
     *
     * @param grantedPermissions     已授权的权限
     * @param deniedPermissions      拒绝授权的权限
     * @param forceDeniedPermissions 永久拒绝(不再提醒)的权限
     */
    void onRequestFailed(String[] grantedPermissions, String[] deniedPermissions, String[] forceDeniedPermissions);
}