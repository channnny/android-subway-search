package com.mcnc.subwayexaple;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class DataTask extends AsyncTask<String, String, String> {

    private Context context;
    private String date;
    private ProgressDialog asyncDialog;

    /**
     *
     * @param context : 적용될 Context
     * @param date : 검색할 날짜
     */
    public DataTask(Context context, String date) {
        this.context = context;
        this.date = date;
    }

    private final String URL = "http://openapi.seoul.go.kr:8088/";      // 호출할 URL
    private final String APP_KEY = "4f5861784173796337396a50516d6e/";   // 앱 키
    private final String DATA_TYPE = "json/CardSubwayStatsNew/";        // 데이터 타입 지정
    private final String START_INDEX = "1/";                            // 시작 인덱스
    private final String END_INDEX = "600/";                            // 종료 인덱스
    private final String REQUEST_URL = URL + APP_KEY + DATA_TYPE + START_INDEX + END_INDEX;

    private String str, receiveMsg;

    /**
     * 데이터 송신 전 프로그레스바 생성
     */
    @Override
    protected void onPreExecute() {
        asyncDialog = new ProgressDialog(context);
        asyncDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        asyncDialog.setMessage("로딩중입니다..");
        asyncDialog.show();
    }

    /**
     * Rest API
     * @return receiveMsg : jsonString
     */
    @Override
    protected String doInBackground(String... params) {
        try {
            URL url = new URL(REQUEST_URL + date); // 검색 URL
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            if (conn.getResponseCode() == conn.HTTP_OK) {
                InputStreamReader tmp = new InputStreamReader(conn.getInputStream(), "UTF-8");
                BufferedReader reader = new BufferedReader(tmp);
                StringBuffer buffer = new StringBuffer();
                while ((str = reader.readLine()) != null) {
                    buffer.append(str);
                }
                receiveMsg = buffer.toString();
                Log.i("receiveMsg : ", receiveMsg);

                reader.close();
            } else {
                Log.i("통신 결과", conn.getResponseCode() + "에러");
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return receiveMsg;
    }

    /**
     * 데이터 송신 후 다이얼로그 제거
     */
    @Override
    protected void onPostExecute(String s) {
        asyncDialog.dismiss();
        if(s != null) Log.d("DataTask Result : ", "Success");
        else Log.d("DataTask Result : ", "Fail");
    }
}
