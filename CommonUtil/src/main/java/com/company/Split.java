package com.company;

/**
 * Helper class which might contain:<br>
 * user command and message got user input in UserThread <br>
 * server command and message got from console in ServerThread<br>
 * while handling their single String input
 */
public class Split {
    private final String command;
    private String message;

    /**
     * Base constructor
     *
     * @param text {@link String} - users input will be split to:<br>
     *             {@link #command} - users command<br>
     *             {@link #message} - users message
     */
    public Split(String text) {
        String[] splitText = text.split(" ");

        this.command = splitText[0];
        this.message = "";

        for (int i = 1; i < splitText.length; i++) {
            if (i == splitText.length - 1) {
                message = message.concat(splitText[i]);
                break;
            }
            message = message.concat(splitText[i] + " ");
        }
    }

    /**
     * Returns user command
     *
     * @return {@link #command} String - command of the user
     */
    public String getCommand() {
        return command;
    }

    /**
     * Returns user command
     *
     * @return {@link #message} String - message of the user
     */
    public String getMessage() {
        return message;
    }
}
