package project;


import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.bson.Document;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gte;

public class Client {

	private MongoDatabase database;
	private MongoClient mongoClient;
	private String dbName = "airbase";

	private String ClientCollectionName = "clients";

    private MongoCollection<Document> collection;


	public static void main(String args[]) throws FileNotFoundException {
		try {
			
			Client client = new Client();
			
			
			
			// pour chercher selon la ville (Taper la ville souhaiter)
			client.findByTown("Paris");
			
			//client.loadManyClientsFromJsonFile("src/main/resources/Airbase.json");
			//client.loadOneClientFromJsonFile("src/main/resources/Airbase.json");
			client.mongoClient.close();
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	/**
	 Constructeur Client.
	 Dans ce constructeur sont effectu�es les activit�s suivantes:
	 - Cr�ation d'une instance du client MongoClient
	 - Cr�ation d'une BD Mongo appel� RH
	 - Cr�ation d'un utilisateur appel�
	 - Chargement du pointeur vers la base RH
	 */
	public Client() {

		String mongodbUri = "mongodb://test:test@cluster0-shard-00-00-d9c8u.mongodb.net:27017,cluster0-shard-00-01-d9c8u.mongodb.net:27017,cluster0-shard-00-02-d9c8u.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true&w=majority";


		mongoClient = MongoClients.create(mongodbUri);
		

		try {
			Logger.getLogger("org.mongodb.driver").setLevel(Level.WARNING);
			mongoClient = MongoClients.create(mongodbUri);
			List<Document> databases = mongoClient.listDatabases().into(new ArrayList());
			System.out.println(" ******* liste des BDs : *******");
			for (Document db : databases) {
				System.out.println(db.toJson());
			}

			database = mongoClient.getDatabase("airbase");
			System.out.println("******* liste des collections *******");
			for (String coll : database.listCollectionNames()) {
				System.out.println(coll);
			}

			this.collection = database.getCollection("clients");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}



	/**
	 Cette fonction permet de cr�er une collection
	 de nom nomCollection.
	 */
	public void createCollectionClient(String nomCollection) {
		//Creating a collection
		database.createCollection(nomCollection);
		System.out.println("Collection Clients created successfully");

	}

	/**
	 Cette fonction permet de supprimer une collection
	 de nom nomCollection.
	 */

	public void dropCollectionClient(String nomCollection) {
		//Drop a collection
		MongoCollection<Document> colClients = null;
		System.out.println("\n\n\n*********** dans dropCollectionClient *****************");

		System.out.println("!!!! Collection Client : " + colClients);

		colClients = database.getCollection(nomCollection);
		System.out.println("!!!! Collection Client : " + colClients);
		// Visiblement jamais !!!
		if (colClients == null)
			System.out.println("Collection inexistante");
		else {
			colClients.drop();
			System.out.println("Collection colClients removed successfully !!!");

		}
	}

	/**
	 Cette fonction permet d'ins�rer un Departement dans une collection.
	 */

	public void insertOneClient(String nomCollection, Document client) {
		//Drop a collection
		MongoCollection<Document> colClients = database.getCollection(nomCollection);
		colClients.insertOne(client);
		System.out.println("Document inserted successfully");
	}

	/**
	 Cette fonction permet de tester la m�thode insertOneClient.
	 */

	public void testInsertOneClient() {

		Document client = new Document("_id", "07").append("nom", "Bond").append("prenom", Arrays.asList("James"))
				.append("telephone", "673212293").append("DateNaiss", "03/02/1990")
				.append("adresse", new Document("numero", 20).append("rue", "queens avenue")
						.append("codePostal", "EC4R 2SU").append("ville", "Londres").append("pays", "Royaune Uni"));
		this.insertOneClient(this.ClientCollectionName, client);
		System.out.println("Document inserted successfully");
	}

	/**
	 Cette fonction permet d'ins�rer plusieurs D�partements dans une collection
	 */

	public void insertManyClients(String nomCollection, List<Document> clients) {
		//Drop a collection
		MongoCollection<Document> colClients = database.getCollection(nomCollection);
		colClients.insertMany(clients);
		System.out.println("Many Documents inserted successfully");
	}

	/**
	 Cette fonction permet de tester la fonction insertManyClients
	 */

	public void testInsertManyClients() {
		List<Document> clients = Arrays.asList(
				new Document("_id", 1).append("nom", "Martin").append("prenom", Arrays.asList("Aaron", "Frida"))
						.append("telephone", "673212284").append("DateNaiss", "01/01/1980").append("adresse",
								new Document("numero", 11).append("rue", "All�e Cavendish")
										.append("codePostal", "06000").append("ville", "Nice")
										.append("pays", "France")),

				new Document("_id", 2).append("nom", "Bernard").append("prenom", Arrays.asList("Abel"))
						.append("telephone", "673212285").append("DateNaiss", "05/05/1984")
						.append("adresse", new Document("numero", 12).append("rue", "All�e de la Chapelle Saint-Pierre")
								.append("codePostal", "06000").append("ville", "Nice").append("pays", "France")),

				new Document("_id", 3).append("nom", "Dubois").append("prenom", Arrays.asList("Abella", "Mehdi"))
						.append("telephone", "673212286").append("DateNaiss", "02/02/1990")
						.append("adresse", new Document("numero", 13).append("rue", "Rue la Fontaine aux Oiseaux")
								.append("codePostal", "06000").append("ville", "Nice").append("pays", "France")),

				new Document("_id", 4).append("nom", "Thomas").append("prenom", Arrays.asList("Ab�lard"))
						.append("telephone", "673212287").append("DateNaiss", "01/06/1987")
						.append("adresse", new Document("numero", 14).append("rue", "Rue La Palmeraie")
								.append("codePostal", "France").append("ville", "Nice").append("pays", "France")),

				new Document("_id", 5).append("nom", "Walter").append("prenom", Arrays.asList("Robert"))
						.append("telephone", "673212288").append("DateNaiss", "01/08/1983")
						.append("adresse", new Document("numero", 15).append("rue", "Rue de la R�sistance")
								.append("codePostal", "10001").append("ville", "New-york").append("pays", "USA")),

				new Document("_id", 6).append("nom", "Richard").append("prenom", Arrays.asList("Maria", "Abondance"))
						.append("telephone", "673212289").append("DateNaiss", "12/01/1980")
						.append("adresse", new Document("numero", 16).append("rue", "All�e des Citronniers")
								.append("codePostal", "75001").append("ville", "Paris").append("pays", "France")),

				new Document("_id", 7).append("nom", "Petit").append("prenom", Arrays.asList("Abraham", "Leonard"))
						.append("telephone", "673212290").append("DateNaiss", "01/08/1980")
						.append("adresse", new Document("numero", 17).append("rue", "All�e des Faunes")
								.append("codePostal", "69001").append("ville", "Lyon").append("pays", "France")),

				new Document("_id", 8).append("nom", "Durand").append("prenom", Arrays.asList("Mari", "Achille"))
						.append("telephone", "673212291").append("DateNaiss", "01/09/1989")
						.append("adresse", new Document("numero", 18).append("rue", "Rue des Isnards")
								.append("codePostal", "75001").append("ville", "Paris").append("pays", "France")),

				new Document("_id", 9).append("nom", "Leroy").append("prenom", Arrays.asList("Ada", "Mousse"))
						.append("telephone", "673212292").append("DateNaiss", "28/07/1985")
						.append("adresse", new Document("numero", 19).append("rue", "Rue des Lucioles")
								.append("codePostal", "13001").append("ville", "Marseille").append("pays", "France")),

				new Document("_id", 10).append("nom", "Moreau").append("prenom", Arrays.asList("Adam"))
						.append("telephone", "673212293").append("DateNaiss", "03/02/1990")
						.append("adresse", new Document("numero", 20).append("rue", "All�e des Palmiers")
								.append("codePostal", "31000").append("ville", "Toulouse").append("pays", "France")));
		this.insertManyClients(this.ClientCollectionName, clients);
	}

	/**
	 Cette fonction permet de rechercher un d�partement dans une collection
	 connaissant son id.
	 */
	public void getClientById(String nomCollection, Integer ClientId) {
		//Drop a collection
		System.out.println("\n\n\n*********** dans getClientById *****************");

		MongoCollection<Document> colClients = database.getCollection(nomCollection);

		//BasicDBObject whereQuery = new BasicDBObject();
		Document whereQuery = new Document();

		whereQuery.put("_id", ClientId);
		//DBCursor cursor = colClients.find(whereQuery);
		FindIterable<Document> listClient = colClients.find(whereQuery);

		// Getting the iterator
		Iterator it = listClient.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}

	/**
	 Cette fonction permet de rechercher des d�partements dans une collection.
	 Le param�tre whereQuery : permet de passer des conditions de rechercher
	 Le param�tre projectionFields : permet d'indiquer les champs � afficher
	 Le param�tre sortFields : permet d'indiquer les champs de tri.
	 */
	public void getClients(String nomCollection, Document whereQuery, Document projectionFields, Document sortFields) {
		//Drop a collection
		System.out.println("\n\n\n*********** dans getClients *****************");

		MongoCollection<Document> colClients = database.getCollection(nomCollection);

		FindIterable<Document> listClient = colClients.find(whereQuery).sort(sortFields).projection(projectionFields);

		// Getting the iterator
		Iterator it = listClient.iterator();
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}

	/**
	 Cette fonction permet de modifier des d�partements dans une collection.
	 Le param�tre whereQuery : permet de passer des conditions de recherche
	 Le param�tre updateExpressions : permet d'indiquer les champs � modifier
	 Le param�tre UpdateOptions : permet d'indiquer les options de mise � jour :
	 .upSert : ins�re si le document n'existe pas
	 */
	public void updateClients(String nomCollection, Document whereQuery, Document updateExpressions,
			UpdateOptions updateOptions) {
		//Drop a collection
		System.out.println("\n\n\n*********** dans updateClients *****************");

		MongoCollection<Document> colClients = database.getCollection(nomCollection);
		UpdateResult updateResult = colClients.updateMany(whereQuery, updateExpressions);

		System.out.println("\nR�sultat update : " + "getUpdate id: " + updateResult + " getMatchedCount : "
				+ updateResult.getMatchedCount() + " getModifiedCount : " + updateResult.getModifiedCount());

		//return updateResult.getUpsertedId() != null ||
		//		(updateResult.getMatchedCount() > 0 && updateResult.getModifiedCount() > 0);
		//FindIterable<Document> listEmp=colClients.find(whereQuery).update(sortFields).projection(projectionFields);

		// Getting the iterator
		//Iterator it = listClient.iterator();
		//while(it.hasNext()) {
		//		System.out.println(it.next());
		//}
	}

	/**
	 Cette fonction permet de supprimer des d�partements dans une collection.
	 Le param�tre filters : permet de passer des conditions de recherche des employ�s � supprimer
	 */
	public void deleteClients(String nomCollection, Document filters) {

		System.out.println("\n\n\n*********** dans deleteClients *****************");
		FindIterable<Document> listClient;
		Iterator it;
		MongoCollection<Document> colClients = database.getCollection(nomCollection);

		listClient = colClients.find(filters).sort(new Document("_id", 1));
		it = listClient.iterator();// Getting the iterator
		this.displayIterator(it, "Dans deleteClients: avant suppression");

		colClients.deleteMany(filters);
		listClient = colClients.find(filters).sort(new Document("_id", 1));
		it = listClient.iterator();// Getting the iterator
		this.displayIterator(it, "Dans deleteClients: Apres suppression");
	}

	/**
	 Parcours un it�rateur et affiche les documents qui s'y trouvent
	 */
	public void displayIterator(Iterator it, String message) {
		System.out.println(" \n #### " + message + " ################################");
		while (it.hasNext()) {
			System.out.println(it.next());
		}
	}

	/**
	 1.6.2 Afficher tous les clients habitant Une ville donn�es et ayant plus d'un prenom
	 Trouver les bons param�tres.
	 * @throws ParseException 
	 */
	public void findByTown(String ville) throws ParseException {
		
		MongoDatabase bd = mongoClient.getDatabase("airbase");
		MongoCollection<Document> collection = bd.getCollection("clients");
		
		List<Document> myDoc = collection.find(eq("adresse.ville", ville)).into(new ArrayList<>());
		System.out.println("");
		System.out.println(" ************** Les clients habitant : / "+ville+" / et ayant plus d'un prenom **************");
		
		for (Document doc : myDoc) {
				
			JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(doc.toJson());
			JSONArray jsonArray = (JSONArray) jsonObject.get("prenom");
			
			// Pour compter les prenoms
			int compteurPrenom = 0;
			
			Iterator<String> iterator = jsonArray.iterator();
			while(iterator.hasNext()) {
				iterator.next();
				compteurPrenom ++;
				
			}
			//System.out.println(compteurPrenom);
			if (compteurPrenom > 1)
			{
				System.out.println(doc.toJson());
			}
			jsonObject.clear();
			
		}
		

			//JsonObject obj = new JsonParser().parse(myDoc.get(0).toJson()).getAsJsonObject();
			//System.out.println(obj);
		
			/*JSONParser jsonParser = new JSONParser();
			JSONObject jsonObject = (JSONObject) jsonParser.parse(myDoc.get(0).toJson());
			JSONArray jsonArray = (JSONArray) jsonObject.get("prenom");
			
			Iterator<String> iterator = jsonArray.iterator();
			while(iterator.hasNext()) {
			   System.out.println(iterator.next());
			}*/

	}

	/**
	 1.6.3 Afficher les clients sans leurs adresses
	 Trouver les bons param�tres.
	 */

	public void findClientWithOutAdress() {
		// A compl�ter
	}

	/**
	 .6.4 Afficher les informations sur 1 client ainsi que ses appr�ciations
	 sur les vols
	 Trouver les bons param�tres.
	 */
	public void joinClientsVols() {
		// A compl�ter
	}

	/**
	 charger un document (client) JSON  depuis un fichier vers une collection mongoDB
	 Cr�er pour cela un fichier contenant un seul json
	 Trouver les bons param�tres.
	 */
	public void loadOneClientFromJsonFile(String path) throws FileNotFoundException {
		// A compl�ter
		BufferedReader reader = new BufferedReader(new FileReader(path));

		ClientObj[] clientObj = new Gson().fromJson(reader, ClientObj[].class);
		Gson gson = new Gson();

			String json = gson.toJson(clientObj[0]);
			Document myDoc = Document.parse(json);
		    collection.insertOne(myDoc);
	}

	/**
	 charger plusieurs documents (clients) JSON depuis un fichier vers une collection mongoDB
	 Utilisez le fichier 2Json_collection_Import_Clients_Airbase.json vu dans le cours
	 Trouver les bons param�tres.
	 */
	public void loadManyClientsFromJsonFile(String path) throws FileNotFoundException {
		// A compl�ter
		BufferedReader reader = new BufferedReader(new FileReader(path));

		ClientObj[] clientObj = new Gson().fromJson(reader, ClientObj[].class);
		Gson gson = new Gson();
		List<Document> doc = new ArrayList<>();
		for (int j=0; j <clientObj.length; j++){
			String json = gson.toJson(clientObj[j]);
			Document myDoc = Document.parse(json);
			doc.add(myDoc);
		}

		collection.insertMany(doc);
		String str = "";
	}

	/**
	 charger des clients contenu dans un fichier CSV vers une collection mongoDB
	 Construisez un fichier CSV � partir du fichier json 2Json_collection_Import_Clients_Airbase
	 Trouver les bons param�tres.
	 */

	public void loadClientsFromCSVFile() {
		// A compl�ter
	}

}