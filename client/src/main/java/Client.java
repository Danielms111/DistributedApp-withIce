import java.util.Scanner;

public class Client {
    public static void main(String[] args) {
        java.util.List<String> extraArgs = new java.util.ArrayList<>();

        try (com.zeroc.Ice.Communicator communicator = com.zeroc.Ice.Util.initialize(args, "client.cfg"/*, extraArgs*/)) {
            //com.zeroc.Ice.ObjectPrx base = communicator.stringToProxy("SimplePrinter:default -p 10000");
            Demo.PrinterPrx twoway = Demo.PrinterPrx.checkedCast(
                communicator.propertyToProxy("Printer.Proxy")).ice_twoway().ice_secure(false);
            Demo.PrinterPrx printer = twoway.ice_oneway();

            if (printer == null) {
                throw new Error("Invalid proxy");
            }

            Scanner scanner = new Scanner(System.in);
            String message;
            do {
                System.out.print("Enter message (or 'exit' to quit): ");
                message = scanner.nextLine();
                
                if (!message.equals("exit")) {
                    String formattedMessage = formatMessage(message);
                    printer.printString(formattedMessage); 
                }
            } while (!message.equals("exit"));
        }
    }
    
    private static String formatMessage(String message) {
        String username = System.getProperty("user.name");
        String hostname = "LAPTOP-IL6REUHO";
        return username + ":" + hostname + ":" + message;
    }
}