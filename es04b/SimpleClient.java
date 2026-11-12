import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/** Client della variante con una sola richiesta: invio e lettura sono a righe UTF-8. */
public class SimpleClient {
    public static void main(String[] args) throws IOException {

        String[] array;
        String input;

        try (Socket socket = new Socket("localhost", 8844);
             Scanner keyboard = new Scanner(System.in)) {
            socket.setSoTimeout(30_000);

            // Crea un BufferedReader e un PrintWriter per leggere e scrivere righe UTF-8.
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    socket.getInputStream(), StandardCharsets.UTF_8));

            // Chiede all'utente di scrivere un messaggio, lo invia al server e stampa la risposta.
            System.out.println("Scrivi un messaggio:");
            if (!keyboard.hasNextLine()) return; // Nessun input: esci.
            
            input = keyboard.nextLine();
            input = input.trim();
            array = input.split("\\s+");
            if (array.length != 3 || (!array[1].equals("+") && !array[1].equals("-") && !array[1].equals("*") && !array[1].equals("/"))){
                System.out.println("operazione non riconosciuta");
                System.exit(1);
            }

            try{
                float first = Float.parseFloat(array[0]);
                float second = Float.parseFloat(array[2]);
            }
            catch(NumberFormatException e){
                System.out.println("valori non numerici");
                System.exit(1);
            }

            if(array[1].equals("/") && Float.parseFloat(array[2]) == 0){
                if (Float.parseFloat(array[0]) > 0){
                    System.out.println("il risultato tende a infinito");
                }
                else if(Float.parseFloat(array[0]) < 0){
                    System.out.println("il risultato tende a meno infinito");
                }
                else if(Float.parseFloat(array[0]) == 0){
                    System.out.println("il risultato è indefinito");
                }
                System.exit(0);
            }

            out.println(input);   // La newline rende completa la richiesta.            
            
            String response = in.readLine();    // Legge la risposta del server.
            if (response != null) System.out.println("Risultato: " + response);
        }
    }
}
