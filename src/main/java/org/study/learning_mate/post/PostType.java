package org.study.learning_mate.post;

public enum PostType {
    DEMAND_LECTURE("DEMAND_LECTURE"),
    LECTURE("LECTURE");

    private String postType;

    PostType(String postType) {
        this.postType = postType;
    }

    public String getPostType() {
        return this.postType;
    }

    public static PostType getPostType(String postType) {
        for (PostType p : PostType.values()) {
            if (p.getPostType().equals(postType)) {
                return p;
            }
        }
        throw new IllegalArgumentException("No enum constant for post type : " + postType);
    }
}
