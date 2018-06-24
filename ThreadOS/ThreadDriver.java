

public class ThreadDriver {
    public static void main(String[] args) {
        String[] arg = new String[2]; 

        arg[0] = "ping"; 
        arg[1] = "10000"; 
        new PingPong(arg).start(); 
        arg[0] = "PING"; 
        arg[1] = "90000"; 
        new PingPong(arg).start(); 
    }

}