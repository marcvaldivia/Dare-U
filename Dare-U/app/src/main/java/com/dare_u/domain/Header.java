package com.dare_u.domain;

/**
 * Created by Marc Valdivia, Dare-U 2015.
 */
public class Header {

    private String title;

    /**
     * Empty constructor.
     */
    public Header() {

    }

    /**
     * Constructor with all the fields of the class.
     *
     * @param title
     */
    public Header(String title) {
        this.setTitle(title);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
