/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web_client;

import java.io.*;
import java.net.*;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Thibaud
 */
public class WEB_Client {
    
    private Socket socket;
    
    public WEB_Client(){
    }

    public void connexion() {
        try {
            Scanner sc = new Scanner(System.in);
            URL url = new URL(sc.nextLine());
            String requete;
            
            //cree le socket
            InetAddress ia = InetAddress.getByName(url.getHost());
            socket = new Socket(ia,1026);
            socket.setSoTimeout(5000);
            //cree la requete
            OutputStream os = socket.getOutputStream();
            requete = "GET " +"http://"+url.getHost()+":80/"+ url.getFilePath() + " HTTP/1.0\r\n";
            os.write(requete.getBytes());
            os.flush();
        } catch (UnknownHostException ex) {
            Logger.getLogger(WEB_Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WEB_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public String receive() throws IOException{
        BufferedInputStream bis = new BufferedInputStream(socket.getInputStream());
        System.out.println("test");
        String data = "";
        byte[] buffer = new byte[512];
        int byteLu = 0;
        try {
            do {
                byteLu = bis.read(buffer, 0, 512);
                String bloc = new String(buffer, 0, byteLu);
                data += bloc;
            } while (byteLu == 512);
        } catch (SocketTimeoutException e) {
            System.out.println("Connection timeout");
        }

        return data;
    }
    
    public void interpretReponse(String reponse){
        System.out.println(reponse);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            WEB_Client client = new WEB_Client();
            client.connexion();
            String reponse = client.receive();
            client.interpretReponse(reponse);
        } catch (IOException ex) {
            System.out.println("erreur reception réponse");
        }
    }

}
