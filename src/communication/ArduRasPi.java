package communication;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import gnu.io.CommPort;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;

public class ArduRasPi {

	public static void main(String[] args) {
		if (args.length != 2) {
			System.out.println("Usage: ArduRasPi portName portSpeed");
			System.out.println("Example: ArduRasPi /dev/ttyUSB0 9600");
			System.exit(1);
		}

		try {
			(new ArduRasPi()).connect(args[0], Integer.parseInt(args[1]));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ArduRasPi() {
		super();
	}

	void connect(String portName, int portSpeed) throws Exception {
		System.out.println("Connecting to " + portName + " at " + portSpeed + " baud");

		CommPortIdentifier portIdentifier = null;

		try {
			portIdentifier = CommPortIdentifier.getPortIdentifier(portName);
		} catch (gnu.io.NoSuchPortException e) {
			System.out.println("Port not found, listing available ports...");
			java.util.Enumeration<CommPortIdentifier> portEnum = CommPortIdentifier.getPortIdentifiers();
			while (portEnum.hasMoreElements())
				System.out.println(portEnum.nextElement().getName());
			System.exit(1);
		}

		if (portIdentifier.isCurrentlyOwned()) {
			System.out.println("Error: Port is currently in use");
		} else {
			CommPort commPort = portIdentifier.open(this.getClass().getName(), 2000);

			if (commPort instanceof SerialPort) {
				SerialPort serialPort = (SerialPort) commPort;
				serialPort.setSerialPortParams(portSpeed, SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
						SerialPort.PARITY_NONE);

				InputStream in = serialPort.getInputStream();
				OutputStream out = serialPort.getOutputStream();

				(new Thread(new SerialReader(in))).start();
				(new Thread(new SerialWriter(out))).start();

			} else {
				System.out.println("Error: Only serial ports are handled by this example.");
			}
		}
	}

	public static class SerialReader implements Runnable {
		InputStream in;

		public SerialReader(InputStream in) {
			this.in = in;
		}

		@Override
		public void run() {
			byte[] buffer = new byte[1024];
			int len = -1;
			try {
				while ((len = this.in.read(buffer)) > -1) {
					System.out.print(new String(buffer, 0, len));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static class SerialWriter implements Runnable {
		OutputStream out;

		public SerialWriter(OutputStream out) {
			this.out = out;
		}

		@Override
		public void run() {
			try {
				int c = 0;
				while ((c = System.in.read()) > -1) {
					this.out.write(c);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}