package com.example.proformancemonitor;

import android.content.Context;
import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


//a class for all the network activities, including connection, readbuffer..
//will also handle connection failed exception, it will start ConnectionFailedActivity
public class NetworkConnection {
    private String ipAddress = "";
    private String jsonStr = "";
    Context context;

    private HttpURLConnection urlConnection;
    private BufferedInputStream bufferedInputStream;
    private BufferedReader bufferedReader;
    private StringBuffer stringBuffer;
    private JSONObject jsonObject;

    public NetworkConnection(String ipAddress, String jsonStr, Context context){
        this.ipAddress = ipAddress;
        this.jsonStr = jsonStr;
        this.context = context;
    }

    public String connect(){
        URL url = null;

        try {
            url = new URL(ipAddress);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(1000);

            String jsonInputString = jsonStr;//"{\"text\": \"cpuInfo\"}";

            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            int code = urlConnection.getResponseCode();
            bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));
            stringBuffer = new StringBuffer();
            String str = "";

            if(code == 200){
                while ((str = bufferedReader.readLine()) != null){
                    stringBuffer.append(str);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            netwoekConnectionFailed();
            return null;
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                netwoekConnectionFailed();
                return null;
            }
        }
        if(stringBuffer == null){
            System.out.println("string buffer is empty");
            netwoekConnectionFailed();
            return null;
        }

        jsonObject = null;
        String responseStr = "";

        try {
            jsonObject = new JSONObject(stringBuffer.toString());
            responseStr = jsonObject.getString("text");

        } catch (JSONException e) {
            netwoekConnectionFailed();
            e.printStackTrace();
            return null;
        }
        return responseStr;
    }

    //if expecting multiple return value
    public ArrayList<String> connect(int expectedListSize){
        ArrayList<String> returnList = new ArrayList<String>();
        URL url = null;

        try {
            url = new URL(ipAddress);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            urlConnection.setRequestProperty("Accept", "application/json");
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setConnectTimeout(1000);

            String jsonInputString = jsonStr;//"{\"text\": \"cpuInfo\"}";

            try(OutputStream os = urlConnection.getOutputStream()) {
                byte[] input = jsonInputString.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            int code = urlConnection.getResponseCode();
            bufferedInputStream = new BufferedInputStream(urlConnection.getInputStream());
            bufferedReader = new BufferedReader(new InputStreamReader(bufferedInputStream));
            stringBuffer = new StringBuffer();
            String str = "";

            if(code == 200){
                while ((str = bufferedReader.readLine()) != null){
                    stringBuffer.append(str);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            netwoekConnectionFailed();
            return null;
        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
            try {
                if (bufferedReader != null){
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
                netwoekConnectionFailed();
                return null;
            }
        }
        if(stringBuffer == null){
            System.out.println("string buffer is empty");
            netwoekConnectionFailed();
            return null;
        }

        jsonObject = null;
        String responseStr = "";

        try {
            jsonObject = new JSONObject(stringBuffer.toString());
            for(int i=0; i<expectedListSize; i++){
                returnList.add(jsonObject.getString(("text" + i)));
            }
        } catch (JSONException e) {
            netwoekConnectionFailed();
            e.printStackTrace();
            return null;
        }
        return returnList;
    }

    //will start ConnectionFailedActivity
    public void netwoekConnectionFailed(){
        Intent intent_error = new Intent(context, ConnectionFailedActivity.class);
        intent_error.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent_error.putExtra("ipAddress", ipAddress);
        context.startActivity(intent_error);
    }
}




