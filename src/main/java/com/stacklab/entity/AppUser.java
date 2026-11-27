package com.stacklab.entity;
import jakarta.persistence.*;
@Entity @Table(name="users")
public class AppUser extends AuditedEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=120) private String name;
 @Column(nullable=false,unique=true,length=190) private String email;
 @Column(name="password_hash",nullable=false) private String passwordHash;
 @Enumerated(EnumType.STRING) @Column(nullable=false,length=20) private Role role;
 @Column(nullable=false) private boolean active=true;
 protected AppUser(){}
 public AppUser(String name,String email,String passwordHash,Role role){this.name=name;this.email=email.toLowerCase();this.passwordHash=passwordHash;this.role=role;}
 public Long getId(){return id;} public String getName(){return name;} public String getEmail(){return email;} public String getPasswordHash(){return passwordHash;} public Role getRole(){return role;} public boolean isActive(){return active;}
 public void update(String name,String email,Role role,Boolean active){if(name!=null)this.name=name;if(email!=null)this.email=email.toLowerCase();if(role!=null)this.role=role;if(active!=null)this.active=active;}
 public void deactivate(){active=false;}
}
