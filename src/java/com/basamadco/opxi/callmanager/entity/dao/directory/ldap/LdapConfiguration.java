package com.basamadco.opxi.callmanager.entity.dao.directory.ldap;

public class LdapConfiguration {
	
	private int version;
	
	private int port;
	
	private String host;
	
	private String username;
	
	private String password;


    public LdapConfiguration() {
        
    }

    public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}
	
	

}
