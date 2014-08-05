package com.plugin.baidu;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;

public class Utils {
  public static void setBind(Context context, boolean flag) {
    String flagStr = "not";
    if (flag) {
      flagStr = "ok";
    }
    SharedPreferences sp = PreferenceManager
        .getDefaultSharedPreferences(context);
    Editor editor = sp.edit();
    editor.putString("bind_flag", flagStr);
    editor.commit();
  }

  // 用share preference来实现是否绑定的开关。在ionBind且成功时设置true，unBind且成功时设置false
  public static boolean hasBind(Context context) {
    SharedPreferences sp = PreferenceManager
        .getDefaultSharedPreferences(context);
    String flag = sp.getString("bind_flag", "");
    if ("ok".equalsIgnoreCase(flag)) {
      return true;
    }
    return false;
  }
}
