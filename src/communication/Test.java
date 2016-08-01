package communication;

import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * @author ericjbruno
 */
public class Test implements SerialPortEventListener {
	SerialPort serialPort = null;

	private static final String PORT_NAMES[] = {
			// "/dev/tty.usbmodem", // Mac OS X
			// "/dev/usbdev", // Linux
			"/dev/ttyACM", // Linux
			// "/dev/serial", // Linux
			// "COM3", // Windows
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
			System.out.println("Trying:");
			while (portId == null && portEnum.hasMoreElements()) {
				// Iterate through your host computer's serial port IDs
				//
				CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
				System.out.println("   port" + currPortId.getName());
				for (String portName : PORT_NAMES) {
					if (currPortId.getName().equals(portName) || currPortId.getName().startsWith(portName)) {

						// Try to connect to the Arduino on this port
						//
						// Open serial port
						serialPort = (SerialPort) currPortId.open(appName, TIME_OUT);
						portId = currPortId;
						System.out.println("Connected on port" + currPortId.getName());
						break;
					}
				}
			}

			if (portId == null || serialPort == null) {
				System.out.println("Oops... Could not connect to Arduino");
				return false;
			}

			// set port parameters
			serialPort.setSerialPortParams(DATA_RATE, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);

			// add event listeners
			serialPort.addEventListener(this);
			serialPort.notifyOnDataAvailable(true);

			// Give the Arduino some time
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private void sendData(String data) {
		try {
			System.out.println("Sending data: '" + data + "'");

			// open the streams and send the "y" character
			output = serialPort.getOutputStream();
			output.write(data.getBytes());
		} catch (Exception e) {
			System.err.println(e.toString());
			System.exit(0);
		}
	}

	//
	// This should be called when you stop using the port
	//
	public synchronized void close() {
		if (serialPort != null) {
			serialPort.removeEventListener();
			serialPort.close();
		}
	}

	//
	// Handle serial port event
	//
	@Override
	public synchronized void serialEvent(SerialPortEvent oEvent) {
		// System.out.println("Event received: " + oEvent.toString());
		try {
			switch (oEvent.getEventType()) {
			case SerialPortEvent.DATA_AVAILABLE:
				if (input == null) {
					input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
				}
				String inputLine = input.readLine();
				System.out.println(inputLine);
				break;

			default:
				break;
			}
		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	public Test() {
		appName = getClass().getName();
	}

	public static void main(String[] args) throws Exception {
		Test test = new Test();
		if (test.initialize()) {
			test.sendData("y");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
			test.sendData("n");
			try {
				Thread.sleep(2000);
			} catch (InterruptedException ie) {
			}
			test.close();
		}

		// Wait 5 seconds then shutdown
		try {
			Thread.sleep(2000);
		} catch (InterruptedException ie) {
		}
	}
}
