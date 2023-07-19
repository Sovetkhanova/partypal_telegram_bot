package com.example.it32007telegram.models.dtos;


import com.google.gson.JsonObject;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Setter
@Data
@ToString
public class ApiResponse {

    private JsonObject json;
    private JsonObject headers;
    private int statusCode;

    public ApiResponse(JsonObject json, JsonObject headers, int statusCode) {
        this.json = json;
        this.headers = headers;
        this.statusCode = statusCode;
    }

    public ApiResponse(JsonObject headers, int statusCode) {
        this.headers = headers;
        this.statusCode = statusCode;
    }

    public JsonObject getJson() {
        return json;
    }

}
