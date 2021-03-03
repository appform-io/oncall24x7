package io.appform.oncall24x7.events;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.appform.oncall24x7.db.ChannelInfoDao;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpStatus;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Variant;

/**
 *
 */
@Slf4j
public class SlackcallEventVisitor implements EventVisitor<Void> {

    private final Client client;
    private final ObjectMapper mapper;
    private final ChannelInfoDao channelInfoDao;

    public SlackcallEventVisitor(
            @NonNull Client client,
            @NonNull ObjectMapper mapper,
            ChannelInfoDao channelInfoDao) {
        this.client = client;
        this.mapper = mapper;
        this.channelInfoDao = channelInfoDao;
    }

    @Override
    @SneakyThrows
    public Void visit(AuthCodeReceivedEvent authCodeReceivedEvent) {

        return null;
    }
}
