package sensors.camera;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.common.base.Utf8;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class MoovingDetectAllRooms {
    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");

    /**
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Started");
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.load("C:\\Program Files\\Java\\opencv\\opencv_ffmpeg320_64.dll");
        VideoCapture camera = new VideoCapture("rtsp://192.168.0.105:554/live/main");
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 960);

        if (!camera.isOpened()) {
            System.out.println("Error");
        } else {
            int sensivity = 10;
            double maxArea = 20;
            int index = 0;
            Mat frame = new Mat(960, 1280, CvType.CV_8UC3);
            Mat frame_current = new Mat(960, 1280, CvType.CV_8UC3);
            Mat frame_previous = new Mat(960, 1280, CvType.CV_8UC3);
            Mat frame_result = new Mat(960, 1280, CvType.CV_8UC3);
            Size size = new Size(3, 3);
            Mat v = new Mat();
            Scalar scalar1 = new Scalar(0, 0, 255);
            Scalar scalar2 = new Scalar(0, 255, 0);

            String location_previous = "";
            String location_current = "";
            double currentMaxArea = 0;
            Point currentMaxPointTl = null;
            Point currentMaxPointBr = null;

            int i = 1;
            while (true) {
                // System.out.println("Чек номер: " + i++);
                if (camera.read(frame)) {
                    frame.copyTo(frame_current);

                    Imgproc.GaussianBlur(frame_current, frame_current, size, 0);

                    if (index > 1) {
                        Core.subtract(frame_previous, frame_current, frame_result);

                        Imgproc.cvtColor(frame_result, frame_result, Imgproc.COLOR_RGB2GRAY);

                        Imgproc.threshold(frame_result, frame_result, sensivity, 255, Imgproc.THRESH_BINARY);

                        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                        Imgproc.findContours(frame_result, contours, v, Imgproc.RETR_LIST, Imgproc.CHAIN_APPROX_SIMPLE);
                        v.release();


                        boolean found = false;
                        for (int idx = 0; idx < contours.size(); idx++) {
                            Mat contour = contours.get(idx);
                            double contourarea = Imgproc.contourArea(contour);
                            // System.out.println("square1: " + contourarea);

                            // Imgproc.drawContours(frame, contours, idx, scalar3);
                            if (contourarea > maxArea) {
                                found = true;

                                Rect r = Imgproc.boundingRect(contours.get(idx));
                                Imgproc.drawContours(frame, contours, idx, scalar1);
                                //Imgproc.drawContours();
/*                                System.out.println();
                                System.out.println("tl (верх-лево): " + r.tl());
                                System.out.println("br (низ-право): " + r.br());
                                System.out.println("square: " + contourarea);*/
                                Imgproc.rectangle(frame, r.br(), r.tl(), scalar2, 1);

                                //проверяем, является ли площадь максимальной
                                if (contourarea > currentMaxArea) {
                                    currentMaxArea = contourarea;
                                    currentMaxPointTl = r.tl();
                                    currentMaxPointBr = r.br();
                                }
                            }
                            contour.release();
                        }

                        if (found) {
                            location_current = getLocation(currentMaxPointTl, currentMaxPointBr);
                            if (!"еррор".equals(location_current)) {
                                if (!location_previous.equals(location_current)) {
                                    System.out.println(location_current);
                                    Imgcodecs.imwrite("camera" + (sdf.format(new Date())) + ".jpg", frame);
                                    //Imgcodecs.imwrite("new/" + (sdf.format(new Date())) + ".jpg", frame);
                                }
                            }
                        }
                    }

                    index++;

                    frame_current.copyTo(frame_previous);
                    frame.release();
                    frame_result.release();
                    frame_current.release();
                    if (!"еррор".equals(location_current))
                        location_previous = location_current;
                    // try {
                    //    Thread.currentThread().sleep(1000);
                    //  } catch (InterruptedException ignored) {}
                }
            }
        }
        camera.release();
    }

    private static String getLocation(Point tl, Point br) {
        Point room_tl;
        Point room_br;
        //спальная зона
        room_tl = new Point(0D, 461D);
        room_br = new Point(829D, 960D);
        if (insideLocation(room_tl, room_br, tl, br))
            return "Спальня";

        //кухонная зона
        room_tl = new Point(0D, 0D);
        room_br = new Point(829D, 460D);
        if (insideLocation(room_tl, room_br, tl, br))
            return "Кухня";

        //коридор
        room_tl = new Point(905D, 0D);
        room_br = new Point(1000D, 400D);
        if (insideLocation(room_tl, room_br, tl, br))
            return "Коридор";

        //компьютер
        room_tl = new Point(830D, 461D);
        room_br = new Point(1050D, 700D);
        if (insideLocation(room_tl, room_br, tl, br))
            return "Компьютер";
        return "еррор";
    }

    private static boolean insideLocation(Point room_tl, Point room_br, Point tl, Point br) {
        if (tl.x >= room_tl.x && tl.x <= room_br.x) {
            if (tl.y >= room_tl.y && tl.y <= room_br.y) {
                return true;
            }
        }
        return false;
    }

}