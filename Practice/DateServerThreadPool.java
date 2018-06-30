/* Programming Problem 4.18 
* Modify the socket-based date server (Figure 3.26) in Chapter 3 so that the server services each client request using a thread pool.
* (The source code for Figure 3.26 will not be included here - you can find it in your textbook)
*/


import java.net.*; 
import java.io.*; 
import java.util.*; 
import java.util.concurrent.*; 

public class DateServerThreadPool {
    public static void main(String[] args) {
        ExecutorService pool = Executors.newCachedThreadPool();
        try {
            ServerSocket sock = new ServerSocket(6013); 
            // now listen for connections 
            while (true) {
                Socket client = sock.accept();  
                pool.execute(new Server(client)); 
            }
        } catch (IOException ioe) {

            System.err.println(ioe); 
        }
        pool.shutdown();
    }
}


class Server implements Runnable {
    private Socket client; 

    public Server(Socket client) {
        this.client = client; 
    }

    public void run() {
        try {
            PrintWriter pout = new PrintWriter(this.client.getOutputStream(), true); 

            // write the Date to the socket 
            pout.println(new java.util.Date().toString()); 
            
            // close the socket and resume listening for conenctions. 
            client.close();
        } catch(IOException ioe) {
            System.err.println(ioe); 
        }
    }
}