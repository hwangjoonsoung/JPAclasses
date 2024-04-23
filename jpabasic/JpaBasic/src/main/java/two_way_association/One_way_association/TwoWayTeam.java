package two_way_association.One_way_association;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
public class TwoWayTeam {

    @Id
    @GeneratedValue
    @Column(name = "team_id")
    private Long id;

    private String name;

    @OneToMany(mappedBy = "twoWayTeam")
    private List<TwoWaySection5Member> members = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public List<TwoWaySection5Member> getMembers() {
        return members;
    }

    public void setMembers(List<TwoWaySection5Member> members) {
        this.members = members;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void addMember(TwoWaySection5Member member) {
        member.setTeam(this);
        members.add(member);
    }



}
