package com.example.it32007telegram.models.dtos;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class PageParameters {

    private Integer page = 0;

    private Integer count = 10;

    @JsonSetter
    public void setPage(Integer page) {
        this.page = page == null || page < 0 ? 0 : page;
    }

    @JsonSetter
    public void setCount(Integer count) {
        this.count = count == null || count < 0 ? 10 : count;
    }
}
