package put.student.main;

import put.student.rmi.proxy.ClientServerRMIProxy;

public class ClientMain {
    /**
     *
     * @param args: {GET,PUT} local_path remote_id
     */
    public static void main(String[] args) {
        if(args.length <3 ) {
            System.err.println("Bad args");
            return;
        }

        final String method = args[0];
        final String from = args[1];
        final String to = args[2];

        try {
            switch(method) {
                case "GET":
                    new ClientServerRMIProxy().getFile(from, to);
                    break;
                case "PUT":
                    new ClientServerRMIProxy().putFile(from, to);
                    break;
                default:
                    System.err.println("Bad method");
            }
        } catch (Exception e) {
            System.err.println("Client exception: " + e.toString());
            e.printStackTrace();
        }
    }
}
