package io.appform.oncall24x7.events;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.oncall24x7.db.ClientInfoDao;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.client.Client;

/**
 *
 */
@Slf4j
public class SlackcallEventVisitor implements EventVisitor<Void> {

    private final Client client;
    private final ObjectMapper mapper;
    private final ClientInfoDao clientInfoDao;

    public SlackcallEventVisitor(
            @NonNull Client client,
            @NonNull ObjectMapper mapper,
            ClientInfoDao clientInfoDao) {
        this.client = client;
        this.mapper = mapper;
        this.clientInfoDao = clientInfoDao;
    }

    @Override
    @SneakyThrows
    public Void visit(AuthCodeReceivedEvent authCodeReceivedEvent) {

        return null;
    }
}
