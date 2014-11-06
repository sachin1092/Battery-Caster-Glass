package glass.batterycaster.saphion.battercaster;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;

/**
 * A transparent {@link Activity} displaying a "Stop" options menu to remove the {@link LiveCard}.
 */
public class LiveCardMenuActivity extends Activity {

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
        // Open the options menu right away.
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.live_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_stop:
                // Stop the service which will unpublish the live card.
                stopService(new Intent(this, LiveCardService.class));
                return true;
            case R.id.action_toggle_temp:
                // Stop the service which will unpublish the live card.
//                stopService(new Intent(this, LiveCardService.class));
                getSharedPreferences(LiveCardService.PREF_NAME,
                        Context.MODE_MULTI_PROCESS).edit().putBoolean(LiveCardService.MAIN_TEMP, !getSharedPreferences(LiveCardService.PREF_NAME,
                        Context.MODE_MULTI_PROCESS).getBoolean(
                        LiveCardService.MAIN_TEMP, true)).commit();
                sendBroadcast(new Intent("saphion.battery.update"));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        super.onOptionsMenuClosed(menu);
        // Nothing else to do, finish the Activity.
        finish();
    }
}
