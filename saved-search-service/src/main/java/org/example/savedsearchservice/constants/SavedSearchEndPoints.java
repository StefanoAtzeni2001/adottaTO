package org.example.savedsearchservice.constants;

/**
 * Contains the URL constants used by SavedSearchController
 */
public class SavedSearchEndPoints {

    /** Endpoint to save a new search filter */
    public static final String SAVE_SEARCH = "/save";

    /** Endpoint to delete a saved search by ID */
    public static final String DELETE_SAVED_SEARCH = "/delete/{searchId}";

    /** Endpoint to get all saved searches for the authenticated user */
    public static final String GET_MY_SAVED_SEARCHES = "/my/saved";

    private SavedSearchEndPoints() {
        // Prevent instantiation
    }
}
