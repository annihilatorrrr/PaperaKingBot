package it.stredox02.duckbot.database;

import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import it.stredox02.duckbot.Bot;
import it.stredox02.duckbot.mongodb.MongoConnection;
import it.stredox02.duckbot.object.GroupData;
import it.stredox02.duckbot.object.UserData;
import it.stredox02.duckbot.permissions.PermissionType;
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
    private MongoCollection<Document> usersCollection;
    @Getter
    private MongoCollection<Document> groupsCollection;

    public Database(Bot bot, MongoConnection mongoConnection) {
        this.bot = bot;
        this.database = mongoConnection.getMongoClient().getDatabase("duckkingbot");
        this.usersCollection = this.database.getCollection("users");
        this.groupsCollection = this.database.getCollection("groups");
    }

    @Override
    public void insertKing(String chatid, User user) {
        Document find = usersCollection.find(Filters.eq("chatid", chatid)).first();
        ZoneId zoneId = ZoneId.of("Europe/Rome");
        ZonedDateTime start = ZonedDateTime.ofInstant(Instant.now(), zoneId).toLocalDate().atStartOfDay(zoneId);
        ZonedDateTime tomorrow = start.plusDays(1);
        if (find != null) {
            usersCollection.updateOne(Filters.eq("chatid", chatid), new Document("$set", new Document("id", user.getId())
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
        usersCollection.insertOne(document);
    }

    @Override
    public List<UserData> getAllUsers() {
        return usersCollection.find().into(new ArrayList<>()).stream().map(document -> new UserData(document.getString("chatid"),
                document.getLong("id"),
                document.getString("username"),
                document.getString("firstname"),
                document.getString("lastname"),
                document.getLong("time"))).collect(Collectors.toList());
    }

    @Override
    public void removeKing(String chatid, long id) {
        Document document = usersCollection.find(Filters.and(Filters.eq("chatid", chatid), Filters.eq("id", id))).first();
        if (document == null) {
            return;
        }
        usersCollection.deleteOne(document);
    }

    @Override
    public void removeKing(String chatid) {
        Document document = usersCollection.find(Filters.eq("chatid", chatid)).first();
        if (document == null) {
            return;
        }
        usersCollection.deleteOne(document);
    }

    private void initGroup(String chatid){
        Document groupDocument = groupsCollection.find(Filters.eq("chatid", chatid)).first();
        if(groupDocument == null) {
            Document document = new Document("chatid", chatid);
            document.put("permissions", new ArrayList<>());
            document.put("reset_per_day", 0);
            document.put("today_reset", 0);
            groupsCollection.insertOne(document);
        }
    }

    @Override
    public void addPermissionToGroup(String chatid, PermissionType permission) {
        Document groupDocument = groupsCollection.find(Filters.eq("chatid", chatid)).first();
        if(groupDocument == null){
            return;
        }
        List<String> permissionsList = groupDocument.getList("permissions", String.class);
        if(permissionsList.contains(permission.name())){
            return;
        }
        permissionsList.add(permission.name());
        groupsCollection.updateOne(Filters.eq("chatid", chatid), new Document("$set",
                new Document("permissions", permissionsList)));
    }

    @Override
    public void removePermissionToGroup(String chatid, PermissionType permission) {
        Document groupDocument = groupsCollection.find(Filters.eq("chatid", chatid)).first();
        if(groupDocument == null){
            return;
        }
        List<String> permissionsList = groupDocument.getList("permissions", String.class);
        if(!permissionsList.contains(permission.name())){
            return;
        }
        permissionsList.remove(permission.name());
        groupsCollection.updateOne(Filters.eq("chatid", chatid), new Document("$set",
                new Document("permissions", permissionsList)));
    }

    @Override
    public List<PermissionType> getActualSettings(String chatid) {
        Document groupDocument = groupsCollection.find(Filters.eq("chatid", chatid)).first();
        if(groupDocument == null){
            initGroup(chatid);
            return new ArrayList<>();
        }
        return groupDocument.getList("permissions", String.class).stream()
                .map(PermissionType::valueOf).collect(Collectors.toList());
    }

    @Override
    public void updateResetPerDay(String chatid, int number) {
        Document groupDocument = groupsCollection.find(Filters.eq("chatid", chatid)).first();
        if(groupDocument == null) {
            Document document = new Document("chatid", chatid);
            document.put("reset_per_day", number);
            groupsCollection.insertOne(document);
        } else {
            groupsCollection.updateOne(Filters.eq("chatid", chatid), new Document("$set",
                    new Document("reset_per_day", number)));
        }
    }

    @Override
    public int getResetPerDay(String chatid) {
        Document groupDocument = groupsCollection.find(Filters.eq("chatid", chatid)).first();
        if(groupDocument == null){
            return -1;
        }
        return groupDocument.getInteger("reset_per_day");
    }

    @Override
    public void clearResetPerDay(String chatid) {
        int maxResetPerDay = getResetPerDay(chatid);
        groupsCollection.updateOne(Filters.eq("chatid", chatid), new Document("$set",
                new Document("today_reset", maxResetPerDay)));
    }

    @Override
    public void decreaseResetPerDay(String chatid) {
        Document groupDocument = groupsCollection.find(Filters.eq("chatid", chatid)).first();
        if(groupDocument == null){
            return;
        }
        int actualReset = getActualReset(chatid);
        actualReset--;
        groupsCollection.updateOne(Filters.eq("chatid", chatid), new Document("$set",
                new Document("today_reset", actualReset)));
    }

    @Override
    public int getActualReset(String chatid) {
        Document groupDocument = groupsCollection.find(Filters.eq("chatid", chatid)).first();
        if(groupDocument == null){
            return 0;
        }
        return groupDocument.getInteger("today_reset");
    }

    @Override
    public List<GroupData> getAllGroups() {
        return groupsCollection.find().into(new ArrayList<>()).stream().map(
                document -> new GroupData(document.getString("chatid"),
                document.getList("permissions", String.class).stream()
                        .map(PermissionType::valueOf).collect(Collectors.toList()),
                        document.getInteger("reset_per_day"),
                        document.getInteger("today_reset")))
                .collect(Collectors.toList());
    }

}
