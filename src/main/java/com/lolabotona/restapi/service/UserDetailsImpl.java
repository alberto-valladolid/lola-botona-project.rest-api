package com.lolabotona.restapi.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.lolabotona.restapi.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;

public class UserDetailsImpl implements UserDetails {
	private static final long serialVersionUID = 1L;

	private Long id;

	private String username;

	private String role;
	
	private String name;

	@JsonIgnore
	private String password;

	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String username, String role, String password, String name,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.role = role;
		this.password = password;
		this.name = name;
		this.authorities = Arrays.asList(new SimpleGrantedAuthority( role ));
	}

	public static UserDetailsImpl build(User user) {
		
		List<GrantedAuthority> authorities =  Arrays.asList(new SimpleGrantedAuthority( user.getRole()));
		
		return new UserDetailsImpl(
				user.getId(), 
				user.getUsername(), 
				user.getRole(),
				user.getPassword(), 
				user.getName(),
				authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public Long getId() {
		return id;
	}

	public String getRole() {
		return role;
	}

	@Override
	public String getPassword() {
		return password;
	}
	

	public String getName() {
		return name;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserDetailsImpl user = (UserDetailsImpl) o;
		return Objects.equals(id, user.id);
	}
}