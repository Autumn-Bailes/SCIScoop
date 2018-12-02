package codedragon.com.sciscoop;

import android.app.LoaderManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ScienceActivity extends AppCompatActivity
        implements LoaderCallbacks<List<News>> {

    private static final String LOG_TAG = ScienceActivity.class.getName();

    /**
     * URL for the news data from the Guardian API data set
     */
    private static final String NEWS_REQUEST_URL =
            "https://content.guardianapis.com/search?show-tags=contributor&q=science&api-key=test";

    // Constant value for the news loader ID. Any integer can be chosen.
    private static final int NEWS_LOADER_ID = 1;

    // Adapter for the list of news articles
    private NewsAdapter mAdapter;

    // Empty state textView
    private TextView mEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_science );

        // Find a reference to the {@link ListView}
        ListView newsListView = findViewById( R.id.list );

        mEmptyView = findViewById( R.id.empty );
        newsListView.setEmptyView( mEmptyView );

        // Create an adapter that takes in an empty list of news objects
        mAdapter = new NewsAdapter( this, new ArrayList<News>() );

        newsListView.setAdapter( mAdapter );

        newsListView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                // Find the current news article that was clicked on
                News currentArticle = mAdapter.getItem( position );

                // Convert the String URL into a URI object
                Uri newsUri = null;
                if (currentArticle != null) {
                    newsUri = Uri.parse( currentArticle.getUrl() );
                }

                // Create a new intent to view the news URI
                Intent newsIntent = new Intent( Intent.ACTION_VIEW, newsUri );

                PackageManager packageManager = getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities( newsIntent, 0 );
                boolean isIntentSafe = activities.size() > 0;

                // If it is safe, start activity.
                if (isIntentSafe) {
                    startActivity( newsIntent );
                }
            }
        } );

        // Get a reference to the ConnectivityManager to check the state of the network connectivity
        ConnectivityManager connectivityManager = (ConnectivityManager)
                getSystemService( Context.CONNECTIVITY_SERVICE );

        NetworkInfo networkInfo = null;
        if (connectivityManager != null) {
            networkInfo = connectivityManager.getActiveNetworkInfo();
        }

        // If there is a network connection then fetch the data
        if (networkInfo != null && networkInfo.isConnected()) {
            // Get a reference to the LoaderManager
            LoaderManager loaderManager = getLoaderManager();

            // Initialize the loader
            loaderManager.initLoader( NEWS_LOADER_ID, null, this );
        } else {
            View progressBar = findViewById( R.id.progress_bar_circle );
            progressBar.setVisibility( View.GONE );

            // Update empty state with no connection error message
            mEmptyView.setText( R.string.no_connection );
        }
    }

    public Loader<List<News>> onCreateLoader(int i, Bundle bundle) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences( this );

        String orderBy = sharedPreferences.getString(
                getString( R.string.settings_order_by_key ),
                getString( R.string.settings_order_by_default ) );

        Uri uri = Uri.parse( NEWS_REQUEST_URL );
        Uri.Builder builder = uri.buildUpon();
        builder.appendQueryParameter( "order-by", orderBy );

        // Create a new loader for the given URL
        return new NewsLoader( this, builder.toString() );
    }


    @Override
    public void onLoadFinished(Loader<List<News>> loader, List<News> articles) {
        View progressbar = findViewById( R.id.progress_bar_circle );
        progressbar.setVisibility( View.GONE );

        // Set empty state to display "No news"
        mEmptyView.setText( R.string.no_news );

        // Clear the adapter of previous data
        mAdapter.clear();

        // If there is a list of news add it to the adapter's data.
        if (articles != null && !articles.isEmpty()) {
            mAdapter.addAll( articles );
        }
    }

    @Override
    public void onLoaderReset(Loader<List<News>> loader) {
        mAdapter.clear();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.main, menu );
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        int id = menuItem.getItemId();

        if (id == R.id.settings) {
            Intent settings = new Intent( this, SettingsActivity.class );
            startActivity( settings );
            return true;
        }
        return super.onOptionsItemSelected( menuItem );
    }
}
