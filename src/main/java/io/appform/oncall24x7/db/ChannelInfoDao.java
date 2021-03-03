package io.appform.oncall24x7.db;

import io.dropwizard.hibernate.AbstractDAO;
import lombok.var;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Optional;

/**
 *
 */
public class ChannelInfoDao extends AbstractDAO<ChannelInfo> {

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public ChannelInfoDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<ChannelInfo> save(
            final String teamId,
            final String channelId,
            final String botToken,
            final String webhook,
            final String botUserId,
            final String botOwnerUserId) {
        var info = currentInfo(teamId, channelId).orElse(null);
        if(null == info) {
            info = new ChannelInfo(teamId, channelId, botToken, webhook, botUserId, botOwnerUserId);
        }
        else {
            info.setBotToken(botToken);
            info.setWebhook(webhook);
            info.setBotUserId(botUserId);
            info.setBotOwnerUserId(botOwnerUserId);
        }
        return Optional.of(super.persist(info));
    }

    public Optional<ChannelInfo> currentInfo(final String teamId, final String channelId) {
        return list(((Query<ChannelInfo>)namedQuery("findChannelInfo"))
                   .setParameter("teamId", teamId)
                   .setParameter("channelId", channelId))
                .stream()
                .findAny();
    }
}
