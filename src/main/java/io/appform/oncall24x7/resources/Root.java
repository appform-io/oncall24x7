package io.appform.oncall24x7.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import io.appform.oncall24x7.Message;
import io.appform.oncall24x7.SlackSecrets;
import io.appform.oncall24x7.db.ChannelInfo;
import io.appform.oncall24x7.db.ChannelInfoDao;
import io.appform.oncall24x7.db.OncallDao;
import io.appform.oncall24x7.model.SlackMessage;
import io.appform.oncall24x7.model.SlackWebhookResponse;
import io.dropwizard.hibernate.UnitOfWork;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.http.HttpStatus;
import org.apache.http.client.utils.URIBuilder;
import org.hibernate.validator.constraints.NotEmpty;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.*;
import java.net.URI;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutorService;

/**
 *
 */
@Path("/")
@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class Root {
    private final ObjectMapper mapper;
    private final ChannelInfoDao channelInfoDao;
    private final OncallDao oncallDao;
    private final Client httpClient;
    private final ExecutorService executorService;
    private final SlackSecrets slackSecrets;

    public Root(
            ObjectMapper mapper,
            ChannelInfoDao channelInfoDao,
            OncallDao oncallDao,
            Client httpClient, ExecutorService executorService, SlackSecrets slackSecrets) {
        this.mapper = mapper;
        this.channelInfoDao = channelInfoDao;
        this.oncallDao = oncallDao;
        this.httpClient = httpClient;
        this.executorService = executorService;
        this.slackSecrets = slackSecrets;
    }

    @POST
    public Response home(
            final MultivaluedMap<String, String>
                    formParameters) {
        val entity = mapper.valueToTree(formParameters);
        log.info("DATA: {}", entity);
        return Response.ok(entity).build();
    }

    @GET
    @SneakyThrows
    @UnitOfWork
    public Response register(@QueryParam("code") final String tempAuthCode) {
        if (!Strings.isNullOrEmpty(tempAuthCode)) {
            val appConfigUrl = saveChannelInfo(tempAuthCode).orElse(null);
            if (!Strings.isNullOrEmpty(appConfigUrl)) {
                return Response.status(Response.Status.FOUND)
                        .location(URI.create(appConfigUrl))
                        .build();
            }
            return Response.serverError().build();
        }
        val uri = new URIBuilder()
                .setScheme("https")
                .setHost("slack.com")
                .setPath("/oauth/authorize")
                .setParameter("client_id", slackSecrets.getClientId())
                .setParameter("scope", "commands,incoming-webhook,users.profile:read")
                .build();
        return Response.status(Response.Status.FOUND).location(uri).build();
    }
/*

    @GET
    @SneakyThrows
    @Path("/install")
    public Response redirectToAuth(@QueryParam("code") final String tempAuthCode) {
        eventBus.publish(new AuthCodeReceivedEvent(tempAuthCode));
        return Response.ok().build();
    }*/

    @POST
    @Path("/command")
    @UnitOfWork
    public SlackWebhookResponse set(
            @FormParam("team_id") @NotEmpty final String domain,
            @FormParam("channel_id") @NotEmpty final String channel,
            @FormParam("user_name") @NotEmpty final String userName,
            @FormParam("command") @NotEmpty final String command,
            @FormParam("text") final String commandText,
            @FormParam("response_url") @NotEmpty final String responseUrl) {
        if (Strings.isNullOrEmpty(commandText)) {
            return showExistingOncall(domain, channel);
        }
        val oncall = oncallDao.set(domain, channel, commandText).orElse(null);
        if (null == oncall) {
            log.error("Oncall could not be created");
            return null;
        }
        log.debug("Saved oncall. Responding: {}", responseUrl);
        return SlackWebhookResponse.builder()
                .text("<" + commandText + "> has been set as on-call")
                .responseType(SlackWebhookResponse.ResponseType.IN_CHANNEL)
                .build();
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/events")
    @UnitOfWork
    public Response events(final JsonNode event) {
        log.info("Event payload: {}", event);
        if (event.get("type").asText().equals("url_verification")) {
            return Response.ok(Collections.singletonMap("challenge", event.get("challenge").asText()))
                    .build();
        }
        if (event.get("type").asText().equals("event_callback")) {
            //This is a rich event
            val eventNode = event.at("/event");
            if ("app_mention" .equals(eventNode.get("type").asText())) {
                val teamId = eventNode.at("/team").asText();
                val channelId = eventNode.at("/channel").asText();
                val channelInfo = channelInfoDao.currentInfo(teamId, channelId).orElse(null);
                val sender = eventNode.at("/user").asText();
                if (null != channelInfo) {
                    val oncall = oncallDao.findCurrent(teamId, channelId).orElse(null);
                    if (oncall == null) {
                        sendMessage(channelInfo,
                                    Message.ONCALL_NOT_SET,
                                    ImmutableMap.of("sender", sender));
                    }
                    else {
                        val botUserId = channelInfo.getBotUserId();
                        val original = eventNode.at("/text")
                                .asText()
                                .replaceAll("<@" + botUserId + ">", "Oncall");
                        sendMessage(channelInfo,
                                    Message.BOT_MENTIONED,
                                    ImmutableMap.of("oncall", oncall.getCurrent(),
                                                    "sender", sender),
                                    original);
                        return Response.ok().build();
                    }
                }
                log.warn("Got mentioned but no info found for team: {} channel: {}", teamId, channelId);
            }
        }
        return Response.ok().build();
    }

    @SneakyThrows
    private Optional<String> saveChannelInfo(String accessCode) {
        Form form = new Form();

        form.param("code", accessCode);
        form.param("client_id", slackSecrets.getClientId());
        form.param("client_secret", slackSecrets.getClientSecret());
        log.debug("CID: {} CS: {}", slackSecrets.getClientId(), slackSecrets.getClientSecret());
        log.info("Parameters: {}", form.asMap());
        val response = httpClient.target("https://slack.com/api/oauth.v2.access")
                .request()
                .buildPost(Entity.entity(form, new Variant(MediaType.APPLICATION_FORM_URLENCODED_TYPE, "", "")))
                .invoke();
        if (response.getStatus() == HttpStatus.SC_OK) {
            val body = response.readEntity(String.class);
            log.debug("Received info: {}", body);
            final JsonNode jsonNode = mapper.readTree(body);
            val teamId = jsonNode.at("/team/id").asText();
            val channelId = jsonNode.at("/incoming_webhook/channel_id").asText();
            val botToken = jsonNode.at("/access_token").asText();
            val webhook = jsonNode.at("/incoming_webhook/url").asText();
            val botUserId = jsonNode.at("/bot_user_id").asText();
            val botOwnerUserId = jsonNode.at("/authed_user/id").asText();
            val savedChannelInfo = channelInfoDao.save(teamId, channelId, botToken, webhook, botUserId, botOwnerUserId);
            log.info("Channel infor save status for team: {}, channel: {} is {}",
                     teamId, channelId, savedChannelInfo.isPresent());
            if (savedChannelInfo.isPresent()) {
                sendMessage(savedChannelInfo.get(),
                            Message.BOT_ADDED_MESSAGE,
                            Collections.singletonMap("owner", botOwnerUserId));
                return Optional.ofNullable(jsonNode.at("/incoming_webhook/configuration_url").asText());
            }
        }
        return Optional.empty();
    }

    private SlackWebhookResponse showExistingOncall(String domain, String channel) {
        val currOncall = oncallDao.findCurrent(domain, channel).orElse(null);
        if (null == currOncall) {
            return SlackWebhookResponse.builder()
                    .responseType(SlackWebhookResponse.ResponseType.EPHEMERAL)
                    .text("No oncall is set. Please use /oncall <username> to set oncall")

                    .build();
        }
        return SlackWebhookResponse.builder()
                .responseType(SlackWebhookResponse.ResponseType.EPHEMERAL)
                .text(String.format("Current on-call for this channel is <%s>", currOncall.getCurrent()))
                .build();
    }

    private boolean sendMessage(final ChannelInfo channelInfo, Message message, Map<String, Object> context) {
        return sendMessage(channelInfo, message, context, null);
    }

    private boolean sendMessage(
            final ChannelInfo channelInfo,
            Message message,
            Map<String, Object> context,
            String quote) {
        executorService.submit(() -> {
            try {
                val webhook = channelInfo.getWebhook();
                final JsonNode markdown = Message.markdown(message, context, mapper, quote);
                final SlackMessage slackMessage = SlackMessage.builder()
                        .text(Message.text(message, context))
                        .blocks(markdown)
                        .build();
                log.debug("Node: {}", mapper.valueToTree(slackMessage));
                val response = httpClient.target(webhook)
                        .request()
                        .buildPost(Entity.entity(slackMessage,
                                                 new Variant(MediaType.APPLICATION_JSON_TYPE, "", "")))
                        .invoke();
                log.info("Slack call to {} response: {}", webhook, response.getStatusInfo());
            }
            catch (Throwable t) {
                log.error("Error sending message: ", t);
            }
            return null;
        });
        return true;
    }
}
