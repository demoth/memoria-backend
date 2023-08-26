package org.dnj.memoria

import com.pengrad.telegrambot.TelegramBot
import com.pengrad.telegrambot.TelegramException
import com.pengrad.telegrambot.UpdatesListener
import com.pengrad.telegrambot.model.Update
import com.pengrad.telegrambot.request.SendMessage
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component


fun interface MessageSender {
    fun sendUpdate(message: String)
}
// todo: make tg bot an optional component
@Component
class MemoriaTgBot: MessageSender {
    private val logger = getLogger(MemoriaTgBot::class.java)!!
    // such initialization is not ideal but simple
    private val bot: TelegramBot = TelegramBot(System.getenv("TG_BOT_TOKEN")!!)
    private val allowedChatId = System.getenv("TG_BOT_ALLOWED_CHAT_ID").toLong()

    init {
        bot.setUpdatesListener(::processUpdates, ::onException)
        logger.info("Telegram bot is initialized")
    }

    override fun sendUpdate(message: String) {
        try {
            bot.execute(SendMessage(allowedChatId, message))
        } catch (e: Exception) {
            logger.error("Exception during sendUpdate:", e)
        }
    }

    private fun processUpdates(updates: MutableList<Update>): Int {
        updates.forEach {
            try {
                val chatId: Long = it.message()?.chat()?.id() ?: return UpdatesListener.CONFIRMED_UPDATES_ALL
                if (chatId != allowedChatId) {
                    bot.execute(SendMessage(chatId, "Not authorized! This incident will be reported!"))
                    return UpdatesListener.CONFIRMED_UPDATES_ALL
                }

                sendUpdate("No commands are implemented yet :|")
            } catch (e: Exception) {
                logger.error("Exception during processing message update: $it")
            }

        }
        return UpdatesListener.CONFIRMED_UPDATES_ALL
    }

    private fun onException(e: TelegramException) {
        if (e.response() != null) {
            // got bad response from telegram
            logger.warn("Got error response from server: ${e.response().errorCode()} ${e.response().description()}")
        } else {
            // probably network error
            logger.error("TelegramException", e)
        }
    }
}