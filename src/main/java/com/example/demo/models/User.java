package com.example.demo.models;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    private String id;

    private String name;
    private String email;
    @Enumerated(value = EnumType.STRING)
    private Role role;
    @ManyToMany(mappedBy = "votes", fetch = FetchType.EAGER)
    private Set<Initiative> votes;
    @OneToMany(mappedBy = "author", fetch = FetchType.EAGER)
    private Set<Initiative> initiatives = new HashSet<>();

    public Set<Initiative> getVotes() {
        return votes;
    }

    public void setVotes(Set<Initiative> votes) {
        this.votes = votes;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Set<Initiative> getInitiatives() {
        return initiatives;
    }

    public void setInitiatives(Set<Initiative> initiatives) {
        this.initiatives = initiatives;
    }

    public void addInitiative(Initiative initiative) {
        initiatives.add(initiative);
    }

    public boolean equals(User other){
        return this.id.equals(other.id);
    }
}
