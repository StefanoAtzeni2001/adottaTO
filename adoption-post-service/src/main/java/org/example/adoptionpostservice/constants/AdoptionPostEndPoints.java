package org.example.adoptionpostservice.constants;


    /**
     * Contains the URL constant used by AdoptionPostController
    */
    public final class AdoptionPostEndPoints {

        /** URL to retrieve a filtered list of adoption posts based on specific criteria */
        public static final String GET_FILTERED_ADOPTION_POSTS = "/get-list-filtered";

        /** URL to retrieve the details of a specific adoption post by its ID */
        public static final String GET_ADOPTION_POST_BY_ID = "/get-by-id/{postId}";

        /** URL to create a new adoption post */
        public static final String CREATE_ADOPTION_POST = "/create-adoption-post";

        /** URL to delete a specific adoption post by its ID */
        public static final String DELETE_ADOPTION_POST_BY_ID = "/delete-by-id/{postId}";

        /** URL to update an existing adoption post by its ID */
        public static final String UPDATE_ADOPTION_POST_BY_ID = "/update-by-id/{postId}";

        /** URL to retrieve a list of owned posts*/
        public static final String GET_ADOPTION_POSTS_BY_OWNER = "/get-my-owned-posts";

        /** URL to retrieve a list of adopted  posts*/
        public static final String GET_ADOPTION_POSTS_BY_ADOPTER = "/get-my-adopted-posts";


        private AdoptionPostEndPoints() {}
    }

