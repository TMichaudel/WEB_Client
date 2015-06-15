/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web_client;

/**
 *
 * @author Thibaud
 */
public class URL {

    String methode;
    String machine;
    String port;
    String filePath;

    public URL(String adresse) {
        String[] data = adresse.split(":", 3);
        methode = data[0];
        machine = data[1].substring(2);
        port = data[2].split("/")[0];
        filePath = data[2].split("/")[1];
        
        
    }
}
