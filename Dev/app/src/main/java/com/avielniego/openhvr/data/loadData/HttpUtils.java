package com.avielniego.openhvr.data.loadData;

import android.net.Uri;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils
{
    private static final String LOG_TAG = HttpUtils.class.getSimpleName();

    public static JSONObject downloadJson(String url) throws IOException, JSONException
    {
        return new JSONObject(getRequest(url));
    }

    public static String getRequest(String url) throws IOException
    {
        HttpURLConnection urlConnection = null;
        try
        {
            urlConnection = (HttpURLConnection) new URL(url).openConnection();
            urlConnection.setDoInput(true);
            urlConnection.connect();
            return getRequest(urlConnection);
        }
        finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
    }

    public static String getRequest(HttpURLConnection urlConnection) throws IOException
    {

        BufferedReader reader = null;

        try
        {
            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null)
            {
                // Nothing to do.
                return "";
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null)
            {
                // It does make debugging a *lot* easier if you print out the completed
                // buffer for debugging with new lines.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0)
            {
                // Stream was empty.  No point in parsing.
                return "";
            }

            return buffer.toString();
        } finally
        {
            if (reader != null)
            {
                try
                {
                    reader.close();
                } catch (final IOException e)
                {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
    }

    public static String postRequest(Uri uri) throws IOException
    {
        HttpURLConnection urlConnection = null;
        try
        {
            URL url = new URL(uri.getScheme(), uri.getHost(), uri.getPath());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");

            // Write params to query
            OutputStream os = urlConnection.getOutputStream();
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(uri.getQuery());
            writer.flush();
            writer.close();
            os.close();

            urlConnection.connect();

            return getRequest(urlConnection);
        } finally
        {
            if (urlConnection != null)
            {
                urlConnection.disconnect();
            }
        }
    }

}
