package com.forgotten.lpermissionrequset;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * @ClassName LPermissionConfig
 * @Description 配置类
 * @Author Palmer
 * @Date 2021/12/21
 **/
public class LPermissionConfig implements Parcelable {
    /**
     * 是否要求所有的权限都通过
     */
    private boolean forceAllPermissionsGranted;
    /**
     * 用户点击"不再提示"后的 弹窗文案
     */
    private String forceDeniedPermissionTips;


    public void setForceAllPermissionsGranted(boolean forceAllPermissionsGranted) {
        this.forceAllPermissionsGranted = forceAllPermissionsGranted;
    }


    public void setForceDeniedPermissionTips(String forceDeniedPermissionTips) {
        this.forceDeniedPermissionTips = forceDeniedPermissionTips;
    }

    public LPermissionConfig() {
        forceAllPermissionsGranted = false;
        forceDeniedPermissionTips = "";
    }


    public String getForceDeniedPermissionTips() {
        return forceDeniedPermissionTips;
    }

    public boolean isForceAllPermissionsGranted() {
        return forceAllPermissionsGranted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeByte((byte) (forceAllPermissionsGranted ? 1 : 0));
        parcel.writeString(forceDeniedPermissionTips);
    }

    protected LPermissionConfig(Parcel in) {
        forceAllPermissionsGranted = in.readByte() != 0;
        forceDeniedPermissionTips = in.readString();
    }

    public static final Creator<LPermissionConfig> CREATOR = new Creator<LPermissionConfig>() {
        @Override
        public LPermissionConfig createFromParcel(Parcel in) {
            return new LPermissionConfig(in);
        }

        @Override
        public LPermissionConfig[] newArray(int size) {
            return new LPermissionConfig[size];
        }
    };
}