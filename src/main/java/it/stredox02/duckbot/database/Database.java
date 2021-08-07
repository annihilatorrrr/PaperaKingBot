package it.stredox02.duckbot.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import it.stredox02.duckbot.Bot;
import it.stredox02.duckbot.mongodb.MongoConnection;
import it.stredox02.duckbot.object.UserData;
import lombok.Getter;
import org.bson.Document;
import org.telegram.telegrambots.meta.api.objects.User;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Database implements DatabaseHandler {

    private Bot bot;
    @Getter
    private MongoDatabase database;
    @Getter
    private MongoCollection<Document> collection;

    public Database(Bot bot, MongoConnection mongoConnection) {
        this.bot = bot;
        this.database = mongoConnection.getMongoClient().getDatabase("duckkingbot");
        this.collection = this.database.getCollection("users");
    }

    @Override
    public void insertKing(String chatid, User user) {
        Document find = collection.find(Filters.eq("chatid", chatid)).first();
        ZoneId zoneId = ZoneId.of("Europe/Rome");
        ZonedDateTime start = ZonedDateTime.ofInstant(Instant.now(), zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime tomorrow = start.plusDays(1);
        if (find != null) {
            collection.updateOne(Filters.eq("chatid", chatid), new Document("$set", new Document("id", user.getId())
                    .append("username", user.getUserName())
                    .append("firstname", user.getFirstName())
                    .append("lastname", user.getLastName())
                    .append("time", tomorrow.toEpochSecond())));
            return;
        }
        Document document = new Document("chatid", chatid);
        document.put("id", user.getId());
        document.put("username", user.getUserName());
        document.put("firstname", user.getFirstName());
        document.put("lastname", user.getLastName());
        document.put("time", tomorrow.toEpochSecond());
        collection.insertOne(document);
    }

    @Override
    public List<UserData> getAllUsers() {
        return collection.find().into(new ArrayList<>()).stream().map(document -> new UserData(document.getString("chatid"),
                document.getLong("id"),
                document.getString("username"),
                document.getString("firstname"),
                document.getString("lastname"),
                document.getLong("time"))).collect(Collectors.toList());
    }

    @Override
    public void removeKing(String chatid, long id) {
        Document document = collection.find(Filters.and(Filters.eq("chatid", chatid), Filters.eq("id", id))).first();
        if (document == null) {
            return;
        }
        collection.deleteOne(document);
    }

    @Override
    public void removeKing(String chatid) {
        Document document = collection.find(Filters.eq("chatid", chatid)).first();
        if (document == null) {
            return;
        }
        collection.deleteOne(document);
    }

}
