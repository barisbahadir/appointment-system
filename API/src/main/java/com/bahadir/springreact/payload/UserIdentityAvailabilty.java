package com.bahadir.springreact.payload;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserIdentityAvailabilty {

    private Boolean available;

    public UserIdentityAvailabilty(Boolean available) {
        this.available = available;
    }

}
