package devices.light.yeelight;

import com.google.common.base.Charsets;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Yeelight {
    private int id;
    private boolean status;
    private String ip;
    private Socket socket;
    private OutputStreamWriter objOut;
    private InputStreamReader objIn;

    public Yeelight(int id, boolean status, String ip) {
        this.id = id;
        this.status = status;
        this.ip = ip;
    }

    private void openConnection() {
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, 55443));
            objOut = new OutputStreamWriter(socket.getOutputStream());
            objIn = new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8);
            //objIn = new InputStreamReader(socket.getInputStream());

        } catch (IOException e) {
            IOUtils.closeQuietly(socket);
        }
    }
    public String swith(String swith) {
        JSONObject jo = new JSONObject();
        List<String> params = new ArrayList<>();
        params.add(swith);
        jo.put("params", params)
                .put("method", "set_power")
                .put("id", id);
        return SendCommand(jo);
    }

    private String SendCommand(JSONObject jo) {
        openConnection();
        try {
            objOut.write(jo.toString() + "\r\n");
            objOut.flush(); //очищает поток output-a

            char[] buffer = new char[8192];

            StringBuilder builder = new StringBuilder();
            int received;

            String reply = null;

            String data = new String(buffer, 0, objIn.read(buffer));

            System.out.println(data);

            return data;

        } catch (JSONException | IOException e) {
            e.printStackTrace();
        } finally {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(-1);
    }
}
