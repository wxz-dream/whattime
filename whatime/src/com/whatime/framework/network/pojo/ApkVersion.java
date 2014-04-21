package com.whatime.framework.network.pojo;

import java.io.Serializable;

public class ApkVersion implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String uuid;
    
    private String version;
    
    private String url;

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion(String version)
    {
        this.version = version;
    }

    public String getUrl()
    {
        return url;
    }

    public void setUrl(String url)
    {
        this.url = url;
    }
    
    
    
}
