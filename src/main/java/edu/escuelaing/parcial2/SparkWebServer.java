package edu.escuelaing.parcial2;

import static spark.Spark.get;
import static spark.Spark.port;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import static spark.Spark.*;
import spark.Request;
import spark.Response;

public class SparkWebServer {
    private static int flag= 1;
    private static String url1="";
    private static String url2="";

    public static void main(String... args){
        port(getPort());

        options("/*",
        (request, response) -> {

            String accessControlRequestHeaders = request
                    .headers("Access-Control-Request-Headers");
            if (accessControlRequestHeaders != null) {
                response.header("Access-Control-Allow-Headers",
                        accessControlRequestHeaders);
            }

            String accessControlRequestMethod = request
                    .headers("Access-Control-Request-Method");
            if (accessControlRequestMethod != null) {
                response.header("Access-Control-Allow-Methods",
                        accessControlRequestMethod);
            }

            return "OK";
        });

        before((request, response) -> response.header("Access-Control-Allow-Origin", "*"));
        get("hello",(req, res) -> "Hello Docker!");
        get("/operation", (req,res) -> operation(req,res));
    }

    private static JSONObject operation(Request req, Response res){
        res.type("application/json");
        String op= req.queryParams("operation");
        String val= req.queryParams("value");
        JSONObject response;
        try {
            response = new JSONObject(IOUtils.toString(new URL(String.format(roundRobin(),op,val)),Charset.forName("UTF-8")));
        } catch (JSONException | IOException e) {
            response= new JSONObject();
            e.printStackTrace();
        }
        return response;
    }

    private static String roundRobin(){
        if(flag == 1){
            flag= 2;
            return url1;
        }else{
            flag= 1;
            return url2;
        }
    }

    /**
     * Define el puerto de spark
     * @return
     */
    private static int getPort() {
        if (System.getenv("PORT") != null) {
            return Integer.parseInt(System.getenv("PORT"));
        }
        return 4567;
    }
}
