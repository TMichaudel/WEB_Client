/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package web_client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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
    private ClientForm form;

    public WEB_Client() {
        form = new ClientForm();
        form.getButtonObtenir().addActionListener(new GoButtonListener());
        form.setVisible(true);
    }

    public void connexion() {
        try {
            String machine = "";
            String file = "";
            String requete = "";

            //URL
            String url = form.getUrl();
            String[] data = url.split("/", 2);
            machine = data[0];
            if (data.length == 2) {
                file = data[1];
            } else {
                file = "/";
            }
            //cree la requete
            requete = "GET " + file + " HTTP/1.0\r\n";
            form.setOutPut(requete);
            //cree le socket
            InetAddress ia = InetAddress.getByName(machine);
            System.out.println(ia);
            socket = new Socket(ia, 80);
            socket.setSoTimeout(10000);
            OutputStream os = socket.getOutputStream();

            os.write(requete.getBytes());
            os.flush();
        } catch (UnknownHostException ex) {
            Logger.getLogger(WEB_Client.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(WEB_Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public String receive() throws IOException {
        InputStream bis = socket.getInputStream();
        String data = "";
        byte[] buffer = new byte[512];
        int byteLu = 0;
        try {
            do {
                System.out.println(bis.available());
                byteLu = bis.read(buffer, 0, 512);
                String bloc = new String(buffer, 0, byteLu);
                data += bloc;
            } while (byteLu == 512);
        } catch (SocketTimeoutException e) {
            form.getPagePane().setText("<p>Connection Timeout</p>");
        }

        return data;

    }

    class GoButtonListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            connexion();
            new Thread() {
                @Override
                public void run() {
                    try {
                        interpretReponse(receive());
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(WEB_Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
        }
    }

    public void interpretReponse(String reponse) {
        String header = reponse.substring(0, reponse.indexOf("\r\n\r\n"));
        String page = reponse.substring(reponse.indexOf("\r\n\r\n"));
        String output[] = header.split(" ",3);

        form.getHeaderPane().setText(header);
        form.getPagePane().setText(page);
        int code = Integer.parseInt(output[1]);
        String msg= new String("");
        switch (code / 100) {
            case 1:
                msg=("Infomation");
                break;

            case 2:
                msg=("Succes");
                break;

            case 3:
                msg=("Redirection");
                break;

            case 4:
                msg=("Erreur Client");
                break;

            case 5:
                msg=("Erreur Serveur");
                break;
             
            default:
                break;
        }
        msg+=(" : "+output[1]+", "+output[2].split("\n")[0]);
        form.setOutPut(msg);
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WEB_Client client = new WEB_Client();
    }

}
