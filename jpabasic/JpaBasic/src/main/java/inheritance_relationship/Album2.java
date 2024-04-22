package inheritance_relationship;

import jakarta.persistence.Entity;

@Entity
public class Album2 extends Item2 {

    private String director;

    private String actor;


    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getActor() {
        return actor;
    }

    public void setActor(String actor) {
        this.actor = actor;
    }
}
