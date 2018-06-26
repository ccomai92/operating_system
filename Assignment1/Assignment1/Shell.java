public class Shell extends Thread { 
    
    private int count; // count of shell number 
    
    public Shell() {
        this.count = 1;  
    }

    public void run( ) {
        Boolean notDone = true;  
        while (notDone) {
            StringBuffer stringBuffer = new StringBuffer();
            SysLib.cout("Shell[" + this.count + "]% ");
            SysLib.cin(stringBuffer); // get input from user 
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
                    // execute argument and wait till to be done 
                    String[] inputArgs = SysLib.stringToArgs(input); 
                    SysLib.exec(inputArgs); 
                    SysLib.join(); 
                    input = ""; 
                } else if (args[i].equals("&")) {
                    // execute argument and not waiting to be done
                    String[] inputArgs = SysLib.stringToArgs(input); 
                    countAmpersand ++;
                    SysLib.exec(inputArgs);  
                    input = ""; 
                } else if (i == args.length - 1) { // and not equal to ";"
                    // no ; after last argument wait for last child to be done
                    input += args[i]; 
                    String[] inputArgs = SysLib.stringToArgs(input); 
                    SysLib.exec(inputArgs); 
                    SysLib.join(); 
                } else {
                    input += args[i] + " "; 
                }
            }

            // wait for children to join for the number of ampersend
            for (int i = 0; i < countAmpersand; i++) {
                SysLib.join(); 
            }

            this.count++;     
        } 
        SysLib.exit( );
    }
    
  }