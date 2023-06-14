package com.amit.journal.service.util;

import com.amit.journal.constants.Constants;
import com.amit.journal.util.CommonUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class YahooFinanceUtil {

    private static final Logger LOG = LogManager.getLogger(YahooFinanceUtil.class);
    public static Map<String, String> getCookie() {
        Map<String, String> dataMap = new HashMap<>();
        String cookie = null;
        String crumb = null;
        try {
            URL url = new URL(Constants.YAHOO_FINANCE_COOKIE_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Get the cookie from the response headers
            cookie = connection.getHeaderField("Set-Cookie");

            if (cookie != null) {
                // Print the cookie
                LOG.info("******* cookie retrieved is : {}", cookie);
                System.out.println("Cookie: " + cookie);
            }
            crumb = getCrumb(cookie);
            //get crumb
            System.out.println(crumb);
            LOG.info("******* Retrieved cookie : {}, crumb : {}", cookie, crumb);
            // Disconnect the connection
            connection.disconnect();
            dataMap.put(Constants.YAHOO_COOKIE, cookie);
            dataMap.put(Constants.YAHOO_CRUMB, crumb);
        } catch (Exception e) {
            LOG.error("Exception : {}", CommonUtil.getStackTrace(e));
        }
        return dataMap;
    }

    public static String getCrumb(String cookie) {
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("https://query2.finance.yahoo.com/v1/test/getcrumb");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            // Set the cookie
            connection.setRequestProperty("Cookie", cookie);

            // Make the HTTP request
            connection.setRequestMethod("GET");

            // Read the response content
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Process the response content as needed
            System.out.println("Response: " + response.toString());

            // Disconnect the connection
            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response.toString();
    }
}
