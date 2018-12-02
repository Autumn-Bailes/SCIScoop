package codedragon.com.sciscoop;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_settings );
    }

    public static class NewsPreferenceFragment extends PreferenceFragment
            implements Preference.OnPreferenceChangeListener {

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate( savedInstanceState );
            addPreferencesFromResource( R.xml.settings_main );

            Preference orderBy = findPreference( getString( R.string.settings_order_by_key ) );
            bindPreferenceToValue( orderBy );
        }

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            String stringValue = newValue.toString();

            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int preferenceIndex = listPreference.findIndexOfValue( stringValue );
                if (preferenceIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary( labels[preferenceIndex] );
                }
            } else {
                preference.setSummary( stringValue );
            }

            return true;
        }

        public void bindPreferenceToValue(Preference preference) {
            preference.setOnPreferenceChangeListener( this );
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(
                    preference.getContext() );
            String preferenceString = preferences.getString( preference.getKey(), "" );
            onPreferenceChange( preference, preferenceString );
        }
    }
}
