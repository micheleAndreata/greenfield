import java.io.*; 
import java.net.*;

class TCPClient { 
	public static void main(String[] args) throws Exception {
		String sentence; 
		String modifiedSentence; 

		/* Inizializza l'input stream (da tastiera) */
		BufferedReader inFromUser = 
			new BufferedReader(new InputStreamReader(System.in)); 

		/* Inizializza una socket client, connessa al server */
		Socket clientSocket = new Socket("localhost", 6789); 

		/* Inizializza lo stream di output verso la socket */
		DataOutputStream outToServer = 
			new DataOutputStream(clientSocket.getOutputStream()); 

		/* Inizializza lo stream di input dalla socket */
		BufferedReader inFromServer = 
			new BufferedReader(new
					InputStreamReader(clientSocket.getInputStream())); 

		/* Legge una linea da tastiera */
		sentence = inFromUser.readLine(); 

		/* Invia la linea al server */
		outToServer.writeBytes(sentence + '\n'); 

		/* Legge la risposta inviata dal server (linea terminata da \n) */
		modifiedSentence = inFromServer.readLine(); 

		System.out.println("FROM SERVER: " + modifiedSentence); 

		clientSocket.close(); 
	}
}
