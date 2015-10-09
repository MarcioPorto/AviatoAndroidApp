package com.aviato.android.aviato.beacons;

import java.net.*;
import java.io.*;

public class BeaconAPI {
    public static String getURL(String uuid, String major, String minor) {
        String base = "https://cube.api.aero/atibeacon/beacons/1/SITA_SIN/";
        String app_info = "?app_id=e719dcad&app_key=cedf92f7a02fd4e8cb585ba7477d9b19";
        String url = base + uuid + "/" + major + "/" + minor + app_info;
        return url;
    }

    public static String getHTML(String urlToRead) throws Exception {
        StringBuilder result = new StringBuilder();
        URL url = new URL(urlToRead);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        rd.close();
        return result.toString();
    }

    public static void main(String[] args) throws Exception
    {
        String uuid = "D54C1FAD-4749-11E5-8ED3-ACB57D6C3AB2";
        String major = "2300";
        String minor = "1";

        String url = getURL(uuid, major, minor);
        String result = getHTML(url);
        System.out.println(result);
    }
}

