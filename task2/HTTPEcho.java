import java.net.*;
import java.io.*;

public class HTTPEcho {
    public static void main( String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        ServerSocket serverSocket = new ServerSocket(port);
        while(true){
            System.out.println("Waiting for server");
            Socket socket = serverSocket.accept();
            InputStream serverInput = socket.getInputStream();
            OutputStream serverOutput = socket.getOutputStream();
            StringBuilder sb = new StringBuilder();
            sb.append("HTTP/1.1 200 OK\r\n\r\n");
            socket.setSoTimeout(1000);
            int byteLen = 0;
            byte[] inputBuffer = new byte[1024];
            while(socket.isConnected() && byteLen != -1){
                try {
                    byteLen = serverInput.read(inputBuffer);
                    String msg = convToString(inputBuffer, byteLen);
                    if(byteLen != -1)
                        sb.append(msg);
                } catch (Exception e) {
                    byteLen = -1;
                }
            }
            String input = sb.toString();
            serverOutput.write(convToByte(input));
            socket.close();
        }
    }

    private static String convToString(byte[] b, int len) throws UnsupportedEncodingException {
        return new String(b, 0, len, "UTF-8");
    }
    private static byte[] convToByte(String text) throws UnsupportedEncodingException {
        return (text + '\n').getBytes("UTF-8");
    }
}

