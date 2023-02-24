package com.photoshower.service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import com.photoshower.domain.Image;
import com.photoshower.domain.User;
import com.photoshower.repository.ImageRepository;
import com.photoshower.repository.UserRepository;

/**
 * LINE BOTのサービスです。
 */
@LineMessageHandler
@Service
public class LineBotService {

    @Value("${photoshower.image-output-path}")
    private String outputPath;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @Autowired
    private LineBlobClient lineBlobClient;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        reply(event.getReplyToken(), "表示させたい写真を送ってください！");
    }

    // TODO @Transactional 付けたらLine側でエラーになった
    @EventMapping
    public void handleImageMessage(MessageEvent<ImageMessageContent> event) throws Exception {

        String userId = userIdFrom(event);

        // TODO Returningでクラスごと返したらなぜかnullになった
        int imageId = imageRepository.insert(
                Image.builder()
                        .userId(userId)
                        .build());

        ImageMessageContent message = event.getMessage();
        MessageContentResponse response = lineBlobClient.getMessageContent(message.getId()).get();

        Path output = Paths.get(outputPath);
        if (!Files.exists(output)) {
            Files.createDirectory(output);
        }
        try (InputStream is = response.getStream()) {
            Path file = Files.createFile(output.resolve(String.valueOf(imageId)));
            Files.copy(is, file, StandardCopyOption.REPLACE_EXISTING);
        }

        reply(event.getReplyToken(), "写真を登録しました！");
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("Get event");
    }

    private String userIdFrom(Event event) throws InterruptedException, ExecutionException {

        String userId = event.getSource().getUserId();
        User user = userRepository.select(userId);

        if (user == null) {
            UserProfileResponse userProfileResponse = lineMessagingClient.getProfile(userId).get();
            userRepository.insert(
                    User.builder()
                            .userId(userId)
                            .name(userProfileResponse.getDisplayName())
                            .build());
        }

        return userId;
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
