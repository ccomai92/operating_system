public class Shell extends Thread { 
    private int count; 
    public Shell() {
        this.count = 1;  
    }

    public void run( ) {
        Boolean notDone = true;  
        while (notDone) {
            StringBuffer stringBuffer = new StringBuffer();
            SysLib.cout("Shell[" + this.count + "]% ");
            SysLib.cin(stringBuffer); 
            String input = stringBuffer.toString();
            String[] args = SysLib.stringToArgs(input); 

            if (args[0].equals("exit")) {
                notDone = false; 
                break; 
            }

            // else do something with args
            int countAmpersand = 0; 
            input = ""; 
            for (int i = 0; i < args.length; i++) {
                if (args[i].equals(";")) {
                    String[] inputArgs = SysLib.stringToArgs(input); 
                    SysLib.exec(inputArgs); 
                    SysLib.join(); 
                    input = ""; 
                } else if (args[i].equals("&")) {
                    String[] inputArgs = SysLib.stringToArgs(input); 
                    countAmpersand ++;
                    SysLib.exec(inputArgs);  
                    input = ""; 
                } else if (i == args.length - 1) { // and not equal to ";"
                    input += args[i]; 
                    String[] inputArgs = SysLib.stringToArgs(input); 
                    SysLib.exec(inputArgs); 
                    SysLib.join(); 
                } else {
                    input += args[i] + " "; 
                }
            }

            for (int i = 0; i < countAmpersand; i++) {
                SysLib.join(); 
            }

            


            /* PingPong abc 100 ; PingPong xyz 100 ; PingPong 123 100 ; 
            String[] args1 = {"PingPong", "abc", "100"}; 
            String[] args2 = {"PingPong", "xyz", "100"};
            String[] args3 = {"PingPong", "123", "100"};
            SysLib.exec(args1); 
            SysLib.join();
            SysLib.exec(args2); 
            SysLib.join();
            SysLib.exec(args3); 
            SysLib.join();
            */

            /* PingPong abc 100 & PingPong xyz 100 & PingPong 123 100 & 
            String[] args1 = {"PingPong", "abc", "100"}; 
            String[] args2 = {"PingPong", "xyz", "100"};
            String[] args3 = {"PingPong", "123", "100"};
            SysLib.exec(args1); 
            SysLib.exec(args2); 
            SysLib.exec(args3); 
            SysLib.join(); 
            SysLib.join(); 
            SysLib.join(); 
            */

            this.count++;     
        } 
        SysLib.exit( );
    }
  }