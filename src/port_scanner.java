import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.*;
import java.net.*;
import java.util.concurrent.*;

public class port_scanner {
    public static void main(final String...args) throws InterruptedException, ExecutionException {
        final ExecutorService es = Executors.newCachedThreadPool();
        System.out.println("Input the ip address you would like to scan for open ports. ");
        Scanner inputScanner = new Scanner(System.in);
        final String ip = inputScanner.nextLine();
        final int timeout = 200;
        final List<Future<ScanResult>>futures = new ArrayList<Future<ScanResult>>();
        for (int port = 1; port<= 65535; port++){
            futures.add(portIsOpen(es, ip, port, timeout));
        }
        es.awaitTermination(200L, TimeUnit.MILLISECONDS);
        int openPorts = 0;
        for (final Future<ScanResult> f: futures){
            if (f.get().isOpen()){
                openPorts++;
                System.out.println(f.get().getPort());
            }
        }
        System.out.println("There are " +openPorts + "open ports on host "+ip+" (Timelapse "
                +timeout+ "ms)");

        es.shutdown();
    }


    public static void run() {
        try {
            ServerSocket server = new ServerSocket( 8090 );
            Date creationDate = new Date();
            System.out.println(creationDate );

            Socket uniqueClient = server.accept();

            ObjectOutputStream out = new ObjectOutputStream(
                    uniqueClient.getOutputStream());

            out.writeObject( creationDate );
            out.close();

            uniqueClient.close();

            server.close();

        }catch( IOException ioe ) {}

    }


    public static Future<ScanResult> portIsOpen(final ExecutorService es, final String ip, final int port,
                                                final int timeout)
    {
        return es.submit(new Callable<ScanResult>() {
            @Override
            public ScanResult call() {
                try {
                    Socket socket = new Socket();
                    socket.connect(new InetSocketAddress(ip , port), timeout);
                    socket.close();
                    return new ScanResult (port, true);
                }catch (Exception ex){
                    return new ScanResult(port, true);
                }
            }
        });
    }

    public static class ScanResult {
        private int port;
        private boolean isOpen;

        public ScanResult(int port, boolean isOpen){
            super();
            this.port = port;
            this.isOpen = isOpen;
        }

        public int getPort(){
            return port;
        }

        public boolean isOpen(){
            return isOpen;
        }

        public void setOpen(boolean isOpen){
            this.isOpen = isOpen;
        }
    }
}

