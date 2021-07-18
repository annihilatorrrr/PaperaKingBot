package it.stredox02.duckbot.mongodb;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MongoConnection {

    private final String server;
    private final int port;
    private final String username;
    private final String password;
    @Getter
    private MongoClient mongoClient;

    public void init() {
        mongoClient = MongoClients.create(new ConnectionString("mongodb://" + username + ":" + password + "@" + server + ":" + port));
    }

}
