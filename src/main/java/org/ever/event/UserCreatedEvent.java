package org.ever.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserCreatedEvent {
    private String eventId;
    private String transactionId;
    private String customerUserId;
    private String userId;
    private String loginEmail;
    private String password;
    private boolean success;
}
