/*
 * Programming Problem 4.17
 * Modify the socket-based date server (Figure 3.26) in Chapter 3 so that the server services each client request in a separate thread.
 * (The source code for Figure 3.26 will not be included here - you can find it in your textbook)
 * 
 * Programming Problem 4.18 
 * Modify the socket-based date server (Figure 3.26) in Chapter 3 so that the server services each client request using a thread pool.
 * (The source code for Figure 3.26 will not be included here - you can find it in your textbook)
 */

import java.net.*; 
import java.io.*; 

public class DateServer {
    public static void main(String[] args) {
        try {
            ServerSocket sock = new ServerSocket(6013); 
            // now listen for connections 
            while (true) {
                Socket client = sock.accept(); 
                PrintWriter pout = 
                    new PrintWriter(client.getOutputStream(), true); 
                
                // write the Date to the socket 
                pout.println(new java.util.Date().toString()); 

                // close the socket and resume 
                // listening for connections 
                client.close(); 
            }
        } catch (IOException ioe) {

            System.err.println(ioe); 
        }
    }
}