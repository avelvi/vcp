package com.aivlev.vcp.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by aivlev on 4/27/16.
 */

@Document
public class Token implements Serializable {

    private static final long serialVersionUID = 2424635589078839418L;

    @Id
    private String series;
    private String value;
    private Date date;
    @Field("ip_address")
    private String ipAddress;
    @Field("user_agent")
    private String userAgent;
    @Field("user_login")
    private String userLogin;

    public String getSeries() {
        return series;
    }

    public void setSeries(String series) {
        this.series = series;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

}
