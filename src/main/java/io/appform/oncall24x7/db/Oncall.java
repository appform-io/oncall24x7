package io.appform.oncall24x7.db;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;

import javax.persistence.*;
import java.util.Date;

/**
 *
 */
@Entity
@Table(name = "oncall",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_oncall_lookup", columnNames = {"team_id", "channel_id"})
        })
@NamedQueries({
        @NamedQuery(
                name = "findCurrentOncall",
                query = "from Oncall o where o.teamId = :teamId and o.channelId = :channelId"
        )
})
@Data
@NoArgsConstructor
public class Oncall {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "team_id")
    private String teamId;

    @Column(name = "channel_id")
    private String channelId;

    @Column(name = "current")
    private String current;

    @Column(name = "created", columnDefinition = "timestamp", updatable = false, insertable = false)
    @Generated(value = GenerationTime.INSERT)
    private Date created;

    @Column(name = "updated", columnDefinition = "timestamp default current_timestamp",
            updatable = false, insertable = false)
    @Generated(value = GenerationTime.ALWAYS)
    private Date updated;

    public Oncall(String teamId, String channelId, String current) {
        this.teamId = teamId;
        this.channelId = channelId;
        this.current = current;
    }
}
