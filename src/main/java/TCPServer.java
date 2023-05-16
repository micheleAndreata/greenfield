import java.io.*; 
import java.net.*; 

class TCPServer { 

	public static void main(String[] args) throws Exception
	{ 
		String clientSentence; 
		String capitalizedSentence; 

		/* Crea una "listening socket" sulla porta specificata */
		ServerSocket welcomeSocket = new ServerSocket(6789); 

		while(true) { 
			/* 
			 * Viene chiamata accept (bloccante). 
			 * All'arrivo di una nuova connessione crea una nuova
			 * "established socket"
			 */
			Socket connectionSocket = welcomeSocket.accept(); 

			/* Inizializza lo stream di input dalla socket */
			BufferedReader inFromClient = 
				new BufferedReader(new
						InputStreamReader(connectionSocket.getInputStream())); 

			/* Inizializza lo stream di output verso la socket */
			DataOutputStream  outToClient = 
				new DataOutputStream(connectionSocket.getOutputStream()); 

			/* Legge una linea (terminata da \n) dal client */
			clientSentence = inFromClient.readLine(); 

			capitalizedSentence = clientSentence.toUpperCase() + '\n'; 

			/* Invia la risposta al client */
			outToClient.writeBytes(capitalizedSentence);

			connectionSocket.close();

		}
	}
}
