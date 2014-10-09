/*
 * Samuel von Baussnern
 * S09-914-623
 * samuel.vonbaussnern@uzh.ch
 * 
 * (c) University of Zurich 2014
 */

package Assignment1;

import java.net.*;
import java.io.*;

public class Producer {
	static final String STOP_READING_STRING = ".bye";

	public static void main(String[] args) {
		if (args.length != 4) {
			System.err
					.println("Usage: java Producer <server name> <port number> <client name> <input file name>");
			System.exit(1);
		}
		String serverName = args[0];
		int port = Integer.parseInt(args[1]);
		String clientName = args[2];
		String inputFileName = args[3];

		// create connection to server
		// send the string "PRODUCER" to server first
		// read messages from input file line by line
		// put the client name and colon in front of each message
		// e.g., clientName:....
		// send message until you find ".bye" in the input file
		// close connection

		Socket clientSocket;
		BufferedWriter out;
		BufferedReader in;

		try {
			clientSocket = new Socket(serverName, port);

			in = new BufferedReader(new FileReader(inputFileName));
			out = new BufferedWriter(new OutputStreamWriter(
					clientSocket.getOutputStream()));

			out.write("PRODUCER");
			out.newLine();
			out.flush();

			String message = in.readLine();

			while (!message.equals(STOP_READING_STRING)) {
				out.write(clientName + ":" + message);
				out.newLine();
				out.flush();

				message = in.readLine();
			}

			in.close();
			out.close();
			clientSocket.close();
		} catch (FileNotFoundException e) {
			System.out.println("Producer FileNotFoundException: ");
			System.out.println(e.getMessage());
		} catch (IOException e) {
			System.out.println("Producer IOException: ");
			System.out.println(e.getMessage());
		}
	}
}
