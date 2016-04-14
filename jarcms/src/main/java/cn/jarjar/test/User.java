package cn.jarjar.test;

public class User {
    private Long id;

    private Long organizationId;

    private String username;

    private String password;

    private String salt;

    private String roleIds;

    private Boolean locked;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Long organizationId) {
        this.organizationId = organizationId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt == null ? null : salt.trim();
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds == null ? null : roleIds.trim();
    }

    public Boolean getLocked() {
        return locked;
    }

    public void setLocked(Boolean locked) {
        this.locked = locked;
    }
    
    public String getCredentialsSalt() {
        return username + salt;
    }

	@Override
	public String toString() {
		return "User [id=" + id + ", organizationId=" + organizationId
				+ ", username=" + username + ", password=" + password
				+ ", salt=" + salt + ", roleIds=" + roleIds + ", locked="
				+ locked + "]";
	}
    
    
}