package com.xmrbi.unware.event;

import com.xmrbi.unware.data.entity.main.User;

import java.util.List;

/**
 * 在库人员
 * Created by wzn on 2018/6/14.
 */
public class UserEvent {
    private List<User> lstUsers;

    public UserEvent(List<User> lstUsers) {
        this.lstUsers = lstUsers;
    }

    public List<User> getLstUsers() {
        return lstUsers;
    }

    public void setLstUsers(List<User> lstUsers) {
        this.lstUsers = lstUsers;
    }
}
