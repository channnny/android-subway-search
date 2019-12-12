package com.mcnc.subwayexaple;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import java.util.ArrayList;

public class WebViewInterface {

    private Activity mContext;
    private WebView mAppView;
    private static ArrayList<StationInfo> result = null;

    /**
     * 생성자.
     * @param activity : context
     * @param view : 적용될 웹뷰
     */
    public WebViewInterface(Activity activity, WebView view) {
        mAppView = view;
        mContext = activity;
    }

    /**
     * 앱 처음 시작했을 때 초기 날짜 값 설정(4일 전)
     */
    @JavascriptInterface
    public void initDate() {
        final String date = MainActivity.make4DayAgoDate();

        mAppView.post(new Runnable() {
            @Override
            public void run() {
                mAppView.evaluateJavascript("javascript:getInitDate('" + date + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        // 데이터 리턴 받았을 때
                    }
                });
            }
        });
    }

    /**
     * 날짜가 바뀔때마다 데이터 가져오기
     * @param date : 검색 날짜
     */
    @JavascriptInterface
    public void changeDate(String date) {
        MainActivity.doJSONParser(MainActivity.getData(date));
    }

    /**
     * WebView에서 값을 전달 받아 검색 결과를 화면에 띄워준다.
     * @param keyword : 검색 키워드
     */
    @JavascriptInterface
    public void search(String keyword) {
        final String result = MainActivity.resultLogic(keyword);

        mAppView.post(new Runnable() {
            @Override
            public void run() {
                mAppView.evaluateJavascript("javascript:getNativeData('" + result + "')", new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        // 데이터 리턴 받았을 때 처리
                    }
                });
            }
        });
    }

}
