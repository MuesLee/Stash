package de.ts.stash.domain;

import java.util.Collection;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Builder
public class ApplicationUser implements UserDetails {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(unique = true, nullable = false)
	private String username;

	@Column(nullable = false)
	private String password;

	@ElementCollection
	@Fetch(FetchMode.JOIN)
	@Column(nullable = false)
	private Collection<Role> authorities;

	@Column(nullable = false)
	private boolean accountNonExpired;

	@Column(nullable = false)
	private boolean accountNonLocked;

	@Column(nullable = false)
	private boolean credentialsNonExpired;

	@Column(nullable = false)
	private boolean enabled;

	public ApplicationUser(final String username, final String password, final List<Role> authorities) {
		this.id = 0l;
		this.username = username;
		this.password = password;
		this.authorities = authorities;
		this.accountNonExpired = true;
		this.accountNonLocked = true;
		this.enabled = true;
		this.credentialsNonExpired = true;
	}
}