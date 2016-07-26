package main;

import java.io.OutputStream;


import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

import vision.*;


/*public class main {

	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		IdentifyParking x = new IdentifyParking();
		
		int[] B=  x.center();
		
		System.out.println("X"+(B[0	]));
		System.out.println("Y"+(B[1]));
		
}} */



import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * @author ericjbruno
 */
public class main implements SerialPortEventListener {
    SerialPort serialPort = null;

    private static final String PORT_NAMES[] = { 
//        "/dev/tty.usbmodem", // Mac OS X
//        "/dev/usbdev", // Linux
        "/dev/ttyACM", // Linux
//        "/dev/serial", // Linux
//        "COM3", // Windows
    };
    
    private String appName;
    private BufferedReader input;
    private OutputStream output;
    
    private static final int TIME_OUT = 1000; // Port open timeout
    private static final int DATA_RATE = 9600; // Arduino serial port

    public boolean initialize() {
        try {
            CommPortIdentifier portId = null;
            Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

            // Enumerate system ports and try connecting to Arduino over each
            //
            System.out.println( "Trying:");
            while (portId == null && portEnum.hasMoreElements()) {
                // Iterate through your host computer's serial port IDs
                //
                CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
                System.out.println( "   port" + currPortId.getName() );
                for (String portName : PORT_NAMES) {
                    if ( currPortId.getName().equals(portName) 
                      || currPortId.getName().startsWith(portName)) {

                        // Try to connect to the Arduino on this port
                        //
                        // Open serial port
                        serialPort = (SerialPort)currPortId.open(appName, TIME_OUT);
                        portId = currPortId;
                        System.out.println( "Connected on port" + currPortId.getName() );
                        break;
                    }
                }
            }
        
            if (portId == null || serialPort == null) {
                System.out.println("Oops... Could not connect to Arduino");
                return false;
            }
        
            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                            SerialPort.DATABITS_8,
                            SerialPort.STOPBITS_1,
                            SerialPort.PARITY_NONE);

            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);

            // Give the Arduino some time
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
            
            return true;
        }
        catch ( Exception e ) { 
            e.printStackTrace();
        }
        return false;
    }
    
    private void sendData(String data) {
        try {
            System.out.println("Sending data: '" + data +"'");
            
            // open the streams and send the "y" character
            output = serialPort.getOutputStream();
            output.write( data.getBytes() );
        } 
        catch (Exception e) {
            System.err.println(e.toString());
            System.exit(0);
        }
    }

    //
    // This should be called when you stop using the port
    //
    public synchronized void close() {
        if ( serialPort != null ) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    //
    // Handle serial port event
    //
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        //System.out.println("Event received: " + oEvent.toString());
        try {
            switch (oEvent.getEventType() ) {
                case SerialPortEvent.DATA_AVAILABLE: 
                    if ( input == null ) {
                        input = new BufferedReader(
                            new InputStreamReader(
                                    serialPort.getInputStream()));
                    }
                    String inputLine = input.readLine();
                    System.out.println(inputLine);
                    break;

                default:
                    break;
            }
        } 
        catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    public main() {
        appName = getClass().getName();
    }
    
    public static void main(String[] args) throws Exception {
        main test = new main();
        
        IdentifyParking x = new IdentifyParking();
		
		int[] B;
			B = x.center();
			System.out.println("X"+(B[0	]));
			System.out.println("Y"+(B[1]));
			
			int X = (int) B[0];
			int Y = (int) B[1];
			
			if (X < 0){
        
        if ( test.initialize() ) {
            test.sendData("y");
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
            test.sendData("n");
            try { Thread.sleep(2000); } catch (InterruptedException ie) {}
            test.close();
        } } else { 
        	
        	
        }

        // Wait 5 seconds then shutdown
        try { Thread.sleep(2000); } catch (InterruptedException ie) {}
    }
}
