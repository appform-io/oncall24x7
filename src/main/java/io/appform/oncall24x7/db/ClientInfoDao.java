package io.appform.oncall24x7.db;

import io.dropwizard.hibernate.AbstractDAO;
import lombok.var;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Optional;

/**
 *
 */
public class ClientInfoDao extends AbstractDAO<ClientInfo> {

    /**
     * Creates a new DAO with a given session provider.
     *
     * @param sessionFactory a session provider
     */
    public ClientInfoDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<ClientInfo> save(
            final String teamId,
            final String botToken,
            final String webhook,
            final String botUserId,
            final String botOwnerUserId) {
        var info = currentInfo(teamId).orElse(null);
        if(null == info) {
            info = new ClientInfo();
            info.setTeamId(teamId);
        }
        info.setBotToken(botToken);
        info.setWebhook(webhook);
        info.setBotUserId(botUserId);
        info.setBotOwnerUserId(botOwnerUserId);
        return Optional.of(super.persist(info));
    }

    public Optional<ClientInfo> currentInfo(final String teamId) {
        return list(((Query<ClientInfo>)namedQuery("findClientInfo"))
                   .setParameter("teamId", teamId))
                .stream()
                .findAny();
    }
}
