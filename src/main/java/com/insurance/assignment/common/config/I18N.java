package com.insurance.assignment.common.config;

import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Component;

import java.util.Locale;

@NoArgsConstructor
@Component
public class I18N {

    private static MessageSource messageSourceStatic;

    public static String getMessage(String key, Object... args) {
        if (messageSourceStatic == null) {
            return key;
        }

        Locale currentLocale = LocaleContextHolder.getLocale();

        try {
            return messageSourceStatic.getMessage(key, args, currentLocale);
        } catch (NoSuchMessageException e) {
            return key;
        } catch (Exception e) {
            return "!!!" + key + "!!!";
        }
    }

    @RequiredArgsConstructor
    @Component
    private static class MessageSourceInjector {

        private final MessageSource messageSource;

        @PostConstruct
        public void postConstruct() {
            I18N.messageSourceStatic = messageSource;

        }
    }
}
