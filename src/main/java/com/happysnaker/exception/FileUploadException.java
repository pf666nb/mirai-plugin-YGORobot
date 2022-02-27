package com.happysnaker.exception;

/**
 * 当资源上传腾讯服务器失败时抛出
 * @author Happysnaker
 * @description
 * @date 2022/1/19
 * @email happysnaker@foxmail.com
 */
public class FileUploadException extends Exception {
    public FileUploadException(String message) {
        super(message);
    }
}
