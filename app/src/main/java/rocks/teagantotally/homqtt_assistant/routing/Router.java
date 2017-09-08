package rocks.teagantotally.homqtt_assistant.routing;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import rocks.teagantotally.homqtt_assistant.BuildConfig;
import rocks.teagantotally.homqtt_assistant.R;
import rocks.teagantotally.homqtt_assistant.events.NavigationEvent;
import rocks.teagantotally.homqtt_assistant.ui.activities.BaseActivity;

/**
 * Created by tglenn on 8/30/17.
 */

public abstract class Router {
    public static final String FRAGMENT = "fragment";
    public static final String EXTRAS = "extras";
    public static final String KEY = "key";
    public static final String BACKSTACK = "backstack";

    private static final String TAG = "Router";

    private static Map<String, Route> routeMap = new HashMap<>();

    static {

    }

    /**
     * Registers a route
     *
     * @param route    The uri for the route
     * @param activity The activity to load
     * @param fragment The fragment to display
     */
    public static void registerRoute(String route,
                                     Class activity,
                                     Class fragment) {
        routeMap.put(route,
                     new Route(activity,
                               fragment));
    }

    /**
     * Executes logic for a given navigation event
     *
     * @param activity        Current activity
     * @param navigationEvent Navigation event data
     * @return
     */
    public static boolean navigateTo(BaseActivity activity,
                                     NavigationEvent navigationEvent) {
        String routeKey = navigationEvent.getTo();
        Route route = getRoute(navigationEvent.getTo());
        if (route == null) {
            // TODO : Web url
            return false;
        }

        if (!isCurrentActivity(activity,
                               route)) {
            return navigateToActivity(activity,
                                      route,
                                      routeKey,
                                      navigationEvent.shouldAddToBackstack(),
                                      navigationEvent.getFlags());
        } else if (route.getFragment() != null) {
            //Already on the correct activity. What about the fragment?
            return navigateToFragment(activity,
                                      navigationEvent,
                                      route);
        }
        if (!BuildConfig.DEBUG) {
            return false;
        }

        Toast.makeText(activity,
                       "Already on the requested screen.",
                       Toast.LENGTH_SHORT)
             .show();
        return false;
    }

    private static boolean navigateToFragment(BaseActivity activity,
                                              NavigationEvent navigationEvent,
                                              Route route) {
        FragmentManager fragmentManager = activity.getSupportFragmentManager();

        boolean hasFragment = fragmentManager.getBackStackEntryCount() > 0;
        if (hasFragment
            && fragmentManager.getBackStackEntryAt(
                  fragmentManager.getBackStackEntryCount() - 1)
                              .getName()
                              .equals(navigationEvent.getTo())) {
            Toast.makeText(activity,
                           "Already on the requested screen.",
                           Toast.LENGTH_SHORT)
                 .show();
            return false;
        }
        try {
            Fragment fragment = (Fragment) route.getFragment()
                                                .newInstance();
            fragment.setArguments(route.getExtras());
            FragmentTransaction transaction = fragmentManager.beginTransaction()
                                                             .replace(R.id.container,
                                                                      fragment,
                                                                      navigationEvent.getTo());
            if (navigationEvent.shouldAddToBackstack()) {
                transaction.addToBackStack(navigationEvent.getTo());
            }
            transaction.commit();

            return true;
        } catch (Exception e) {
            Log.e(TAG,
                  "Failed to parse route",
                  e);
            Toast.makeText(activity,
                           "Unable to load fragment",
                           Toast.LENGTH_SHORT)
                 .show();
            return false;
        }
    }

    private static boolean isCurrentActivity(BaseActivity activity,
                                             Route route) {
        return route.getActivity()
                    .getSimpleName()
                    .equals(activity.getClass()
                                    .getSimpleName());
    }

    private static boolean navigateToActivity(BaseActivity activity,
                                              Route route,
                                              String routeKey,
                                              boolean addToBackStack,
                                              Integer flags) {
        Intent intent = new Intent(activity,
                                   route.getActivity());
        intent.putExtra(FRAGMENT,
                        route.getFragment());
        intent.putExtra(EXTRAS,
                        route.getExtras());
        intent.putExtra(KEY,
                        routeKey);
        intent.putExtra(BACKSTACK,
                        addToBackStack);
        if (!addToBackStack) {
            intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        }
        if (flags != null) {
            intent.setFlags(intent.getFlags() | flags);
        }
        activity.startActivity(intent);
        return true;
    }

    static Route getRoute(String uri) {
        for (String route : routeMap.keySet()) {
            Bundle routeExtras = new Bundle();
            if (matchesRoute(uri,
                             route,
                             routeExtras)) {
                Route matchedRoute = routeMap.get(route);
                if (matchedRoute.getExtras() != null) {
                    routeExtras.putAll(matchedRoute.getExtras());
                }
                return new Route(matchedRoute.getActivity(),
                                 matchedRoute.getFragment(),
                                 routeExtras);
            }
        }

        // No routes, assume web view
        return null;
    }

    static boolean matchesRoute(String requestedRoute,
                                String routeToCheck,
                                @NonNull Bundle properties) {
        Objects.requireNonNull(properties,
                               "Route bundle properties cannot be null");
        if (TextUtils.isEmpty(routeToCheck) || TextUtils.isEmpty(requestedRoute)) {
            return false;
        }

        if (TextUtils.equals(routeToCheck,
                             requestedRoute)) {
            // Exact match
            return true;
        }

        Uri requestedUri = Uri.parse(requestedRoute);
        Uri routeUri = Uri.parse(routeToCheck);

        if (!TextUtils.equals(requestedUri.getScheme(),
                              routeUri.getScheme())) {
            // Scheme mismatch
            return false;
        }

        if (!TextUtils.equals(requestedUri.getAuthority(),
                              routeUri.getAuthority())) {
            // Authority mismatch
            return false;
        }

        List<String> requestedSegments = requestedUri.getPathSegments();
        List<String> compareSegments = routeUri.getPathSegments();

        if (compareSegments.size() != requestedSegments.size()) {
            return false;
        }

        for (int i = 0; i < compareSegments.size(); i++) {
            String requestedSegment = requestedSegments.get(i);
            String compareSegment = compareSegments.get(i);

            if (compareSegment.startsWith(":")) {
                properties.putString(compareSegment.substring(1),
                                     requestedSegment);
            } else if (!TextUtils.equals(requestedSegment,
                                         compareSegment)) {
                return false;
            }
        }

        if (!TextUtils.isEmpty(requestedUri.getQuery())) {
            for (String queryParameterName : requestedUri.getQueryParameterNames()) {
                properties.putString(queryParameterName,
                                     requestedUri.getQueryParameter(queryParameterName));
            }
        }

        return true;
    }
}
