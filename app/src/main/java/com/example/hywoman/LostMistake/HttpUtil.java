package com.example.hywoman.LostMistake;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Administrator on 2015-05-08.
 */

public class HttpUtil {
    static {
        try {
            TrustManager[] trustAllCerts = { new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(X509Certificate[] certs,
                                               String authType) {
                }

                public void checkServerTrusted(X509Certificate[] certs,
                                               String authType) {
                }
            } };
            SSLContext sc = SSLContext.getInstance("SSL");

            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            };
            sc.init(null, trustAllCerts, new SecureRandom());

            HttpsURLConnection
                    .setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        } catch (Exception localException) {
        }
    }

    private static HttpUtil httpUtil;

    private HttpUtil() {
    }

    public static HttpUtil getHttpUtil() {
        if (httpUtil == null)
            httpUtil = new HttpUtil();

        return httpUtil;
    }

    public String doPost(String url, String data) throws Exception {
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setDoOutput(true);
        conn.addRequestProperty("Content-Type", "application/xml");
        OutputStreamWriter writer = new OutputStreamWriter(
                conn.getOutputStream());

        writer.write(data);
        writer.flush();

        String line;
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        writer.close();
        reader.close();
        int status = conn.getResponseCode();
        conn.disconnect();
        return status + "";
    }

    public String doGet(String url) throws Exception {

// configure the SSLContext with a TrustManager

        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setDoOutput(true);
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(20000);

        String line;
        StringBuffer buffer = new StringBuffer();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        reader.close();
        conn.disconnect();

        return buffer.toString();
    }

    public ArrayList<Integer> doGetInt(String url) throws Exception {

// configure the SSLContext with a TrustManager

        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setDoOutput(true);

        int value;
        ArrayList<Integer> getData = new ArrayList<Integer>();
        InputStream is = conn.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        while ((value = reader.read()) != -1) {
            getData.add(value);
        }
        reader.close();
        conn.disconnect();

        return getData;
    }

    public byte[] doGetBytes(String url) throws Exception {

// configure the SSLContext with a TrustManager

        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setReadTimeout(20000);
        conn.setConnectTimeout(20000);
        conn.setDoOutput(true);

        byte value;
        ArrayList<Byte> getData = new ArrayList<Byte>();
        InputStream is = conn.getInputStream();
        DataInputStream dis = new DataInputStream(is);

        while(true) {
            int temp = is.read();
            Log.d("dhkim",""+temp);
            if (temp == -1) break;
            getData.add((byte)(temp));
        }
        byte[] bytedata = new byte[getData.size()];
        dis.read(bytedata,0,getData.size());

        for(int i=0; i<bytedata.length; i++){
            bytedata[i]=getData.get(i);
            Log.d("dhkim",  "new "+bytedata[i]);
        }

        //Log.d("dhkim", "totalbytes = " + data.size());

        is.close();
        conn.disconnect();

        return bytedata;
    }


    public InputStream doGetStream(String url) throws Exception {

        // configure the SSLContext with a TrustManager
        URL urlObj = new URL(url);
        HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection();
        conn.setDoOutput(true);

        InputStream is = conn.getInputStream();

        conn.disconnect();

        return is;
    }
}
