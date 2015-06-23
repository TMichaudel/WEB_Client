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
            }

            //cree le socket
            InetAddress ia = InetAddress.getByName(machine);
            socket = new Socket(ia, 225);
            socket.setSoTimeout(4000);
            OutputStream os = socket.getOutputStream();

            //cree la requete
            requete = "GET /" + file + " HTTP/1.1\r\n";
            requete += "Host: " + machine + ":" + socket.getPort() + "\r\n";
            requete += "Connection: Close" + "\r\n" + "\r\n";
            form.setOutPut(requete);
            System.out.println(requete);

            //envoie la requete
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
                byteLu = bis.read(buffer);
                String bloc = new String(buffer, 0, byteLu);
                data += bloc;
            } while (bis.read()!=-1);
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
                        getReponse(receive());
                        socket.close();
                    } catch (IOException ex) {
                        Logger.getLogger(WEB_Client.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }.start();
        }
    }

    public void getReponse(String reponse) {
        String format="text", formatFichier = "txt", path = "C:\\Users\\Thibaud\\Desktop\\web\\";
        File file;
        String[] type = null;

        //séparation entre header et le contenu de la page:
        String header = reponse.substring(0, reponse.indexOf("\r\n\r\n"));
        String page = reponse.substring(reponse.indexOf("\r\n\r\n")+ 2);
        form.getHeaderPane().setText(header);
        form.setOutPut(getCode(header));

        //récupération du format du fichier obtenu
        if (header.contains("Content-Type")) {
            type = header.split("Content-Type:");
            if (type[1].contains("; charset")) {
                format = type[1].substring(1, type[1].indexOf("; charset"));
            }
            else if (type[1].contains("\r\n")) {
                format = type[1].substring(1, type[1].indexOf("\r\n"));
            }
            else {
                format = type[1].substring(1);
            }
        }
        
        //création du fichier suivant le format de la réponse
        if (format.contains("image/")) {
            formatFichier = format.replace("image/", "");
            file = new File(path + "image." + formatFichier);
            ecritureFichier(file, page);
            form.getPagePane().setText("L'image est enregistrée à : " + file.getPath() + "\r\n");
        } else if (format.contains("text/")) {
            formatFichier = format.replace("text/", "");
            file = new File(path + "page." + formatFichier);
            ecritureFichier(file, page);
            form.getPagePane().setText("La page est enregistrée à : " + file.getPath() + "\r\n" + page);
        } else {
            file = new File(path + "text." + formatFichier);
            System.out.println("formatFichier :"+formatFichier);
            ecritureFichier(file, page);
            form.getPagePane().setText("La page est enregistrée à : " + file.getPath() + "\r\n" + page);
        }
        System.out.println("Fichier enregistré !");
    }

    public String getCode(String header) {
        String output[] = header.split(" ", 3);
        int code = Integer.parseInt(output[1]);
        String msg = new String("");
        switch (code / 100) {
            case 1:
                msg = ("Infomation");
                break;

            case 2:
                msg = ("Succes");
                break;

            case 3:
                msg = ("Redirection");
                break;

            case 4:
                msg = ("Erreur Client");
                break;

            case 5:
                msg = ("Erreur Serveur");
                break;

            default:
                break;
        }
        msg += (" : " + output[1] + ", " + output[2].split("\n")[0]);
        return msg;
    }

    public void ecritureFichier(File f, String contenu) {
        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(f));
            bw.write(contenu);
            //bw.flush();
            bw.close();
        } catch (IOException ex) {
            form.setOutPut("Erreur de l'écriture du fichier.");
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        WEB_Client client = new WEB_Client();
    }

}
