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

    private String host;
    private String filePath;

    public URL(String adresse) {
        String[] data = adresse.split("/", 2);
        host = data[0];
        if (data.length==2){
            filePath = data[1];
        }
        else{
            filePath ="";
        }
    }
    
    public String getHost(){
        return this.host;
    }
    
    public String getFilePath(){
        return filePath;
    }
}
