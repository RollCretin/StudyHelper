package com.cretin.studyhelper.utils;

import android.content.Context;

/**
 * Created by cretin on 2018/3/20.
 */

public class ResourceUtil {
    /**
     * 根据文件名获取资源id
     * @param imageName
     * @param context
     * @param type drawable
     * @return
     */
    public static int getResource(String imageName, Context context,String type) {
        return context.getResources().getIdentifier(imageName, type, context.getPackageName());
    }
}
