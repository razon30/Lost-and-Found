package razon.lostandfound.utils;

import android.app.Activity;
import android.content.Intent;

import razon.lostandfound.R;

/**
 * Created by razon30 on 14-05-17.
 */

public class SimpleActivityTransition {


    public static void goToNextActivity(Activity activity, final Class<? extends Activity> activityToOpen) {
        Intent intent = new Intent(activity, activityToOpen);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_right, R.anim.fade_out);
    }


    public static void goToPreviousActivity(Activity activity, final Class<? extends Activity> activityToOpen) {
        Intent intent = new Intent(activity, activityToOpen);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.overridePendingTransition(R.anim.slide_in_from_left, R.anim.fade_out);
    }


}
