package com.villaekinoks.app.user;

import java.util.Collection;

import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.villaekinoks.app.generic.entity.DeleteStatus;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
public class AppUser implements UserDetails {

  @Id
  @UuidGenerator
  protected String id;

  @JsonProperty(access = Access.WRITE_ONLY)
  protected String login;

  @JsonProperty(access = Access.WRITE_ONLY)
  protected String password;

  protected Long lastreqtime;

  protected Boolean isonline;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  protected UserStatusSet statusset;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  protected AppUserPersonalInfo personalinfo;

  @Enumerated(EnumType.STRING)
  protected Role role;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  protected AppUserTimeStamps timestamps;

  @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
  protected AppUserLocaleSettings localesettings;

  @Enumerated(EnumType.STRING)
  @JsonIgnore
  protected DeleteStatus deletestatus;

  @Override
  @JsonIgnore
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return AuthorityUtils.createAuthorityList("ROLE_" + this.role.toString());
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.login;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return this.statusset.getOperationstatus() == OperationStatus.ENABLED;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return this.statusset.getLockstatus() == LockStatus.UNLOCKED;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return this.statusset.getVerificationstatus() == VerificationStatus.VERIFIED;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return this.statusset.getServicestatus() == ServiceStatus.ACTIVE;
  }

}
