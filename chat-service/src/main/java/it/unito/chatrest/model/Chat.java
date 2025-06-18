package it.unito.chatrest.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long ownerId;
    private Long adopterId;
    private Long adoptionPostId;

    private boolean requestFlag;
    private boolean acceptedFlag;

    public Long getId() {
        return id;
    }

    public Long getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(Long ownerId) {
        this.ownerId = ownerId;
    }

    public Long getAdopterId() {
        return adopterId;
    }

    public void setAdopterId(Long adopterId) {
        this.adopterId = adopterId;
    }

    public Long getAdoptionPostId() {
        return adoptionPostId;
    }

    public void setAdoptionPostId(Long adoptionPostId) {
        this.adoptionPostId = adoptionPostId;
    }

    public boolean isRequestFlag() {
        return requestFlag;
    }

    public void setRequestFlag(boolean propostaFlag) {
        this.requestFlag = propostaFlag;
    }

    public boolean isAcceptedFlag() {
        return acceptedFlag;
    }

    public void setAcceptedFlag(boolean accettataFlag) {
        this.acceptedFlag = accettataFlag;
    }
}