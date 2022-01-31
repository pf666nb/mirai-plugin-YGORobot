package com.happysnaker.exception;

/**
 * @author Happysnaker
 * @description
 * @date 2022/1/29
 * @email happysnaker@foxmail.com
 */
public class InsufficientPermissionsException extends Exception {
    public InsufficientPermissionsException(String message) {
        super(message);
    }
}
