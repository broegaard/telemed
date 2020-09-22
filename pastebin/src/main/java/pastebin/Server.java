package pastebin;

import static spark.Spark.*;

import java.util.*;

import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/** Controller class that defines the CRUD REST verbs.
 * 
 * Reference: Jim Webber, Chapter 3.
 * 
 * @author Henrik Baerbak Christensen, Aarhus University.
 *
 */
public class Server {

  private int id = 100;
  private Gson gson = new Gson();
  private Map<String,Bin> db = new HashMap<String, Bin>();

  public static void main(String[] args) {
    new Server();
  }
  
  public Server() {   
    /**
     * POST /bin. Create a new bin, if success, receive a Location header
     * specifying the bin's resource identifier.
     * 
     * Parameter: req.body must be JSON such as {"contents":
     * "Suzy's telephone no is 1234"}
     */
    post("/bin", (req, res) -> {
        // Convert from JSON into object format
        Bin q = gson.fromJson(req.body(), Bin.class);
        
        // Create a new resource ID
        String idAsString = ""+id++;
        
        // Store bin in the database
        db.put(idAsString, q);
        
        // 201 Created
        res.status(HttpServletResponse.SC_CREATED);
        // Set return type
        res.type("application/json");
        // Location = URL of created resource
        res.header("Location", req.host()+"/bin/"+idAsString);
        
        // Return the constructed bin
        return gson.toJson(q);
      });
    
    /**
     * GET /bin/<id>. Get the bin with the given id
     */
    get("/bin/:id", (req, res) -> {
        // Extract the bin id from the request
        String id = req.params(":id");

        // Set return type
        res.type("application/json");

        // Lookup, and return if found
        Bin bin = db.get(id);
        if (bin != null) {
          return gson.toJson(bin);
        }
        
        // Otherwise, return error
        res.status(HttpServletResponse.SC_NOT_FOUND);
        
        return gson.toJson(null);
      });
  }
}
