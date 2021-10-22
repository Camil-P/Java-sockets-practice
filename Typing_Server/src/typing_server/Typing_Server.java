/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package typing_server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *
 * @author Camil
 */
public class Typing_Server {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        try (ServerSocket listener = new ServerSocket(59898)) {
            System.out.println("The Typing war server is running...");
            ExecutorService pool = Executors.newFixedThreadPool(20);
            while (true) {
                pool.execute(new Game(listener.accept()));
            }
        }
    }

    private static class Game implements Runnable {
        private Socket socket;

        String words;
        long timeNeeded;
        protected String chosenWordsArray[] = new String[20];
        protected int wordIndex = 0;
        
        Game(Socket socket) {
            this.socket = socket;
        }

        protected void createWords() {
            int i = 0;
            String crWord = "";
            String[] wordArray = {"godinu", "za", "blic", "nama", "papuca", "patofna", "odasiljac", "definitivno", "je", "obelezila", "i", "aktuelna", "kovid", "pandemija", "koja", "je", "u", "velikoj", "meri", "uticala", "kako", "na", "privatni", "tako", "i", "na", "poslovni", "zivot", "pojedinaca", "mnoge", "obrazovne", "ustanove", "su", "organizovale", "ucenje", "na", "daljinu", "koristeci", "razlicite", "platforme", "za", "povezivanje", "i", "ucenje", "pa", "stoga", "ne", "cudi", "sto", "je", "pojam", "Google", "Classroom", "bio", "najpretrazivaniji", "pojam", "u", "Srbiji", "ove", "godine", "Vucic"};
            words = "";
            while(i < 20) {
                    crWord = wordArray[(int)(Math.random() * 60)];
                    if(!words.contains(crWord)) {
                            words += crWord + " ";
                            this.chosenWordsArray[i] = crWord;
                            i+=1;
                    }
            }  
        }
        
        @Override
        public void run() {
            System.out.println("Connected: " + socket);
            try {
            	System.out.println("Usao u try");
//            	BufferedReader in = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
                Scanner in = new Scanner(socket.getInputStream());
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                createWords();
                System.out.println("Kreirao reci");
                out.println(words); 
                System.out.println(words.length()); 
                System.out.println("Poslao reci");
                long startingTime = System.currentTimeMillis();
                out.println("START"); 
                System.out.println("Pokrenuo igru");
                String score = "";
                String trRec = "";
                while (in.hasNextLine()) {
                    trRec = in.nextLine();
                    if(trRec.equals(this.chosenWordsArray[wordIndex])){
                        if(wordIndex < 19){
                            score += '/';
                            out.println("SCORE " + score);
                            wordIndex += 1;
                        }
                        else{
                            this.timeNeeded = System.currentTimeMillis() - startingTime;
                            out.println("TIME " + this.timeNeeded);
                            break;
                        }
                    }
                    else{
                        out.println("WRONG");
                    }
                }
                System.out.println("Poslao TIME");
            } catch (Exception e) {
                System.out.println("Error: " + e);
            } finally {
                try {
                    socket.close();
                } catch (IOException e) {
                System.out.println("Error: " + e);
                }
                System.out.println("Closed: " + socket);
            }
        }
    }
}
