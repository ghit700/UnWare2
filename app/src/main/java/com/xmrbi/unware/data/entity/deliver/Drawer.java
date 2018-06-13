package com.xmrbi.unware.data.entity.deliver;

/**
 * 货架
 * Created by wzn on 2018/4/28.
 */

public class Drawer {
    private String name;
    private Long id;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
