package com.example.demo.models;


import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "initiatives")
public class Initiative {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;
    private String text;
    @Enumerated(value = EnumType.STRING)
    private InitiativeStatus status;
    private Integer cost;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinTable(name = "initiative_authors",
            joinColumns = @JoinColumn(name = "initiative_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private User author;
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "initiative_votes",
            joinColumns = @JoinColumn(name = "initiative_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private Set<User> votes = new HashSet<>();
    private String performerAddress;
    private int votesNeed;
    private boolean expertApproval;

    public Initiative() {
    }

    public Initiative(String name, String text, Integer cost, User author, String performerAddress) {
        this.name = name;
        this.text = text;
        this.cost = cost;
        this.setAuthor(author);
        this.performerAddress = performerAddress;
        this.status = InitiativeStatus.NEW;
        this.votesNeed = 100000;
        this.expertApproval = false;
    }

    public void addVote(User user){
        this.votes.add(user);
        user.getVotes().add(this);
    }
    public void removeVote(User user) throws Exception {
        try {
            this.votes.removeIf(x-> x.getId().equals(user.getId()));
            user.getVotes().removeIf(x->x.getId().equals(this.getId()));
        }
        catch (Exception e){
            throw new Exception("не получилось удалить голос");
        }
    }

    public boolean isApproved(){
        return this.getVotesCount() >= this.votesNeed || this.expertApproval;
    }

    public int getVotesCount(){
        return votes.size();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public InitiativeStatus getStatus() {
        return status;
    }

    public void setStatus(InitiativeStatus status) {
        this.status = status;
    }

    public Integer getCost() {
        return cost;
    }

    public void setCost(Integer cost) {
        this.cost = cost;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
        author.addInitiative(this);
    }

    public String getPerformerAddress() {
        return performerAddress;
    }

    public void setPerformerAddress(String performerAddress) {
        this.performerAddress = performerAddress;
    }

    public int getVotesNeed() {
        return votesNeed;
    }

    public void setVotesNeed(int votesNeed) {
        this.votesNeed = votesNeed;
    }

    public boolean isExpertApproval() {
        return expertApproval;
    }

    public void setExpertApproval(boolean expertApploval) {
        this.expertApproval = expertApploval;
    }
}
