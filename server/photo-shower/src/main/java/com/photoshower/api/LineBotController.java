package com.photoshower.api;

import org.springframework.beans.factory.annotation.Autowired;

import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;
import com.photoshower.domain.LineBotService;

/**
 * LINE BOTのコントローラです。
 */
@LineMessageHandler
public class LineBotController {

    @Autowired
    private LineBotService lineBotService;

    @EventMapping
    public void handleTextMessageEvent(MessageEvent<TextMessageContent> event) throws Exception {
        Message message = new TextMessage("テストです");
        lineBotService.reply(event.getReplyToken(), message);
    }

    @EventMapping
    public void handleDefaultMessageEvent(Event event) {
        System.out.println("Get event");
    }
}
