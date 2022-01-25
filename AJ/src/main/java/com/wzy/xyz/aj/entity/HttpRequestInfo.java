package com.wzy.xyz.aj.entity;

public class HttpRequestInfo {

    private int id;
    //功能名称
    private String name;
    //请求方法
    private String RequestMethod;
    //ip地址
    private String ipAddress;
    //端口号
    private Integer PortNumber;
    //请求路径
    private String Route;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRequestMethod() {
        return RequestMethod;
    }

    public void setRequestMethod(String requestMethod) {
        RequestMethod = requestMethod;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public Integer getPortNumber() {
        return PortNumber;
    }

    public void setPortNumber(Integer portNumber) {
        PortNumber = portNumber;
    }

    public String getRoute() {
        return Route;
    }

    public void setRoute(String route) {
        Route = route;
    }




}
