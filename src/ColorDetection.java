import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

public class ColorDetection {

	private static Mat mSrc;

	public static void cvt_YUVtoRGBtoHSV(Mat src, Mat dst) {
		mSrc = new Mat();
		src.copyTo(mSrc);
		Imgproc.cvtColor(mSrc, dst, Imgproc.COLOR_YUV420sp2RGB);
		Imgproc.cvtColor(dst, dst, Imgproc.COLOR_RGB2HSV);
	}

	public static void getBlueMat(Mat src, Mat dst) {
		Core.inRange(src, new Scalar(100, 100, 100), new Scalar(120, 255, 255), dst);
	}

	public static void getYellowMat(Mat src, Mat dst) {
		Core.inRange(src, new Scalar(20, 100, 100), new Scalar(30, 255, 255), dst);
	}

	public static void detectSingleBlob(Mat src, Mat image, String text, Mat dst) {
		List<MatOfPoint> contours = new ArrayList<MatOfPoint>(); // vector<vector<Point>
																	// >
																	// contours;
		Mat hierarchy = new Mat();
		src.copyTo(dst);

		Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

		int k = getBiggestContourIndex(contours);
		Rect boundRect = setContourRect(contours, k);

		Point center = new Point();
		getCenterPoint(boundRect.tl(), boundRect.br(), center);
		Core.rectangle(dst, boundRect.tl(), boundRect.br(), new Scalar(255, 255, 0), 2, 8, 0);

		Core.putText(dst, text, boundRect.tl(), 0/* font */, 1, new Scalar(255, 0, 0, 255), 3);
	}

	public static void getCenterPoint(Point tl, Point br, Point dst) {
		dst.x = (tl.x + br.x) / 2;
		dst.y = (tl.y + br.y) / 2;
	}

	public static int getBiggestContourIndex(List<MatOfPoint> contours) {
		double maxArea = 0;
		Iterator<MatOfPoint> each = contours.iterator();
		int j = 0;
		int k = -1;
		while (each.hasNext()) {
			MatOfPoint wrapper = each.next();
			double area = Imgproc.contourArea(wrapper);
			if (area > maxArea) {
				maxArea = area;
				k = j;
			}
			j++;
		}
		return k;
	}

	public static Rect setContourRect(List<MatOfPoint> contours, int k) {
		Rect boundRect = new Rect();
		Iterator<MatOfPoint> each = contours.iterator();
		int j = 0;
		while (each.hasNext()) {
			MatOfPoint wrapper = each.next();
			if (j == k) {
				return Imgproc.boundingRect(wrapper);
			}
			j++;
		}
		return boundRect;
	}
}
