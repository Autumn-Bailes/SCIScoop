package codedragon.com.sciscoop;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /** Tag for log messages */

    /**
     * Query the URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link NewsLoader}
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public NewsLoader(Context context, String url) {
        super( context );
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // Perform the network request, parse the response and extract a list of news articles.
        List<News> articles = Query.fetchNewsData( mUrl );
        return articles;
    }
}
