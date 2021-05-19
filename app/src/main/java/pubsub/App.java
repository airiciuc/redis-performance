package pubsub;

import pubsub.publisher.Publisher;
import pubsub.subscriber.Subscriber;

public class App {

    public static final String PUBLISHER_APP = "pub";
    public static final String SUBSCRIBER_APP = "sub";

    public static void main(String[] args) {
        if (!argValid(args)) {
            printHelp();
            return;
        }

        String type = args[0];

        if (PUBLISHER_APP.equalsIgnoreCase(type)) {
            new Publisher(args[1],
                    Integer.parseInt(args[2]),
                    Integer.parseInt(args[3]),
                    Double.parseDouble(args[4]),
                    Double.parseDouble(args[5])).run();
        } else if (SUBSCRIBER_APP.equalsIgnoreCase(type)) {
            new Subscriber(args[1], Integer.parseInt(args[2])).run();
        } else {
            throw new IllegalArgumentException("Unknown type '" + type + "'");
        }
    }

    private static boolean argValid(String[] args) {
        if (args.length == 0) {
            return false;
        }

        String type = args[0];

        return PUBLISHER_APP.equalsIgnoreCase(type) && args.length == 6 ||
                SUBSCRIBER_APP.equalsIgnoreCase(type) && args.length == 3;
    }

    private static void printHelp() {
        System.out.println("use with parameters:");
        System.out.println();
        System.out.println("***** Subscriber *******");
        System.out.println("\t sub <pattern> <channels?");
        System.out.println("\t example: sub ch 100");
        System.out.println("************************");
        System.out.println();
        System.out.println("****** Publisher *******");
        System.out.println("\t pub <channel> <channels> <initialRounds> <initialRateIncrement> <rateIncrement>");
        System.out.println("\t example: pub ch 100 10 200 100");
        System.out.println("************************");
    }
}
