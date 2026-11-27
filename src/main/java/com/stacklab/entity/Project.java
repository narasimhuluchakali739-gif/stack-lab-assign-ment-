package com.stacklab.entity;
import jakarta.persistence.*;
@Entity @Table(name="projects")
public class Project extends AuditedEntity {
 @Id @GeneratedValue(strategy=GenerationType.IDENTITY) private Long id;
 @Column(nullable=false,length=160) private String name;
 @Column(length=2000) private String description;
 @ManyToOne(fetch=FetchType.LAZY) @JoinColumn(name="manager_id") private AppUser manager;
 @Column(nullable=false) private boolean archived=false;
 protected Project(){}
 public Project(String name,String description,AppUser manager){this.name=name;this.description=description;this.manager=manager;}
 public Long getId(){return id;} public String getName(){return name;} public String getDescription(){return description;} public AppUser getManager(){return manager;} public boolean isArchived(){return archived;}
 public void update(String name,String description,AppUser manager,Boolean archived){if(name!=null)this.name=name;if(description!=null)this.description=description;if(manager!=null)this.manager=manager;if(archived!=null)this.archived=archived;}
}
