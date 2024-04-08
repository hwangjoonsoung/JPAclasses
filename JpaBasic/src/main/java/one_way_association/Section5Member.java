package one_way_association;

import jakarta.persistence.*;

@Entity
public class Section5Member {

    @Id
    @GeneratedValue
    private Long id;

    @Column(name = "username")
    private String name;

    /*    @Column(name = "team_id")
        private Long teamId;*/
    @ManyToOne
    @JoinColumn(name = "team_id")
    private Team team;

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

/*    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }*/

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
    }
}
