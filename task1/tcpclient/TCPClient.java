package tcpclient;
import java.net.*;
import java.io.*;

public class TCPClient {
    private final static int TIMEOUT = 2500;
    private final static int TOTAL_TIME = 5000;
    private final static int BYTE_SIZE = 1024;
    /**
     * Creates a socket with hostname and port and creates the input and output stream with the connection.
     * Writes the ToServer message to the outputstream and builds the inputstream with a String Builder byte for byte.
     * Timeout set to 4 seconds.
     * @param hostname The name of the host
     * @param port The port of the server
     * @param ToServer The message to be sent.
     * @return Returns the response from the server
     * @throws IOException If error is caught
     */
    public static String askServer(String hostname, int port, String ToServer) throws  IOException {
        Socket clientSocket = new Socket(hostname, port);
        InputStream clientInput = clientSocket.getInputStream();
        OutputStream clientOutput = clientSocket.getOutputStream();
        clientSocket.setSoTimeout(TIMEOUT);
        clientOutput.write(convToByte(ToServer));
        StringBuilder sb = new StringBuilder();
        int byteLen = 0;
        byte[] inputBuffer = new byte[BYTE_SIZE];
        long startTime = System.currentTimeMillis();
        while(clientSocket.isConnected() && byteLen != -1 && timeLeft(startTime)){
            try {
                byteLen = clientInput.read(inputBuffer);
                if(byteLen != -1){
                    sb.append(convToString(inputBuffer, byteLen));
                }
            } catch (Exception e) {
                byteLen = -1;
            }
        }
        clientSocket.close();
        return sb.toString();
    }

    /**
     * Creates a socket with hostname and port and creates the inputstream with the connection. Is only called upon
     * if other askServer does not have a ToServer message.
     * Builds the inputstream response with a String builder byte for byte.
     * Timeout set to 4 seconds.
     * @param hostname The name of the host
     * @param port The port of the server
     * @return Returns the response from the server
     * @throws IOException If error is caught
     */
    public static String askServer(String hostname, int port) throws  IOException {
        Socket clientSocket = new Socket(hostname, port);
        InputStream clientInput = clientSocket.getInputStream();

        clientSocket.setSoTimeout(TIMEOUT);
        StringBuilder sb = new StringBuilder();
        int byteLen = 0;
        byte[] inputBuffer = new byte[BYTE_SIZE];
        long startTime = System.currentTimeMillis();
        while(byteLen != -1 && clientSocket.isConnected() && timeLeft(startTime)){
            try {
                byteLen = clientInput.read(inputBuffer);
                if(byteLen != -1){
                    sb.append(convToString(inputBuffer, byteLen));
                }
            } catch (Exception e) {
                byteLen = -1;
            }
        }
        clientSocket.close();
        return sb.toString();
    }

    private static String convToString(byte[] b, int len) throws UnsupportedEncodingException {
        return new String(b, 0, len, "UTF-8");
    }
    private static byte[] convToByte(String text) throws UnsupportedEncodingException {
        return (text + '\n').getBytes("UTF-8");
    }
    private static boolean timeLeft(long time){
        return (System.currentTimeMillis()-time<TOTAL_TIME);
    }
}

