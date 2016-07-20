package trash;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.swing.*;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class panel extends JPanel {
	private static final long serialVersionUID = 1L;
	private BufferedImage image;

	// Create a constructor method
	public panel() {
		super();

	}

	private BufferedImage getimage() {
		return image;
	}

	private void setimage(BufferedImage newimage) {
		image = newimage;
		return;
	}

	public static BufferedImage matToBufferedImage(Mat matrix) {
		int cols = matrix.cols();
		int rows = matrix.rows();
		int elemSize = (int) matrix.elemSize();
		byte[] data = new byte[cols * rows * elemSize];
		int type;
		matrix.get(0, 0, data);
		switch (matrix.channels()) {
		case 1:
			type = BufferedImage.TYPE_BYTE_GRAY;
			break;
		case 3:
			type = BufferedImage.TYPE_3BYTE_BGR;
			// bgr to rgb
			byte b;
			for (int i = 0; i < data.length; i = i + 3) {
				b = data[i];
				data[i] = data[i + 2];
				data[i + 2] = b;
			}
			break;
		default:
			return null;
		}
		BufferedImage image2 = new BufferedImage(cols, rows, type);
		image2.getRaster().setDataElements(0, 0, cols, rows, data);
		return image2;
	}

	public void paintComponent(Graphics g) {

		double recH = getHeight() * .3;
		double recW = getWidth() * .4;
		int recHeight = (int) recH;
		int recWidth = (int) recW;
		int y_center = getHeight() / 2 - recHeight / 2;
		int x_center = getWidth() / 2 - recWidth / 2;
		BufferedImage temp = getimage();
		g.drawImage(temp, 10, 10, temp.getWidth(), temp.getHeight(), this);
		g.drawLine(0, 0, 200, 200);
		g.drawRect(x_center, y_center, recWidth, recHeight);
	}

	public static void main(String arg[]) {
		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		JFrame frame = new JFrame("Webcam");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(400, 400);
		panel panel = new panel();
		frame.setContentPane(panel);
		frame.setVisible(true);
		Mat webcam_image = new Mat();
		BufferedImage temp;

		VideoCapture capture = new VideoCapture(0);
		if (capture.isOpened()) {
			while (true) {
				capture.read(webcam_image);
				if (!webcam_image.empty()) {
					frame.setSize(webcam_image.width() + 40, webcam_image.height() + 60);
					temp = matToBufferedImage(webcam_image);
					panel.setimage(temp);
					panel.repaint();
				} else {
					System.out.println(" --(!) No captured frame -- Break!");
					break;
				}
			}
		}
		return;
	}
}