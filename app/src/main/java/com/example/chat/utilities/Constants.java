package com.example.chat.utilities;

import java.util.HashMap;

public class Constants {
    public static final String KEY_COLLECTION_USERS= "users";
    public static final String KEY_NAME= "name";
    public static final String KEY_EMAIL= "email";
    public static final String KEY_PASSWORD= "password";
    public static final String KEY_PREFERENCE_NAME= "chatAppPreference";
    public static final String KEY_IS_SIGNED_IN= "isSignedIn";
    public static final String KEY_USER_ID= "userId";
    public static final String KEY_IMAGE= "image";
    public static final String KEY_FCM_TOKEN = "fcmToken";
    public static final String KEY_USER = "user";
    public static final String KEY_COLLECTION_CHAT = "chat";
    public static final String KEY_SENDER_ID = "senderId";
    public static final String KEY_RECEIVER_ID = "receiverId";
    public static final String KEY_MESSAGE = "message";
    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_COLLECTION_CONVERSION = "conversion";
    public static final String KEY_SENDER_NAME = "senderName";
    public static final String KEY_RECEIVER_NAME = "receiverName";
    public static final String KEY_SENDER_IMAGE = "senderImage";
    public static final String KEY_RECEIVER_IMAGE = "receiverImage";
    public static final String KEY_LAST_MESSAGE = "lastMessage";
    public static final String KEY_AVAILABILITY = "availability";
    public static final String REMOTE_MSG_AUTHORIZATION = "Authorization";
    public static final String REMOTE_MSG_CONTENT_TYPE = "Content-Type";
    public static final String REMOTE_MSG_DATA = "data";
    public static final String REMOTE_MSG_REGISTRATION_IDS = "registration-ids";


    public static HashMap<String, String > remoteMSGHeaders= null;
    public static HashMap<String, String> getRemoteMsgHeaders(){
        if (remoteMSGHeaders == null){
            remoteMSGHeaders= new HashMap<>();
            remoteMSGHeaders.put(
                    REMOTE_MSG_AUTHORIZATION,
                    "AAAAv_NGrYw:APA91bEaQSGd1JPiqB_KdKLkiJcog8pF5S5imOeDO_SebWHrzlAGqSctECkrSHcwnPLlUH9-03xW2NbdzJou2R4bn75acD2teh8_nwR7we0MegWN_oiR2nrIdDjvatcsys11WRYonFXP"
            );
            remoteMSGHeaders.put(
                    REMOTE_MSG_CONTENT_TYPE,
                    "application/json"
            );

        }
        return remoteMSGHeaders;
    }
}
