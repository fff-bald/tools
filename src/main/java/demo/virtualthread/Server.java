package demo.virtualthread;

public class Server {

    public static void main(String[] args) {
//        Set<String> platformSet = CollectionUtil.hashSet();
//        new Thread(() -> {
//            try {
//                Thread.sleep(10000);
//                System.out.println(platformSet.size());
//            } catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//        try (ServerSocket serverSocket = new ServerSocket(9999)) {
//            Thread.Builder.OfVirtual clientThreadBuilder = Thread.ofVirtual().name("client", 1);
//            while (true) {
//                Socket clientSocket = serverSocket.accept();
//                clientThreadBuilder.start(() -> {
//                    String platformName = Thread.currentThread().toString().split("@")[1];
//                    platformSet.add(platformName);
//                    try (
//                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
//                    ) {
//                        String inputLine;
//                        while ((inputLine = in.readLine()) != null) {
//                            System.out.println(inputLine + "（from:" + Thread.currentThread() + "）");
//                            out.println(inputLine);
//                        }
//                    } catch (IOException e) {
//                        System.err.println(e.getMessage());
//                    }
//                });
//            }
//        } catch (IOException e) {
//            System.err.println("Exception caught when trying to listen on port 999");
//            System.err.printf(e.getMessage());
//        }
    }
}