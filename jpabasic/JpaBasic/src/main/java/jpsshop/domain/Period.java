package jpsshop.domain;

import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

@Embeddable
public class Period {

    private LocalDateTime started_at;
    private LocalDateTime ended_at;

    public Period() {
    }

    public Period(LocalDateTime started_at, LocalDateTime ended_at) {
        this.started_at = started_at;
        this.ended_at = ended_at;
    }

    public LocalDateTime getStarted_at() {
        return started_at;
    }

    public void setStarted_at(LocalDateTime started_at) {
        this.started_at = started_at;
    }

    public LocalDateTime getEnded_at() {
        return ended_at;
    }

    public void setEnded_at(LocalDateTime ended_at) {
        this.ended_at = ended_at;
    }
}
