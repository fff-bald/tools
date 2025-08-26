package demo.virtualthread;

public class Client {

    public static void main(String[] args) throws InterruptedException {
//        Thread.Builder.OfVirtual builder = Thread.ofVirtual().name("client", 1);
//        for (int i = 0; i < 100000; i++) {
//            builder.start(() -> {
//                try (
//                        Socket serverSocket = new Socket("localhost", 9999);
//                        BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
//                        PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
//                ) {
//                    out.println("hello");
//                    String inputLine;
//                    while ((inputLine = in.readLine()) != null) {
//                        System.out.println(inputLine);
//                    }
//                } catch (UnknownHostException e) {
//                    System.err.println("Don't know about localhost");
//                } catch (IOException e) {
//                    System.err.println("Couldn't get I/O for the connection to localhost");
//                }
//            });
//        }
//        Thread.sleep(1000000000);
    }
}
