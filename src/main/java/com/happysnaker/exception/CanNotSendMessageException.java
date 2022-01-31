package com.happysnaker.exception;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/14
 * @email happysnaker@foxmail.com
 */
public class CanNotSendMessageException extends Exception {
    public CanNotSendMessageException(String message) {
        super(message);
    }
}
