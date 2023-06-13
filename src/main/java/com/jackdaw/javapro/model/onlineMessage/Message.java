package com.jackdaw.javapro.model.onlineMessage;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//呃..
public class Message {
    private String fromName;
    private String toName;
    private String message;
    private int messageType;
}
