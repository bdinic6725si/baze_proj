import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoCollection;
import org.bson.Document;
import static com.mongodb.client.model.Filters.eq;

public class MongoConnection {
    private static final String URI = "mongodb://localhost:27017";
    private static final String DATABASE_NAME = "astronomija_mongo";
    private static final String COLLECTION_NAME = "eksperimenti";

    private static MongoClient mongoClient = null;

    static {
        System.setProperty("org.slf4j.simpleLogger.defaultLogLevel", "warn");
    }

    private static synchronized MongoClient getClient() {
        if (mongoClient == null) {
            mongoClient = MongoClients.create(URI);
        }
        return mongoClient;
    }

    public static Document pronadjiRezultat(int idIzvodjenja) {
        try {
            MongoDatabase database = getClient().getDatabase(DATABASE_NAME);
            MongoCollection<Document> collection = database.getCollection(COLLECTION_NAME);

            return collection.find(eq("izvodjenje_id", idIzvodjenja)).first();
        } catch (Exception e) {
            System.err.println("Greška pri čitanju iz MongoDB: " + e.getMessage());
            return null;
        }
    }

    public static void zatvoriKonekciju() {
        if (mongoClient != null) {
            mongoClient.close();
        }
    }
}