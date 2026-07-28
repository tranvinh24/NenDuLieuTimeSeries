package org.NenDuLieuTimeSeries.model;

public class UserResponse {

    private int user;

    public UserResponse() {
    }

    public UserResponse(int user) {
        this.user = user;
    }

    public int getUser() {
        return user;
    }

    public void setUser(int user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "UserResponse{" +
                "user=" + user +
                '}';
    }
}