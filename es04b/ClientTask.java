import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;

/**
 * Gestore a righe UTF-8: il socket è chiuso automaticamente anche su errore o
 * timeout.
 */
public class ClientTask implements Runnable {
  private final Socket socket;

  public ClientTask(Socket socket) {
    this.socket = socket;
  }

  public static float calcolatrice(float[] array, String operazione) {
    if (operazione.equals("+")) {
      return array[0] + array[1];
    } else if (operazione.equals("-")) {
      return array[0] - array[1];
    } else if (operazione.equals("*")) {
      return array[0] * array[1];
    } else {
      return array[0] / array[1];
    }
  }

  @Override
  public void run() {
    try (Socket owned = socket) {
      socket.setSoTimeout(30_000); // Limita le letture di un client inattivo.

      // Creiamo un BufferedReader e un PrintWriter per leggere e scrivere righe
      // UTF-8.
      BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
      PrintWriter out = new PrintWriter(socket.getOutputStream(), true, StandardCharsets.UTF_8);

      // Stampa il nome del thread per distinguere i gestori.
      // Ogni thread ha il suo nome, come Thread-0, Thread-1, ecc.
      System.out.println("Gestore: " + Thread.currentThread().getName());
      // Legge una riga dal client e risponde con un messaggio.
      String message = in.readLine();
      
      if (message == null)
        return;

      String[] array = message.split("\\s+");
      String operazione = array[1];
      float[] dati = { Float.parseFloat(array[0]), Float.parseFloat(array[2]) };

      String risultato = ("il risultato è: " + calcolatrice(dati, operazione));

       // EOF senza richiesta: non rispondere al testo "null".
      out.println("Server: Ho ricevuto il tuo messaggio: " + risultato);
    } catch (IOException e) {
      System.err.println("Errore gestore: " + e.getMessage());
    }
  }
}
