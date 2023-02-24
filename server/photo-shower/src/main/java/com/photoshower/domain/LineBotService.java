package com.photoshower.domain;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.message.Message;

/**
 * LINE BOTのサービスです。
 */
@Service
public class LineBotService {

    @Autowired
    private LineMessagingClient lineMessagingClient;

    /**
     * 返信します。
     * @param replyToken トークン
     * @param message メッセージ
     */
    public void reply(String replyToken, Message message) {
        reply(replyToken, List.of(message));
    }

    /**
     * 返信します。
     * @param replyToken トークン
     * @param messages メッセージ一覧 (5件まで)
     */
    public void reply(String replyToken, List<Message> messages) {
        reply(replyToken, messages, true);
    }

    /**
     * 返信します。
     * @param replyToken トークン
     * @param messages メッセージ一覧 (5件まで)
     * @param isNotification プッシュ通知フラグ
     */
    public void reply(String replyToken, List<Message> messages, boolean isNotification) {

        try {
            lineMessagingClient
                    .replyMessage(new ReplyMessage(replyToken, messages, !isNotification))
                    .get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }
}
