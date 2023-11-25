package com.tariqkhan051.reviewrover.models.external;

import java.util.List;

import com.tariqkhan051.reviewrover.models.User;

public class GetUsersResponse {
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }
}
