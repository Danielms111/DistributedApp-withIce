import java.io.*;
import java.util.*;
import java.net.*;

class PrinterI implements Demo.Printer {
    public void printString(String s, com.zeroc.Ice.Current current) {
        System.out.println("COMANDO DIGITADO:" + s);
        String[] parts2 = s.split(":");
        if (parts2.length >= 3) {
            String messagePart = parts2[2];
            if (s.equalsIgnoreCase("exit")) {
                System.out.println("Goodbye!");
            }else if (s.endsWith("listifs")) {
                System.out.println("------------------------------------------------------------------");
                System.out.println("List of interfaces: " + getNetworkInterfaces());
                System.out.println("------------------------------------------------------------------");
            } else if (messagePart.startsWith("listports")) {
                System.out.println("------------------------------------------------------------------");
                String[] parts = s.split(" ");
                if (parts.length > 1) {
                    String ipAddress = parts[1];
                    System.out.println("Open ports for " + ipAddress);
                    // ##
                    try {
                        InetAddress inetAddress = InetAddress.getByName(ipAddress);
                        for (int port = 1; port <= 535; port++) {
                            try (Socket socket = new Socket()) {
                                socket.connect(new InetSocketAddress(inetAddress, port), 50);
                                System.out.println("Open port: " + port);
                            } catch (IOException e) {
                                // Port is closed or unreachable
                            }
                        }
                    } catch (UnknownHostException e) {
                        System.out.println("Invalid IP address");
                    }
                    System.out.println("------------------------------------------------------------------");
                    //##
                    //System.out.println("Open ports for " + ipAddress + ": " + getOpenPorts(ipAddress));
                }else {
                    System.out.println("------------------------------------------------------------------");
                    System.out.println("Usage: listports <IPv4>");
                    System.out.println("------------------------------------------------------------------");
                }
            } else if (s.contains("!")) {
                System.out.println("------------------------------------------------------------------");
            //String command = messagePart.substring(1);
                int indexOfExclamation = messagePart.indexOf("!");
                String command = messagePart.substring(indexOfExclamation + 1);
                System.out.println(command);
                System.out.println("Command output: " + executeCommand(command));
                System.out.println("------------------------------------------------------------------");
            }else if(isPositiveInteger(messagePart)) {
                System.out.println("------------------------------------------------------------------");
                int number = Integer.parseInt(messagePart);
                System.out.println("Prime factors of " + number + ": " + getPrimeFactors(number));
                System.out.println("------------------------------------------------------------------");
            }else {
                System.out.println("------------------------------------------------------------------");
                System.out.println("Invalid input. Usage: <number>|listifs|listports <IPv4>|!<command>|exit");
                System.out.println("------------------------------------------------------------------");
            }
        }
        /*if (s.equalsIgnoreCase("exit")) {
            System.out.println("Goodbye!");
        } else if (s.endsWith("listifs")) {
            System.out.println("List of interfaces: " + getNetworkInterfaces());
        } else if (s.endsWith("listports")) {
            String[] parts = s.split(" ");
            if (parts.length > 1) {
                String ipAddress = parts[1];
                System.out.println("Open ports for " + ipAddress + ": " + getOpenPorts(ipAddress));
            } else {
                System.out.println("Usage: listports <IPv4>");
            }
        } else if (s.endsWith("!")) {
            String command = s.substring(1);
            System.out.println("Command output: " + executeCommand(command));
        }else {
            System.out.println("Invalid input. Usage: <number>|listifs|listports <IPv4>|!<command>|exit");
        }*/
    }

    private String getNetworkInterfaces() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            StringBuilder result = new StringBuilder();
            
            while (interfaces.hasMoreElements()) {
                NetworkInterface iface = interfaces.nextElement();
                result.append(iface.getName()).append(", ");
            }
            
            return result.toString();
        } catch (SocketException e) {
            return "Error getting network interfaces";
        }
    }

    private String getOpenPorts(String ipAddress) {
        StringBuilder openPorts = new StringBuilder();

        try {
            InetAddress inetAddress = InetAddress.getByName(ipAddress);
            Socket socket;
            
            for (int port = 1; port <= 65535; port++) {
                try {
                    socket = new Socket(inetAddress, port);
                    socket.close();
                    openPorts.append(port).append(", ");
                } catch (IOException e) {
                    // Port is likely closed
                }
            }
        } catch (UnknownHostException e) {
            return "Invalid IP address";
        }

        return openPorts.toString();
    }

    private String executeCommand(String command) {
        StringBuilder output = new StringBuilder();
        String line;

        try {
            Process process = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.getProperty("line.separator"));
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return output.toString();
    }

    private String getPrimeFactors(int number) {
        StringBuilder primeFactors = new StringBuilder();

        for (int i = 2; i <= number; i++) {
            while (number % i == 0) {
                primeFactors.append(i).append(", ");
                number /= i;
            }
        }

        return primeFactors.toString();
    }

    private boolean isPositiveInteger(String s) {
        try {
            int number = Integer.parseInt(s);
            return number > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
