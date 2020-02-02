package sensors.camera;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;

import java.io.File;

public class Camera {
    //public static void main (String args[]){
    public static File getPhoto() {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.load("C:\\Program Files\\Java\\opencv\\opencv_ffmpeg320_64.dll");
        VideoCapture camera = new VideoCapture("rtsp://192.168.0.105:554/live/main");
        camera.set(Videoio.CV_CAP_PROP_FRAME_WIDTH, 1280);
        camera.set(Videoio.CV_CAP_PROP_FRAME_HEIGHT, 960);

        if (!camera.isOpened()) {
            System.out.println("Error");
        } else {
            //int index = 0;
            Mat frame = new Mat();
            int i = 0;
            //while(i==0){
            if (camera.read(frame)) {
                System.out.println("Captured Frame Width " + frame.width() + " Height " + frame.height());

                Imgcodecs.imwrite("photos/Photo" + ".jpg", frame);
                camera.release();

                return new File("photos/Photo" + ".jpg");
                //break;
                //  i++;
                //}
            }
        }

        camera.release();
        return null;
    }
}