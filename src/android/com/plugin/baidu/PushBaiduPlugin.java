package com.plugin.baidu;

import java.util.HashMap;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.baidu.android.pushservice.PushConstants;
import com.baidu.android.pushservice.PushManager;

public class PushBaiduPlugin extends CordovaPlugin {
  public static final String TAG = "PushBaiduPlugin";

  public static final String REGISTER = "register";
  public static final String UNREGISTER = "unregister";
  public static final String EXIT = "exit";

  private static PushBaiduPlugin _THIS;
  private CallbackContext callbackContext = null;

  private static CordovaWebView gWebView;
  private static String gECB;

  public PushBaiduPlugin() {
    super();
    _THIS = this;
  }

  @Override
  public void initialize(CordovaInterface cordova, CordovaWebView webView) {
    super.initialize(cordova, webView);
  }

  @Override
  public boolean execute(String action, JSONArray args,
      CallbackContext callbackContext) throws JSONException {
    boolean result = false;

    Log.d(TAG, "execute: action=" + action);

    if (REGISTER.equals(action)) {

      Log.d(TAG, "execute: data=" + args.toString());
      this.callbackContext = callbackContext;

      try {
        JSONObject jo = args.getJSONObject(0);

        gWebView = this.webView;
        Log.v(TAG, "execute: jo=" + jo.toString());

        gECB = (String) jo.get("ecb");
        String apiKey = jo.getString("api_key");

        Log.v(TAG, "execute: ECB=" + gECB + " apiKey=" + apiKey);

        PushManager.startWork(getApplicationContext(),
            PushConstants.LOGIN_TYPE_API_KEY, apiKey);

        result = true;
        callbackContext.success();
      } catch (JSONException e) {
        Log.e(TAG, "execute: Got JSON Exception " + e.getMessage());
        result = false;
        callbackContext.error(e.getMessage());
      }
    } else if (UNREGISTER.equals(action)) {
      // TODO: 从百度解除绑定
      PushManager.unbind(getApplicationContext());

      Log.v(TAG, "UNREGISTER");
      result = true;
      callbackContext.success();
    } else {
      result = false;
      Log.e(TAG, "Invalid action : " + action);
      callbackContext.error("Invalid action : " + action);
    }
    return result;
  }

  /**
   * 绑定到Baidu时成功
   * 
   * @param data
   *            data中的字段(绑定成功后，百度返回的): errorCode, appid, userId, channelId,
   *            requestId
   */
  public void sendSuccess(HashMap registration) {
    if (this.callbackContext != null) {
      this.callbackContext.success(new JSONObject(registration));
    }
  }

  /**
   * 绑定到Baidu时出错
   * 
   * @param error
   */
  public void sendError(String error) {
    if (this.callbackContext != null) {
      HashMap data = new HashMap();
      data.put("error", error);
      callbackContext.error(new JSONObject(data));
    }
  }

  /**
   * 客户端接受到百度的推送后，调用 COS.pushController.onNotificationBaidu 方法
   * 
   * @param _json
   */
  public void sendJavascript(HashMap data, HashMap payload) {
    try {
      JSONObject _data = new JSONObject(data);
      JSONObject _payload = new JSONObject(data);
      _data.put("payload", _payload);

      String _d = "javascript:" + gECB + "(" + _data.toString() + ")";
      Log.v(TAG, "sendJavascript: " + _d);

      if (gECB != null && gWebView != null) {
        gWebView.sendJavascript(_d);
      }
    } catch (JSONException ex) {
      ex.printStackTrace();
    }
  }

  private Context getApplicationContext() {
    return this.cordova.getActivity().getApplicationContext();
  }

  public static PushBaiduPlugin getInstance() {
    return _THIS;
  }
}
