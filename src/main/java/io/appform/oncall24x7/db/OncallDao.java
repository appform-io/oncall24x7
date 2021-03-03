package io.appform.oncall24x7.db;

import io.dropwizard.hibernate.AbstractDAO;
import lombok.var;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;

import java.util.Optional;

/**
 *
 */
public class OncallDao extends AbstractDAO<Oncall> {

    public OncallDao(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Optional<Oncall> set(final String domain, final String channel, final String current) {
        var oncall = findCurrent(domain, channel).orElse(null);
        if(null == oncall) {
            oncall = new Oncall(domain, channel, current);
        }
        else {
            oncall.setCurrent(current);
        }
        return Optional.of(super.persist(oncall));
    }

    public Optional<Oncall> findCurrent(final String teamId, final String channelId) {
        return list(
                (Query<Oncall>) namedQuery("findCurrentOncall")
                        .setParameter("teamId", teamId)
                        .setParameter("channelId", channelId))
                .stream()
                .findAny();
    }
}
