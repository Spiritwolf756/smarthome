package controller.devicesCommands;

import controller.Controller;
import controller.Tasks;
import devices.light.yeelight.Yeelight;
import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Camera {
    public static Camera INSTANCE;
    VideoCapture camera;
    boolean bAutoLight;
    boolean bGuard;
    boolean auto;
    int guardCount;
    Yeelight yeelight;

/*    public Camera(){
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.load("C:\\Program Files\\Java\\opencv\\opencv_ffmpeg320_64.dll");
        camera = new VideoCapture("rtsp://192.168.0.103:554/live/main");
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 960);
        yeelight = new Yeelight(1, false, "192.168.0.101");
    }*/

    private Camera() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.load("C:\\Program Files\\Java\\opencv\\opencv_ffmpeg320_64.dll");
        //    camera = new VideoCapture("rtsp://192.168.0.103:554/live/main");
      //  camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280);
      //  camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 960);
        yeelight = new Yeelight(1, false, "192.168.0.101");
    }

    public void setCamera() {
        camera = new VideoCapture("rtsp://192.168.0.103:554/live/main");
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 960);
        //return INSTANCE;
    }

    public static Camera getINSTANCE() {
        if (INSTANCE == null)
            INSTANCE = new Camera();
        return INSTANCE;
    }

    public void setGuardOn() {
        guardCount = 0;
        bGuard = true;
        if (!auto) {
            auto = true;
            setCamera();
            autoLight();
        }
    }

    public void setGuardOff() {
        bGuard = false;
        if (!bAutoLight)
            auto = false;
    }

    boolean setAutoLightOn() throws InterruptedException {
        if (bAutoLight) {
            bAutoLight = false;
            Thread.sleep(1000);
        }
        bAutoLight = true;
        if (!auto) {
            auto = true;
            setCamera();
            autoLight();
        }
        return true;
    }

    boolean setAutoLightOff() {
        bAutoLight = false;
        if (!bGuard)
            auto = false;
        return false;
    }

    private void autoLight() {
        Thread al = new Thread(() -> {
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

                Date pre = new Date();
                Date cur;
                boolean yeelightFlag = false;
                int i = 1;
                while (auto) {
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
                                }
                                contour.release();
                            }
                            //блок работы со автоматическим включением света
                            if (bAutoLight) {
                                if (found) {
                                    //если есть движение - обновляем время
                                    pre = new Date();
                                    // System.out.println("Движение");
                                    //если свет выключен - включаем
                                    if (!yeelightFlag) {
                                        //System.out.println("Включаем свет");
                                        yeelight.swith("on");
                                        yeelightFlag = true;
                                    }
                                } else {
                                    cur = new Date();
                                    //если прошло больше 5 секунд без движения
                                    if (cur.getTime() - pre.getTime() > 3000) {
                                        if (yeelightFlag) {
                                            System.out.println("Выключаем свет");
                                            //выключаем свет
                                            //yeelight.swith("off");
                                            //переводим флаг в офф
                                            yeelightFlag = false;
                                            //заменяем текущую картинку, чтобы изменение яркости не влияло на скрипт
                                            try {
                                                Thread.sleep(10000);
                                            } catch (InterruptedException e) {
                                                e.printStackTrace();
                                            }
                                            index = -1;
                                        }
                                    }
                                }
                            }
                            //блок работы с охранной системой
                            if (bGuard) {
                                if (found) {
                                    if (i < 3) {

                                    } else {
                                        bGuard = false;
                                    }
                                    i++;
                                }
                            }
                        }
                        index++;
                        frame_current.copyTo(frame_previous);
                        frame.release();
                        frame_result.release();
                        frame_current.release();
                    }
                }
                camera.release();
            }
        });

        al.start();
        //    camera.release();
    }

    public File getPhoto() {
        setCamera();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        if (!camera.isOpened()) {
            System.out.println("Error");
        } else {
            Mat frame = new Mat(960, 1280, CvType.CV_8UC3);
            String path = "src/main/resources/photos/Photo" + (sdf.format(new Date())) + ".jpg";
            if (camera.read(frame)) {
                Imgcodecs.imwrite(path, frame);
                frame.release();
                camera.release();
                return new File(path);
            }
        }
        return null;
    }
}
