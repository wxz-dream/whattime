package com.whatime.module.books;

import java.io.Serializable;

public class FriendReq implements Serializable
{
    
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    private String uuid;
    
    private Integer access;
    
    private String friendReqUuid;
    
    private String nickName;
    
    private String remark;
    
    private Long requestTime;
    
    private Long uptTime;
    
    private String userUuid;
    
    private String userphotoUri;

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }

    public Integer getAccess()
    {
        return access;
    }

    public void setAccess(Integer access)
    {
        this.access = access;
    }

    public String getFriendReqUuid()
    {
        return friendReqUuid;
    }

    public void setFriendReqUuid(String friendReqUuid)
    {
        this.friendReqUuid = friendReqUuid;
    }

    public String getNickName()
    {
        return nickName;
    }

    public void setNickName(String nickName)
    {
        this.nickName = nickName;
    }

    public String getRemark()
    {
        return remark;
    }

    public void setRemark(String remark)
    {
        this.remark = remark;
    }

    public Long getRequestTime()
    {
        return requestTime;
    }

    public void setRequestTime(Long requestTime)
    {
        this.requestTime = requestTime;
    }

    public Long getUptTime()
    {
        return uptTime;
    }

    public void setUptTime(Long uptTime)
    {
        this.uptTime = uptTime;
    }

    public String getUserUuid()
    {
        return userUuid;
    }

    public void setUserUuid(String userUuid)
    {
        this.userUuid = userUuid;
    }

    public String getUserphotoUri()
    {
        return userphotoUri;
    }

    public void setUserphotoUri(String userphotoUri)
    {
        this.userphotoUri = userphotoUri;
    }
    
    
    
}
