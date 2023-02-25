package com.photoshower.service;

import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linecorp.bot.client.LineBlobClient;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.client.MessageContentResponse;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.ImageMessageContent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.profile.UserProfileResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.photoshower.domain.User;
import com.photoshower.repository.UserRepository;

/**
 * LINE BOTのサービスです。
 */
@LineMessageHandler
@Service
public class LineBotService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageService imageService;

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Autowired
    private LineBlobClient lineBlobClient;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {

        String userId = event.getSource().getUserId();
        saveUser(userId);

        reply(event.getReplyToken(), "表示させたい写真を送ってください！");
    }

    @EventMapping
    public void handleImageMessage(MessageEvent<ImageMessageContent> event) throws Exception {

        String userId = event.getSource().getUserId();
        saveUser(userId);

        ImageMessageContent message = event.getMessage();
        MessageContentResponse response = lineBlobClient.getMessageContent(message.getId()).get();

        try (InputStream is = response.getStream()) {
            imageService.saveImage(userId, is);
        }

        reply(event.getReplyToken(), "写真を登録しました！");
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("Get event");
    }

    private void saveUser(String userId) throws InterruptedException, ExecutionException {

        User user = userRepository.select(userId);

        if (user == null) {
            UserProfileResponse userProfileResponse = lineMessagingClient.getProfile(userId).get();
            userRepository.insert(
                    User.builder()
                            .userId(userId)
                            .name(userProfileResponse.getDisplayName())
                            .build());
        }
    }

    private void reply(String replyToken, String message) {
        reply(replyToken, List.of(new TextMessage(message)));
    }

    private void reply(String replyToken, List<Message> messages) {
        reply(replyToken, messages, true);
    }

    private void reply(String replyToken, List<Message> messages, boolean isNotification) {

        try {
            lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messages, !isNotification))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
