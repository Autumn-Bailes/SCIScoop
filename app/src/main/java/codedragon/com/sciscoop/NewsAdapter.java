package codedragon.com.sciscoop;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * {@link NewsAdapter} knows how to create a list item layout for each
 * article in the data source, which is a list of {@link News} objects.
 */


public class NewsAdapter extends ArrayAdapter<News> {

    /**
     * Contructs a new {@link NewsAdapter}
     */
    public NewsAdapter(Context context, List<News> articles) {
        super( context, 0, articles );
    }

    @TargetApi(Build.VERSION_CODES.O)
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View newsListView = convertView;
        if (newsListView == null) {
            newsListView = LayoutInflater.from( getContext() ).inflate(
                    R.layout.news_list_item, parent, false );
        }

        // Get the {@link News} object located at this position
        News currentArticle = getItem( position );

        // Find the TextView with the Id section_name
        TextView sectionNameView = newsListView.findViewById( R.id.section_name );
        String section = null;
        if (currentArticle != null) {
            section = currentArticle.getSection();
        }
        sectionNameView.setText( section );

        // Find the TextView with the Id news_title
        TextView newsTitleView = newsListView.findViewById( R.id.news_title );
        String title = null;
        if (currentArticle != null) {
            title = currentArticle.getTitle();
        }
        newsTitleView.setText( title );

        // Find the TextView with the Id author_name
        TextView authorView = newsListView.findViewById( R.id.author_name );
        String author = null;
        if (currentArticle != null) {
            author = currentArticle.getAuthor();
        }
        authorView.setText( author );

        // Create a new date object from the date of the article
        Date date = null;
        if (currentArticle != null) {
            date = new Date( currentArticle.getDate() );
        }

        // Find the TextView with the Id news_date
        TextView dateView = newsListView.findViewById( R.id.news_date );

        // Format the date string (i.e. "Nov 1, 2018")
        String formattedDate = formatDate( date );
        dateView.setText( formattedDate );

        // Find the TextView with the Id news_time
        TextView timeView = newsListView.findViewById( R.id.news_time);

        // Format the time string (i.e. "7:30 AM")
        String formattedTime = formatTime( date );
        timeView.setText( formattedTime );

        // Return the whole list item layout
        return newsListView;
    }

    /**
     * Return the formatted date string from the date object
     */
    private String formatDate(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat( "MMM dd, yyyy", Locale.US );
        return dateFormat.format( date );
    }

    /**
     * Return the formatted time string from the date object
     */
    private String formatTime(Date date) {
        SimpleDateFormat timeFormat = new SimpleDateFormat( "h:mm a", Locale.US );
        return timeFormat.format( date );
    }
}