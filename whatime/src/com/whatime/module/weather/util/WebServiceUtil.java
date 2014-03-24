package com.whatime.module.weather.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;

import com.google.gson.stream.JsonReader;

public class WebServiceUtil
{
    
    static final String SERVICE_PROVIDER = "http://flash.weather.com.cn/wmaps/xml/china.xml";
    
    static final String SERVICE_CITY = "http://flash.weather.com.cn/wmaps/xml/";
    
    static final String SERVICE_SEARCH = "http://m.weather.com.cn/data/";
    
    public static HashMap<String, String> providers;
    
    static class Provider
    {
        public String name;
        
        public String code;
    }
    
    static class City
    {
        public String name;
        
        public String code;
    }
    
    /**
     * 获得州，国内外省份和城市信息
     * 
     * @return
     */
    public static HashMap<String, String> getProvinceList()
    {
        if (providers != null && providers.size() > 0)
        {
            return providers;
        }
        providers = new HashMap<String, String>();
        HttpGet get = new HttpGet(SERVICE_PROVIDER);
        HttpClient client = new DefaultHttpClient();
        try
        {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream input = entity.getContent();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(input, "utf-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT)
            {
                switch (type)
                {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("city"))
                        {
                            Provider p = new Provider();
                            for (int i = 0; i < parser.getAttributeCount(); i++)
                            {
                                
                                if (parser.getAttributeName(i).equals("quName"))
                                {
                                    p.name = parser.getAttributeValue(i);
                                }
                                if (parser.getAttributeName(i).equals("pyName"))
                                {
                                    p.code = parser.getAttributeValue(i);
                                }
                                providers.put(p.name, p.code);
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                type = parser.next();
            }
        }
        catch (ClientProtocolException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        catch (XmlPullParserException e)
        {
            e.printStackTrace();
        }
        return providers;
    }
    
    /**
     * 根据省份获取城市列表
     * 
     * @param province
     * @return
     */
    public static HashMap<String, String> getCityListByProvince(String code)
    {
        HashMap<String, String> cs = new HashMap<String, String>();
        HttpGet get = new HttpGet(SERVICE_CITY + code + ".xml");
        HttpClient client = new DefaultHttpClient();
        try
        {
            HttpResponse response = client.execute(get);
            HttpEntity entity = response.getEntity();
            InputStream input = entity.getContent();
            XmlPullParser parser = Xml.newPullParser();
            parser.setInput(input, "utf-8");
            int type = parser.getEventType();
            while (type != XmlPullParser.END_DOCUMENT)
            {
                switch (type)
                {
                    case XmlPullParser.START_TAG:
                        if (parser.getName().equals("city"))
                        {
                            City c = new City();
                            for (int i = 0; i < parser.getAttributeCount(); i++)
                            {
                                if (parser.getAttributeName(i).equals("cityname"))
                                {
                                    c.name = parser.getAttributeValue(i);
                                }
                                if (parser.getAttributeName(i).equals("url"))
                                {
                                    c.code = parser.getAttributeValue(i);
                                }
                                if (c.name != null && !c.equals("null"))
                                {
                                    cs.put(c.name, c.code);
                                }
                            }
                        }
                        break;
                    case XmlPullParser.TEXT:
                        break;
                    case XmlPullParser.END_TAG:
                        break;
                    default:
                        break;
                }
                type = parser.next();
            }
        }
        catch (ClientProtocolException e)
        {
        }
        catch (IOException e)
        {
        }
        catch (XmlPullParserException e)
        {
        }
        return cs;
    }
    
    public static HashMap<String, String> getWeatherByCity(String code)
    {
        InputStream input = null;
        HashMap<String, String> ret = new HashMap<String, String>();
        HttpGet get = new HttpGet(SERVICE_SEARCH + code + ".html");
        HttpClient client = new DefaultHttpClient();
        HttpResponse response;
        
        try
        {
            response = client.execute(get);
            if (response == null)
            {
                return ret;
            }
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK)
            {
                return ret;
            }
            HttpEntity entity = response.getEntity();
            if (entity == null)
            {
                return ret;
            }
            input = entity.getContent();
            if (input == null)
            {
                return ret;
            }
        }
        catch (ClientProtocolException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (IOException e)
        {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        try
        {
            if (input == null)
            {
                return ret;
            }
            JsonReader reader = new JsonReader(new InputStreamReader(input, "utf-8"));
            reader.beginObject();
            while (reader.hasNext())
            {
                String localName = reader.nextName();
                if (localName.equals("weatherinfo"))
                {
                    reader.beginObject();
                    while (reader.hasNext())
                    {
                        localName = reader.nextName();
                        if (localName.contains("temp"))
                        {
                            String[] str = reader.nextString().split("~");
                            float left = 0;
                            float right = 1;
                            if(str[0].contains("℉"))
                            {
                                left = Float.valueOf(str[0].substring(0, str[0].indexOf("℉")));
                                right = Float.valueOf(str[1].substring(0, str[1].indexOf("℉")));
                            }else
                            {
                                left = Float.valueOf(str[0].substring(0, str[0].indexOf("℃")));
                                right = Float.valueOf(str[1].substring(0, str[1].indexOf("℃")));
                            }
                             
                            if(left>right)
                            {
                                ret.put(localName, str[1] + "~" + str[0]);
                            }else
                            {
                                ret.put(localName, str[0] + "~" + str[1]);
                            }
                        }
                        else
                        {
                            ret.put(localName, reader.nextString());
                        }
                        
                    }
                }
                reader.endObject();
            }
            reader.endObject();
        }
        catch (IOException e)
        {
        }
        return ret;
    }
    
}
