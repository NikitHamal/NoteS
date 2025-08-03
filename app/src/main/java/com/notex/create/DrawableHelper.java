package com.notex.create;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.core.content.ContextCompat;

public class DrawableHelper {
    private static final String TAG = "DrawableHelper";
    
    /**
     * Safely loads a drawable resource with fallback handling
     */
    public static Drawable getDrawableSafely(Context context, int resourceId) {
        try {
            return ContextCompat.getDrawable(context, resourceId);
        } catch (Exception e) {
            Log.w(TAG, "Failed to load drawable resource: " + resourceId, e);
            return null;
        }
    }
    
    /**
     * Safely loads a drawable resource by name with fallback handling
     */
    public static Drawable getDrawableSafely(Context context, String resourceName) {
        try {
            int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            if (resourceId != 0) {
                return ContextCompat.getDrawable(context, resourceId);
            } else {
                Log.w(TAG, "Drawable resource not found: " + resourceName);
                return null;
            }
        } catch (Exception e) {
            Log.w(TAG, "Failed to load drawable resource: " + resourceName, e);
            return null;
        }
    }
    
    /**
     * Checks if a drawable resource exists
     */
    public static boolean drawableExists(Context context, String resourceName) {
        try {
            int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            return resourceId != 0;
        } catch (Exception e) {
            Log.w(TAG, "Error checking if drawable exists: " + resourceName, e);
            return false;
        }
    }
    
    /**
     * Gets a safe drawable ID that won't cause crashes
     */
    public static int getSafeDrawableId(Context context, String resourceName) {
        try {
            int resourceId = context.getResources().getIdentifier(resourceName, "drawable", context.getPackageName());
            if (resourceId != 0) {
                return resourceId;
            } else {
                Log.w(TAG, "Drawable resource not found: " + resourceName);
                // Return a safe default drawable if available
                return android.R.drawable.ic_menu_info_details;
            }
        } catch (Exception e) {
            Log.w(TAG, "Error getting drawable ID: " + resourceName, e);
            return android.R.drawable.ic_menu_info_details;
        }
    }
}