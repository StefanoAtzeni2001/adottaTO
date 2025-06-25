package org.example.adoptionpostservice.constants;


    /**
     * Contains the URL constant used by AdoptionPostController
    */
    public final class AdoptionPostEndPoints {

        /** URL to retrieve a filtered list of adoption posts based on specific criteria */
        public static final String GET_FILTERED_ADOPTION_POSTS = "/get/list";

        /** URL to retrieve the details of a specific adoption post by its ID */
        public static final String GET_ADOPTION_POST_BY_ID = "/get/post/{postId}";

        /** URL to create a new adoption post */
        public static final String CREATE_ADOPTION_POST = "/post/create";

        /** URL to delete a specific adoption post by its ID */
        public static final String DELETE_ADOPTION_POST_BY_ID = "/post/delete/{postId}";

        /** URL to update an existing adoption post by its ID */
        public static final String UPDATE_ADOPTION_POST_BY_ID = "/post/update/{postId}";

        /** URL to retrieve a list of owned posts*/
        public static final String GET_ADOPTION_POSTS_BY_OWNER = "/my/owned";

        /** URL to retrieve a list of adopted posts*/
        public static final String GET_ADOPTION_POSTS_BY_ADOPTER = "/my/adopted";


        private AdoptionPostEndPoints() {}
    }

