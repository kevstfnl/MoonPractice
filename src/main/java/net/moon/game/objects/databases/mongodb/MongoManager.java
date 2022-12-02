package net.moon.game.objects.databases.mongodb;

import com.google.common.collect.Sets;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientOptions;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.CreateCollectionOptions;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Indexes;
import lombok.Getter;
import net.moon.game.Practice;
import net.moon.game.configurations.DatabaseConfiguration;
import net.moon.game.objects.arenas.Arena;
import net.moon.game.objects.kits.Kit;
import net.moon.game.objects.leaderboards.Leaderboard;
import net.moon.game.objects.practice.lobby.Lobby;
import net.moon.game.objects.players.PlayerData;
import org.bson.Document;

import java.util.Arrays;

import static net.moon.game.listeners.constants.PracticeLogger.debug;

public class MongoManager {

    private final MongoDatabase mongoDatabase;
    @Getter private MongoClient mongoClient;

    @Getter private final MongoCollection<Document> practice;
    @Getter private final MongoCollection<Document> players;
    @Getter private final MongoCollection<Document> kits;
    @Getter private final MongoCollection<Document> arenas;
    @Getter private final MongoCollection<Document> leaderboards;

    /**
     * Setup the mongoDB Database.
     * @param instance instance of main
     */
    public MongoManager(final Practice instance) {
        debug("Init MongoDB...");
        final DatabaseConfiguration config = instance.getDatabaseConfiguration();

        final ServerAddress serverAddress = new ServerAddress(config.getMongoHost(), config.getMongoPort());
        final MongoClientOptions mongoClientOptions = MongoClientOptions.builder().build();

        if (config.isMongoAuthentication()) {
            final MongoCredential mongoCredential = MongoCredential.createCredential(
                    config.getMongoUsername(),
                    config.getMongoDatabase(),
                    config.getMongoPassword().toCharArray());

            this.mongoClient = new MongoClient(
                    serverAddress,
                    mongoCredential,
                    mongoClientOptions
            );

        } else {
            this.mongoClient = new MongoClient(serverAddress, mongoClientOptions);
        }
        this.mongoDatabase = this.mongoClient.getDatabase(config.getMongoName());

        this.practice = this.getCollection("practice");
        this.players = this.getCollection("players", "uuid", "name");
        this.kits = this.getCollection("kits", "name", "display-name");
        this.arenas = this.getCollection("arenas", "name", "display-name");
        this.leaderboards = this.getCollection("kit");
    }

    /**
     * Stop database instance.
     */
    public void stop() {
        if (this.mongoClient != null) {
            this.mongoClient.close();
            this.mongoClient = null;
        }
    }

    /**
     * Get collections.
     * @param name name of database-collection.
     * @param index value of database occurrences.
     * @return Collection of BSON Document.
     */
    public MongoCollection<Document> getCollection(final String name, final String... index) {
        if (!this.mongoDatabase.listCollectionNames().into(Sets.newHashSet()).contains(name)) { // Look if collection exist
            this.mongoDatabase.createCollection(name, new CreateCollectionOptions());  // Create if if not present
        }

        final MongoCollection<Document> collection = this.mongoDatabase.getCollection(name); // Get collection
        Arrays.stream(index).forEach(s -> collection.createIndex(Indexes.ascending(s))); // Add index

        return collection;
    }

    public void update(final Lobby lobby) {
        final Document document = this.practice.find().first();
        if (document != null) {
            this.practice.replaceOne(document, lobby.toDocument());
        } else {
            this.practice.insertOne(lobby.toDocument());
        }
    }
    public void reset(final Lobby lobby) {
        final Document document = this.practice.find().first();
        if (document != null) document.clear();
    }

    public void update(final PlayerData playerData) {
        final Document document = this.players.find(Filters.eq("uuid", playerData.getUuid())).limit(1).first();
        if (document != null) {
            this.players.replaceOne(document, playerData.toDocument());
        } else {
            this.players.insertOne(playerData.toDocument());
        }
    }
    public void reset(final PlayerData playerData) {
        final Document document = this.players.find(Filters.eq("uuid", playerData.getUuid())).limit(1).first();
        if (document != null) document.clear();
    }

    public void update(final Kit kit) {
        final Document document = this.kits.find(Filters.eq("name", kit.getName())).limit(1).first();
        if (document != null) {
            this.kits.replaceOne(document, kit.toDocument());
        } else {
            this.kits.insertOne(kit.toDocument());
        }
    }
    public void reset(final Kit kit) {
        final Document document = this.kits.find(Filters.eq("name", kit.getName())).limit(1).first();
        if (document != null) document.clear();
    }

    public void update(final Arena arena) {
        final Document document = this.arenas.find(Filters.eq("name", arena.getName())).limit(1).first();
        if (document != null) {
            this.arenas.replaceOne(document, arena.toDocument());
        } else {
            this.arenas.insertOne(arena.toDocument());
        }
    }
    public void reset(final Arena arena) {
        final Document document = this.arenas.find(Filters.eq("name", arena.getName())).limit(1).first();
        if (document != null) document.clear();
    }

    public void update(final Leaderboard leaderboard) {
        final Document document = this.leaderboards.find(Filters.eq("kit", leaderboard.getKit())).limit(1).first();
        if (document != null) {
            this.leaderboards.replaceOne(document, leaderboard.toDocument());
        } else {
            this.leaderboards.insertOne(leaderboard.toDocument());
        }
    }
    public void reset(final Leaderboard leaderboard) {
        final Document document = this.leaderboards.find(Filters.eq("name", leaderboard.getKit())).limit(1).first();
        if (document != null) document.clear();
    }
}
