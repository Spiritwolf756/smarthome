package ping;

import java.io.IOException;
import java.net.InetAddress;

public class Ping {
    String url;

    public Ping(String url) {
        this.url = url;
    }

    private int getPing() {

        try {
            InetAddress address = InetAddress.getByName(url);
            Boolean status = address.isReachable(500);
            if (status) {
                //return "here";
                return 1;
            } else {
                //return "out";
                return 0;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 2;
        }
    }

    public int getStatus(Boolean atHome) {
        /**
         * Возвращаемые значения:
         * 0 - вышел из дома;
         * 1 - пришел домой;
         * 2 - ошибка пинга;
         * 3 - не было изменения;
         * 4 - был лаг, результат пинга некорректен;
         * 5 - алгоритм не работает.
         */
        int j = 3; //количество проверок (отсчет с нуля)
        String status;
        int count = 0;
        int check1 = 0;
        int check2;
        for (int i = 0; i <= j; i++) {
            System.out.println(check1);
            check2 = getPing();
            if (check1 == check2) {
                count = count + 1;
                if (count == j) {
                    if ((atHome && check1 != 1) || (!atHome && check1 != 0)) {
                        return check1;
                    }
                }
            } else {
                count = 0;
                i = 0;
                check1 = check2;
            }
        }
        return 5;
    }
}
