package sensors.camera;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import TelegramBot.Bot;
import com.google.common.base.Utf8;
import devices.light.yeelight.Yeelight;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

public class AutoLightRoom {
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
            int sensivity = 50;
            double maxArea = 200;
            int index = 0;
            Mat frame = new Mat(960, 1280, CvType.CV_8UC3);
            Mat frame_current = new Mat(960, 1280, CvType.CV_8UC3);
            Mat frame_previous = new Mat(960, 1280, CvType.CV_8UC3);
            Mat frame_result = new Mat(960, 1280, CvType.CV_8UC3);
            Size size = new Size(3, 3);
            Mat v = new Mat();
            Scalar scalar1 = new Scalar(0, 0, 255);
            Scalar scalar2 = new Scalar(0, 255, 0);

            //   Calendar calendar = Calendar.getInstance();
            //   Date date = calendar.getTime();

            //    LocalDateTime pre = LocalDateTime.now();
            //    LocalDateTime cur;

            Date pre = new Date();
            Date cur;
            boolean yeelightFlag = false;
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
                            if (contourarea > maxArea) {
                                found = true;
                                Rect r = Imgproc.boundingRect(contours.get(idx));
                                Imgproc.drawContours(frame, contours, idx, scalar1);
                                Imgproc.rectangle(frame, r.br(), r.tl(), scalar2, 1);
                            }
                            contour.release();
                        }

                        if (found) {
                            //если есть движение - обновляем время
                            pre = new Date();
                            System.out.println("Движение");
                            //если свет выключен - включаем
                            if (!yeelightFlag) {
                                System.out.println("Включаем свет");
                                Yeelight yeelight = new Yeelight(1, false, "192.168.0.101");
                                yeelight.swith("on");
                                yeelightFlag = true;
                            }

                            //    Imgcodecs.imwrite("camera" + (sdf.format(new Date())) + ".jpg", frame);

                        } else {
                            cur = new Date();
                            //если прошло больше 5 секунд без движения
                            if (cur.getTime() - pre.getTime() > 5000) {
                                if (yeelightFlag) {
                                    System.out.println("Выключаем свет");
                                    //выключаем свет
                                    Yeelight yeelight = new Yeelight(1, false, "192.168.0.101");
                                    yeelight.swith("off");
                                    //переводим флаг в офф
                                    yeelightFlag = false;
                                }
                            }
                        }
                    }

                    index++;

                    frame_current.copyTo(frame_previous);
                    frame.release();
                    frame_result.release();
                    frame_current.release();


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

        //коридор
        room_tl = new Point(905D, 0D);
        room_br = new Point(1030D, 450D);
        if (insideLocation(room_tl, room_br, tl, br))
            return "Коридор";

        return "Комната";
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