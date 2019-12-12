package com.mcnc.subwayexaple;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private static Context context;
    private static ArrayList<StationInfo> dataList = new ArrayList<>();
    private WebView mWebView;
    private WebSettings mWebSettings;
    private WebViewInterface mWebViewInterface;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
        mWebView = findViewById(R.id.webView);

        // 웹뷰 설정
        WebViewSetting("file:///android_asset/subway/html/SubwayMain.html", MainActivity.this, "Android");

        // 가져온 데이터 json 형식으로 맵핑
        doJSONParser(getData(make4DayAgoDate()));
    }

    /**
     * WebView Setting And Start, JS Interface Connect
     *
     * @param loadUrl       : 불러올 페이지 URL
     * @param activity      : 현재 Activity
     * @param interfaceName : JavaScriptInterface 이름
     */
    public void WebViewSetting(String loadUrl, Activity activity, String interfaceName) {
        mWebView.setWebViewClient(new WebViewClient());               // 클릭시 새창 안뜨게
        mWebSettings = mWebView.getSettings();                        // 세부 세팅 등록
        mWebSettings.setJavaScriptEnabled(true);                      // 웹페이지 자바스크립트 허용 여부
        mWebSettings.setSupportMultipleWindows(false);                // 새창 띄우기 허용 여부
        mWebSettings.setJavaScriptCanOpenWindowsAutomatically(false); // 자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        mWebSettings.setLoadWithOverviewMode(true);                   // 메타태그 허용 여부
        mWebSettings.setSupportZoom(false);                           // 화면 줌 허용 여부
        mWebSettings.setBuiltInZoomControls(false);                   // 화면 확대 축소 허용 여부
        mWebSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);         // 브라우저 캐시 허용 여부
        mWebSettings.setDomStorageEnabled(false);                     // 로컬저장소 허용 여부
        mWebView.setWebChromeClient(new WebChromeClient() {           // JS alert 사용 설정
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final android.webkit.JsResult result) {
                new AlertDialog.Builder(getAppContext()).setMessage(message)
                        .setPositiveButton(android.R.string.ok, new AlertDialog.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                result.confirm();
                            }
                        }).setCancelable(false).create().show();
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, final android.webkit.JsResult result) {
                new AlertDialog.Builder(getAppContext()).setMessage(message)
                        .setPositiveButton(android.R.string.ok,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.confirm();
                                    }
                                })
                        .setNegativeButton(android.R.string.cancel,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        result.cancel();
                                    }
                                })
                        .create()
                        .show();
                return true;
            }
        });

        // 웹뷰에 표시할 웹사이트 주소, 웹뷰 시작
        mWebView.loadUrl(loadUrl);


        // 웹뷰 <> 자바스크립트 인터페이스 연결
        mWebViewInterface = new WebViewInterface(activity, mWebView);
        mWebView.addJavascriptInterface(mWebViewInterface, interfaceName); // 웹뷰에 JavascriptInterface 연결
    }

    /**
     * 4일 전 날짜 계산하기
     *
     * @return 4DayAgoDate
     */
    public static String make4DayAgoDate() {
        Calendar calendar = Calendar.getInstance(); // 현재 시간
        calendar.add(Calendar.DATE, -4); // 4일 전
        Date date = new Date(calendar.getTimeInMillis());

        SimpleDateFormat mFormat = new SimpleDateFormat("yyyyMMdd");
        return mFormat.format(date);
    }

    /**
     * JSON String 형식으로 데이터 가져오기(AsyncTask)
     *
     * @param date : 검색 날짜
     * @return jsonString : 검색 결과
     */
    public static String getData(String date) {
        String jsonString = "";

        try {
            jsonString = new DataTask(getAppContext(), date).execute().get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

    /**
     * JSON String 맵핑하기
     *
     * @param json : jsonString
     */
    public static void doJSONParser(String json) {
        dataList.clear();
        try {
            // String Type JSON을 인자로 넣어 jsonObject를 생성한다.
            JSONObject jsonObject = new JSONObject(json).getJSONObject("CardSubwayStatsNew");
            JSONArray jarray = jsonObject.getJSONArray("row");

            for (int i = 0; i < jarray.length(); i++) {
                // JSONObject 추출
                JSONObject jObject = jarray.getJSONObject(i);
                String name = "SUB_STA_NM";
                String lineNum = "LINE_NUM";
                String rideNum = "RIDE_PASGR_NUM";
                String alightNum = "ALIGHT_PASGR_NUM";
                String workDate = "WORK_DT";

                // 객체에 키가 있는지 확인 후 데이터 삽입
                if (jObject.has(name)) name = jObject.getString(name);
                if (jObject.has(lineNum)) lineNum = jObject.getString(lineNum);
                if (jObject.has(rideNum)) rideNum = jObject.getString(rideNum);
                if (jObject.has(alightNum)) alightNum = jObject.getString(alightNum);
                if (jObject.has(workDate)) workDate = jObject.getString(workDate);

                // 정보 저장
                StationInfo info = new StationInfo();
                info.setAllData(name, lineNum, rideNum, alightNum, workDate);
                dataList.add(info);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getAppContext(), "해당 날짜에는 데이터가 없습니다.", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getAppContext(), "알 수 없는 오류", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 결과 검색 로직 처리
     *
     * @param keyword : 검색어
     * @return
     */
    public static String resultLogic(String keyword) {
        StringBuffer sb = new StringBuffer();
        String sep = "\\n"; // 구분자

        for (int i = 0; i < dataList.size(); i++) {
            StationInfo ele = dataList.get(i);
            String eleName = ele.getName();

            // 검색 형식을 맞추기 위한 검증
            keyword = keywordValidation(eleName, keyword);

            // 검색어와 같은 요소가 있을 때
            if (eleName.equals(keyword)) {
                sb.append(
                        sep + sep
                                + "호선 : " + ele.getLineNum() + sep
                                + "역명 : " + ele.getName() + sep
                                + "승차 승객수 : " + printNumFormat(ele.getRideNum()) + sep
                                + "하차 승객수 : " + printNumFormat(ele.getAlightNum()) + sep
                                + "등록일자 : " + printDateFormat(ele.getWorkDate()) + sep
                                + sep + sep
                );
            }
        }
        return sb.toString();
    }

    /**
     * 검색 형식을 맞추기 위한 검증
     *
     * @param eleName : 요소의 역명
     * @param keyword : 검색할 역명
     * @return 검증된 역명
     */
    public static String keywordValidation(String eleName, String keyword) {
        // 역명의 끝이 "역"이 아니고 검색 키워드의 끝이 "역"일 경우
        if (!eleName.endsWith("역") && keyword.endsWith("역")) {
            // 검색 키워드의 끝 글자("역")을 자르고
            String temp = keyword.substring(0, keyword.length() - 1);
            // 역명과 비교했을 때 같으면, "역"을 자른 문자열로 초기화
            if (eleName.equals(temp)) keyword = temp;
        }
        // 반대의 경우
        else if (eleName.endsWith("역") && !keyword.endsWith("역")) {
            // 역명의 끝 글자("역")을 자르고
            String temp = eleName.substring(0, eleName.length() - 1);
            // 키워드와 비교했을 때 같으면, "역"을 추가한 문자열로 초기화
            if (keyword.equals(temp)) keyword += "역";
        }
        return keyword;
    }

    /**
     * 화면에 표시될 승객수 포맷팅
     *
     * @param num : 원데이터
     * @return ###, ###명
     */
    public static String printNumFormat(String num) {
        DecimalFormat mNumFormat = new DecimalFormat("###,###명");
        return mNumFormat.format(Double.parseDouble(num));
    }

    /**
     * 화면에 표시될 등록일자 포맷팅
     *
     * @param date : yyyyMMdd
     * @return yyyy년 MM월 dd일
     */
    public static String printDateFormat(String date) {
        SimpleDateFormat beforeFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat afterFormat = new SimpleDateFormat("yyyy월 MM월 dd일");

        Date tempDate;
        String transDate = "";

        try {
            tempDate = beforeFormat.parse(date);
            transDate = afterFormat.format(tempDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return transDate;
    }

    /**
     * MainActivity 정적 Context 구하기
     *
     * @return context
     */
    public static Context getAppContext() {
        return MainActivity.context;
    }

}