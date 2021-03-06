import java.net.*;
import java.io.*;

 class MyRunnable implements Runnable  {
    Socket socket;
    public MyRunnable(Socket socket){
        this.socket = socket;
    }
    public void run(){
        try {
            InputStream serverInput = socket.getInputStream();
            OutputStream serverOutput = socket.getOutputStream();
            StringBuilder sb = new StringBuilder();

            socket.setSoTimeout(5000);
            int byteLen = 0;
            byte[] inputBuffer = new byte[1024];
            while(byteLen != -1){
                try {
                    byteLen = serverInput.read(inputBuffer);
                    String msg = convToString(inputBuffer, byteLen);
                    if(byteLen != -1)
                        sb.append(msg);
                } catch (Exception e) {
                    byteLen = -1;
                }
            }
            String[] URLSplit = sb.toString().split("[/ & = ?]");
            StringBuilder sbOutput = new StringBuilder();
            String response = "";

            try {
                if(URLSplit[0].equals("GET") &&
                        URLSplit[2].equals("ask") &&
                        URLSplit[3].equals("hostname") &&
                        URLSplit[5].equals("port") &&
                        (URLSplit[7].equals("HTTP") || URLSplit[9].equals("HTTP") ))
                {
                    if (URLSplit[7].equals("string")) {
                        response = askServer(URLSplit[4], Integer.parseInt(URLSplit[6]), URLSplit[8]);
                    } else {
                        response = askServer(URLSplit[4], Integer.parseInt(URLSplit[6]));
                    }
                    sbOutput.append("HTTP/1.1 200 OK\r\n\r\n");
                    sbOutput.append(response);
                } else {
                    sbOutput.append("HTTP/1.1 400 Bad Request\r\n\r\n HTTP/1.1 400 Bad Request");
                }
            } catch(Exception e) {
                sbOutput.append("HTTP/1.1 404 Not Found\r\n\r\n HTTP/1.1 404 Not Found");
            }
            serverOutput.write(convToByte(sbOutput.toString()));
            System.out.println("Connection Closed");
            socket.close();
        } catch(Exception e){
            System.out.println(e);
        }
    }

     private static String askServer(String hostname, int port, String ToServer) throws  IOException {
         Socket clientSocket = new Socket(hostname, port);
         InputStream clientInput = clientSocket.getInputStream();
         OutputStream clientOutput = clientSocket.getOutputStream();
         clientSocket.setSoTimeout(3000);
         clientOutput.write(convToByte(ToServer));
         StringBuilder sb = new StringBuilder();
         int byteLen = 0;
         byte[] inputBuffer = new byte[1024];
         while(byteLen != -1){
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

     private static String askServer(String hostname, int port) throws  IOException {
         Socket clientSocket = new Socket(hostname, port);
         InputStream clientInput = clientSocket.getInputStream();
         clientSocket.setSoTimeout(3000);
         StringBuilder sb = new StringBuilder();
         int byteLen = 0;
         byte[] inputBuffer = new byte[1024];
         while(byteLen != -1){
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
}


public class ConcHTTPAsk {
    public static void main( String[] args)  throws IOException {
        int port = Integer.parseInt(args[0] );
        ServerSocket serverSocket = new ServerSocket(port);

        while(true) {
            System.out.println("Waiting for server");
            Socket socket = serverSocket.accept();
            Runnable r = new Thread(new MyRunnable(socket));
            new Thread(r).start();
        }
    }
}

