/*Programming Problem 4.17
 * Modify the socket-based date server (Figure 3.26)
 *  in Chapter 3 so that the server services each
 *  client request in a separate thread.
 * (The source code for Figure 3.26 will not be
 *  included here - you can find it in your textbook)
 */

import java.net.*; 
import java.io.*; 
import java.util.*; 

public class DateServerThread {
    public static void main(String[] args) {
        try {
            ServerSocket sock = new ServerSocket(6013); 
            // now listen for connections 
            int count = 0; 
            while (true) {
                Socket client = sock.accept(); 
                Thread thread = new Thread(new Server(client, count)); 
                thread.start(); 
                count++; 
            }
        } catch (IOException ioe) {

            System.err.println(ioe); 
        }
    }
}


class Server implements Runnable {
    private Socket client; 
    private int count; 

    public Server(Socket client, int count) {
        this.client = client;
        this.count = count;  
    }

    public void run() {
        try {
            PrintWriter pout = new PrintWriter(this.client.getOutputStream(), true); 
             
            for (int i = 0; i < 10; i++) {
                pout.println("Thread" + count); 
            }

            // write the Date to the socket 
            pout.println(new java.util.Date().toString()); 
            
            // close the socket and resume listening for conenctions. 
            client.close();
            
        } catch(IOException ioe) {
            System.err.println(ioe); 
        }
    }
}
