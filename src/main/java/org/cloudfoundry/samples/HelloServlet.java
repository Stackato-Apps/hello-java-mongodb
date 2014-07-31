package org.cloudfoundry.samples;

import com.mongodb.*;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/plain");
		response.setStatus(200);
		PrintWriter writer = response.getWriter();
		writer.println("Hello from " + System.getenv("VCAP_APP_HOST") + ":" + System.getenv("VCAP_APP_PORT"));

        String mongodbUrl = System.getenv("MONGODB_URL");
        writer.println("mongodb URL = " + mongodbUrl);

        writer.println();

        String protocol = mongodbUrl.split("://")[0];
        String rest = mongodbUrl.split("://")[1];
        String username = rest.split("@")[0].split(":")[0];
        String password = rest.split("@")[0].split(":")[1];
        String host = rest.split("@")[1].split(":")[0];
        int port = Integer.parseInt(rest.split("@")[1].split(":")[1].split("/")[0]);
        String database = rest.split("@")[1].split(":")[1].split("/")[1];

        writer.println("protocol = " + protocol);
        writer.println("username = " + username);
        writer.println("password = " + password);
        writer.println("host = " + host);
        writer.println("port = " + port);
        writer.println("database = " + database);

        writer.println();

        ServerAddress address = new ServerAddress(host, port);
        MongoCredential credential = MongoCredential.createMongoCRCredential(username, database, password.toCharArray());
        MongoClient mongoClient = new MongoClient(address, Arrays.asList(credential));
        DB db = mongoClient.getDB(database);

        BasicDBObject dbObject1 = new BasicDBObject("test", true);
        dbObject1.append("appended", 10);

        BasicDBObject dbObject2 = new BasicDBObject("test2", false);
        dbObject1.append("appended2", "hello");

        // Write in the database
        DBCollection coll1 = db.getCollection("testCollection1");
        DBCollection coll2 = db.getCollection("testCollection2");
        coll1.insert(dbObject1);
        coll2.insert(dbObject2);

        // Read from the database
        Set<String> colls = db.getCollectionNames();
        for (String s : colls) {
            writer.println("Collection: " + s);
            DBCollection coll = db.getCollection(s);
            DBCursor cursor = coll.find();
            try {
                while(cursor.hasNext()) {
                    writer.println(cursor.next());
                }
            } finally {
                cursor.close();
            }
        }

        writer.println();
        writer.println("Done.");
		writer.close();
    }

}
