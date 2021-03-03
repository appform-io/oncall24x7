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
@Table(name = "channel_info",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_channel_info", columnNames = {"team_id", "channel_id"})
        })
@NamedQueries({
        @NamedQuery(
                name = "findChannelInfo",
                query = "from ChannelInfo c where c.teamId = :teamId and c.channelId = :channelId"
        )
})
@Data
@NoArgsConstructor
public class ChannelInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "team_id", nullable = false)
    private String teamId;

    @Column(name = "channel_id", nullable = false)
    private String channelId;

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

    public ChannelInfo(
            String teamId,
            String channelId,
            String botToken,
            String webhook,
            String botUserId,
            String botOwnerUserId) {
        this.teamId = teamId;
        this.channelId = channelId;
        this.botToken = botToken;
        this.webhook = webhook;
        this.botUserId = botUserId;
        this.botOwnerUserId = botOwnerUserId;
    }
}
