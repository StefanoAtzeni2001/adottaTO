package it.unito.chatrest.constants;

/**
 * Contains the URL constant used by ChatController
 */
public class ChatEndPoints {
    /** URL to send a new message in a new or an existing chat */
    public static final String SEND_NEW_MESSAGE = "/send";

    /** URL to get the list of chats of a specific user */
    public static final String GET_CHATS = "/chats";

    /** URL to get the messages of a specific chat */
    public static final String GET_HISTORY = "/history";

    /** URL to get only the unreaded messages of a specific chat */
    public static final String GET_UNREAD_MESSAGES = "/unread";

    /** URL to send an adoption request related to a specific chat for a specific adoption post */
    public static final String SEND_ADOPTION_REQUEST = "/send-request";

    /** URL to cancel an existing adoption request */
    public static final String DELETE_ADOPTION_REQUEST = "/cancel-request";

    /** URL to accept an adoption request */
    public static final String ACCEPT_ADOPTION_REQUEST = "/accept-request";

    /** URL to reject an adoption request */
    public static final String REJECT_ADOPTION_REQUEST = "/reject-request";

    private ChatEndPoints() {
    }
}
