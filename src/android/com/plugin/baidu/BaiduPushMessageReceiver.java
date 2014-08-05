package com.plugin.baidu;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.frontia.api.FrontiaPushMessageReceiver;

public class BaiduPushMessageReceiver extends FrontiaPushMessageReceiver {

  @Override
  public void onBind(Context context, int errorCode, String appid,
      String userId, String channelId, String requestId) {
    String responseString = "onBind errorCode=" + errorCode + " appid="
        + appid + " userId=" + userId + " channelId=" + channelId
        + " requestId=" + requestId;
    Log.d(TAG, responseString);

    // 绑定成功，设置已绑定flag，可以有效的减少不必要的绑定请求
    if (errorCode == 0) {
      Utils.setBind(context, true);

      HashMap data = new HashMap();
      data.put("errorCode", errorCode + "");
      data.put("appid", appid);
      data.put("userId", userId);
      data.put("channelId", channelId);
      data.put("requestId", requestId);
      data.put("event", "registered");

      PushBaiduPlugin.getInstance().sendSuccess(data);
    } else {
      Utils.setBind(context, false);
      PushBaiduPlugin.getInstance().sendError(
          "绑定到百度失败，errorCode：" + errorCode);
    }
  }

  @Override
  public void onDelTags(Context context, int errorCode,
      List<String> sucessTags, List<String> failTags, String requestId) {
    String responseString = "onDelTags errorCode=" + errorCode
        + " sucessTags=" + sucessTags + " failTags=" + failTags
        + " requestId=" + requestId;
    Log.d(TAG, responseString);
  }

  @Override
  public void onListTags(Context context, int errorCode, List<String> tags,
      String requestId) {
    String responseString = "onListTags errorCode=" + errorCode + " tags="
        + tags;
    Log.d(TAG, responseString);
  }

  @Override
  public void onMessage(Context context, String message,
      String customContentString) {
    String messageString = "透传消息 message=\"" + message
        + "\" customContentString=" + customContentString;
    Log.d(TAG, messageString);

    HashMap data = new HashMap();
    data.put("event", "message");

    HashMap payload = new HashMap();
    payload.put("message", message);

    // 自定义内容获取方式，mykey和myvalue对应透传消息推送时自定义内容中设置的键和值
    if (!TextUtils.isEmpty(customContentString)) {
      JSONObject customJson = null;
      try {
        customJson = new JSONObject(customContentString);
        if (customJson.has("badge") && !customJson.isNull("badge")) {
          payload.put("badge", customJson.getString("badge"));
        }
      } catch (JSONException e) {
        e.printStackTrace();
      }
    }

    PushBaiduPlugin.getInstance().sendJavascript(data, payload);
  }

  @Override
  public void onNotificationClicked(Context context, String title,
      String description, String customContentString) {
    String notifyString = "通知点击 title=\"" + title + "\" description=\""
        + description + "\" customContent=" + customContentString;
    Log.d(TAG, notifyString);

    HashMap data = new HashMap();
    data.put("event", "notification_clicked");

    HashMap payload = new HashMap();
    payload.put("title", title);
    payload.put("description", description);

    // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
    if (!TextUtils.isEmpty(customContentString)) {
      JSONObject customJson = null;
      try {
        customJson = new JSONObject(customContentString);
        if (customJson.has("badge") && !customJson.isNull("badge")) {
          payload.put("badge", customJson.getString("badge"));
        }
      } catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    data.put("payload", payload);
    PushBaiduPlugin.getInstance().sendJavascript(data, payload);
  }

  @Override
  public void onSetTags(Context context, int errorCode,
      List<String> sucessTags, List<String> failTags, String requestId) {
    String responseString = "onSetTags errorCode=" + errorCode
        + " sucessTags=" + sucessTags + " failTags=" + failTags
        + " requestId=" + requestId;
    Log.d(TAG, responseString);
  }

  @Override
  public void onUnbind(Context context, int errorCode, String requestId) {
    String responseString = "onUnbind errorCode=" + errorCode
        + " requestId = " + requestId;
    Log.d(TAG, responseString);

    // 解绑定成功，设置未绑定flag，
    if (errorCode == 0) {
      Utils.setBind(context, false);

      HashMap data = new HashMap();
      data.put("errorCode", errorCode + "");
      data.put("requestId", requestId);
      data.put("event", "unregistered");

      PushBaiduPlugin.getInstance().sendSuccess(data);
    } else {
      PushBaiduPlugin.getInstance().sendError("从百度解除绑定失败，errorCode：" + errorCode);
    }
  }
}
