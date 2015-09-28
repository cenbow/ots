package com.hessian.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.google.gson.Gson;

/**
 * @author he
 *         压力测试工具
 */
public class HttpGetAndPostTest {
    public static final String POST = "post";
    private static Gson gson = new Gson();

    public static String getData(String url, String httpType, Map<String, String> params) {
        HttpClient httpclient = new DefaultHttpClient();
        if (POST.equals(httpType)) {
            HttpPost httpPost = new HttpPost(url);
            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            if (params != null && !params.isEmpty()) {
                for (Entry<String, String> entry : params.entrySet()) {
                    nvps.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));
            String str = null;
            try {
                HttpResponse response = httpclient.execute(httpPost);
                return decompress(response);
            } catch (ClientProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                httpclient.getConnectionManager().shutdown();
            }
            if (str != null && str.length() > 0) {
                return str;
            } else {
                return null;
            }
        }
        return null;
    }

    public static String decompress(HttpResponse response) {
        String result = null;
        if (response.getStatusLine().getStatusCode() == 200) {
            try {
                result = EntityUtils.toString(response.getEntity(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public static void main(String[] args) {
        ExecutorService pool = Executors.newFixedThreadPool(50);
        //params.put
        //params.put();
        final List<Integer> list = new ArrayList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        final List<Integer> list2 = new ArrayList<Integer>();
        list2.add(106);
        list2.add(107);
        list2.add(108);
        for (int i = 0; i < 80; i++) {
            pool.execute(new Runnable() {
                @Override
                public void run() {
                    runMq(list,list2);
                    //params.put("token", "0f09c9e0-6a30-42ac-a590-0ca3c69373e0");
//                    String s = getData("http://10.4.11.218:8089/ots/roomstate/querylist?hotelid=1430&startdateday=20150831&enddateday=20150901","post",params);
//                    System.out.println(s);
                }
            });
        }

    }
    
    
    private static void runMq(final List<Integer> list,final List<Integer> list2){
    	Random r = new Random();
        int A = r.nextInt(3);
        Random r2 = new Random();
        int B= r2.nextInt(3);
        Map<String, String> params = new HashMap<String, String>();
        params.put("copywritertype", list2.get(A)+"");
        params.put("mid", 200097+"");
        params.put("orderid", 12322+"");
        System.out.println(list.get(B));
//        params.put("messagetype", list.get(B)+"");
        params.put("messagetype", 1+"");
        //params.put("token", "0f09c9e0-6a30-42ac-a590-0ca3c69373e0");
//        String s = getData("http://10.4.11.218:8089/ots/roomstate/querylist?hotelid=1430&startdateday=20150831&enddateday=20150901","post",params);
//        String s = getData("http://10.4.37.152:8080/care/test/send", "post", params);
        String s = getData("http://127.0.0.1:8080/care/test/send", "post", params);
    }
}
