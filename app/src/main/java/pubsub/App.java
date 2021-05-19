package pubsub;

import pubsub.publisher.Publisher;
import pubsub.subscriber.Subscriber;

import java.io.IOException;

public class App {

    public static final String PUBLISHER_APP = "pub";
    public static final String SUBSCRIBER_APP = "sub";

    public static void main(String[] args) throws IOException {
        if (!argValid(args)) {
            printHelp();
            return;
        }

        String type = args[0];

        if (PUBLISHER_APP.equalsIgnoreCase(type)) {
            new Publisher(args[1], Double.parseDouble(args[2])).run();
        } else if (SUBSCRIBER_APP.equalsIgnoreCase(type)) {
            new Subscriber(args[1]).run();
        } else {
            throw new IllegalArgumentException("Unknown type '" + type + "'");
        }
    }

    private static boolean argValid(String[] args) {
        if (args.length == 0) {
            return false;
        }

        String type = args[0];

        return PUBLISHER_APP.equalsIgnoreCase(type) && args.length == 3 ||
                SUBSCRIBER_APP.equalsIgnoreCase(type) && args.length == 2;
    }

    private static void printHelp() {
        System.out.println("use with parameters:");
        System.out.println();
        System.out.println("***** Subscriber *******");
        System.out.println("\t sub <pattern>");
        System.out.println("\t example: sub ch*");
        System.out.println("************************");
        System.out.println();
        System.out.println("****** Publisher *******");
        System.out.println("\t pub <channel> <rate/sec>");
        System.out.println("\t example: pub ch1 1000");
        System.out.println("************************");
    }
}