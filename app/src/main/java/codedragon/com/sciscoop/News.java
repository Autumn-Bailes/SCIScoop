package codedragon.com.sciscoop;

/**
 * {@link News} holds all the information for a single news article.
 * Each object has e properties: section, title, author, date, and url.
 */

public class News {

    // Number for the section type of the news article.
    private String mSection;

    // Title of the news article.
    private String mTitle;

    // Name of the author
    private String mAuthor;

    // Date of the event that occurred.
    private long mDate;

    // Website URL of the news article.
    private String mUrl;

    /*
     * Create a new News object.
     *
     * @param section is the name of the section type (e.g. Science)
     * @param title is the place the event occurred (e.g. Washington)
     * @param author is the name of the author
     * @param date is the date of the event (e.g Nov 1, 2018)
     */

    public News(String section, String title, String author, long date, String url) {
        mSection = section;
        mTitle = title;
        mAuthor = author;
        mDate = date;
        mUrl = url;
    }

    public String getSection() {
        return mSection;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public long getDate() {
        return mDate;
    }

    public String getUrl() {
        return mUrl;
    }
}
