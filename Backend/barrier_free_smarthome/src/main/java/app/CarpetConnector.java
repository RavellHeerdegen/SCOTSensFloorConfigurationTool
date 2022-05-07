package app;


import app.models.CarpetCoordinate;
import app.services.JsonService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.Timer;
import java.util.TimerTask;

/**
 * This component is used to connect to the target carpet and get its data in realtime.
 *
 */
@Component
public class CarpetConnector implements Runnable {

    Logger logger = LoggerFactory.getLogger(CarpetConnector.class);

    private long timeStepMillis = 750; // Timestep in milliseconds for evaluating the coordinates
    private Carpet carpet;
    String sensFloorIpAddress;

    public CarpetConnector(Carpet carpet, JsonService jsonService) {
        this.sensFloorIpAddress = jsonService.getCarpetConnectorIP();
        this.carpet = carpet;
        Thread thread = new Thread(this);
        thread.start();
        logger.info("Threat activated");
    }

    @Override
    public void run() {
        if(this.sensFloorIpAddress.isEmpty()){
            mockReader();
            logger.info("Mock Carpetdata startet, beacuse there is no CarpetIP");
        }
        reader();
        logger.info("Carpet Connector started");
    }

    /**
     * Reads data from the carpet.
     */
    public void reader() {
    	logger.info("Trying to read data from the carpet...");

        CoordinateInterpolator interpolator;
        try {
            int port = 4000;
            SocketAddress socketAddress = new InetSocketAddress(sensFloorIpAddress, port);
            Socket socket = new Socket();
            int timeoutInMilliseconds = 1000;
            socket.connect(socketAddress, timeoutInMilliseconds);
            logger.info("Socket to carpet connected!");

            interpolator = new CoordinateInterpolator(this.timeStepMillis) {
                @Override
                public void onInterpolatedCoordinate(CarpetCoordinate c) {
                    carpet.changeActiveField(c);
                }
            };

            // ensures, that the socket is closed and the interpolator is stopped, when the program is terminated
            Runtime.getRuntime().addShutdownHook(new Thread() {
                public void run() {
                    try {
                        socket.close();
                        logger.info("Socket to carpet closed.");
                        if (interpolator != null){
                            interpolator.stop();
                        }
                    } catch (IOException exception) {
                        exception.printStackTrace();
                    }
                }
            });

            interpolator.start();
            while (true) {
                InputStream inputStream = socket.getInputStream();
                byte[] data = new byte[100];
                int count = inputStream.read(data); // Note: Waits if there is no data to read yet

                String dataAsString = "";
                String dataAsIntegers = "";
                for (int m = 0; m < count; m++) {
                    dataAsString += (char) data[m];
                    int tmp = data[m];
                    dataAsIntegers += tmp;
                    dataAsIntegers += " ";
                }
                logger.trace("RAW carpet data: {}",dataAsIntegers);

                if (dataAsIntegers.length() < 11) {
                    logger.info("Carpet sent incomplete data... ignoring it.");
                    continue;
                }

                int posX;
                int posY;

                int carpetID = dataAsIntegers.charAt(7) - '0';
                if(carpetID == 4){ // Wenn die ID 4 ist, dann ist wird die Matte und nicht der Teppich angesprochen
                    posX = 1;
                    posY = 7;
                } else {
                    // Hier wird ein Feld auf dem Teppich angesprochen
                    posY = dataAsIntegers.charAt(9) - '0';
                    posX = dataAsIntegers.charAt(11) - '0';
                }
                logger.trace("Parsed coordinate: {}, {}", posX, posY);
                interpolator.addCoordinate(new CarpetCoordinate(posX, posY));


            }
        } catch (IOException e) {
            logger.error("Connection to carpet failed.", e);
        }

    }

    /**
     * Use this to mock the carpet data. This "simulates" carpet data.
     */
    public void mockReader() {
        logger.info("Trying to read mocket carpet data...");

        Timer timer = new Timer();
        try {
            File file = new File("Sensfloor_Logging.txt");    //creates a new file instance
            FileReader fr = new FileReader(file);   //reads the file
            BufferedReader br = new BufferedReader(fr);  //creates a buffering character input stream
            StringBuffer sb = new StringBuffer();    //constructs a string buffer with no characters
            String line;
            while ((line = br.readLine()) != null) {

                try {
                    Thread.sleep(1100);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                int position1;
                int position2;


                //Check if it's the carpet mat or the  carpet
                int carpetID = line.charAt(19) - '0';
                if(carpetID == 4){
                    position1 = 1;
                    position2 = 7;
                }else{
                     position1 = line.charAt(21) - '0';
                     position2 = line.charAt(23) - '0';
                }

                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            carpet.changeActiveField(position2, position1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };

                timer.schedule(task, 0);
                logger.info("New position: {}. {}.", position2, position1);


            }

            fr.close();    //closes the stream and release the resources
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}


