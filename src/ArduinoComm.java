
import java.io.InputStream;
import java.io.PrintWriter;
import com.fazecast.jSerialComm.*;


public class ArduinoComm {
	static SerialPort comPort;

	public static void connect() {
		comPort = SerialPort.getCommPorts()[0];
		comPort.openPort();
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 100, 0);
	}

	public static void read() {
		InputStream in = comPort.getInputStream();
		try {
			for (int j = 0; j < 1000; ++j)
				// text = (char)in.read();
				System.out.print((char) in.read());
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public static void write(int t) {
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
				while (true) {
					PrintWriter output = new PrintWriter(comPort.getOutputStream());
					output.print(t);
					output.flush();
					
						Thread.sleep(100);
					}} catch (Exception e) {
						try {
							Thread.sleep(1000);
							write(t);
						} catch (InterruptedException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
				
			}

		};
		thread.start();
	}

	public static void disconnect() {
		comPort.closePort();
	}

	public static void main(String[] args) {
		identifytraffic trafficstat = new identifytraffic();
		
		connect();
		
		int a=0;
		int t = 0;
		int traffic = trafficstat.traffic();
		
		while(a < 25){
			traffic = trafficstat.traffic();
		if (traffic == 1){
			t= 1;
		} else if (traffic == 2){
			t=2;
			try {
				Thread.sleep(200);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
		} else {t=3;};
		 
		
		write(t);
		
		read();
		a++;
		}
		disconnect();

	}

}