package com.twinsickle.aem.utils.ssh;

public class SSHCredentials {

    private String url;
    private int port;
    private String username;
    private String password;

    public SSHCredentials(String url, int port, String username, String password){
        this.url = url;
        this.port = port;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
