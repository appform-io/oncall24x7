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
@Table(name = "client_info")
@NamedQueries({
        @NamedQuery(
                name = "findClientInfo",
                query = "from ClientInfo c where c.teamId = :teamId"
        )
})
@Data
@NoArgsConstructor
public class ClientInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "team_id", nullable = false, unique = true)
    private String teamId;

    @Column(name = "bot_token", nullable = false)
    private String botToken;

    @Column(name = "webhook", nullable = false)
    private String webhook;

    @Column(name = "bot_user_id")
    private String botUserId;

    @Column(name = "bot_owner_user_id")
    private String botOwnerUserId;

    @Column(name = "created", columnDefinition = "timestamp", updatable = false, insertable = false)
    @Generated(value = GenerationTime.INSERT)
    private Date created;

    @Column(name = "updated", columnDefinition = "timestamp default current_timestamp",
            updatable = false, insertable = false)
    @Generated(value = GenerationTime.ALWAYS)
    private Date updated;

    public ClientInfo(
            String teamId,
            String botToken,
            String webhook,
            String botUserId,
            String botOwnerUserId) {
        this.teamId = teamId;
        this.botToken = botToken;
        this.webhook = webhook;
        this.botUserId = botUserId;
        this.botOwnerUserId = botOwnerUserId;
    }
}
