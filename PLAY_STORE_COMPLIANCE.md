# Google Play Store Compliance Guide

To get DevDrawer approved, you must address the following requirements in the Google Play Console.

## 1. Privacy Policy
Since DevDrawer uses the `QUERY_ALL_PACKAGES` permission, a Privacy Policy is **mandatory**.

*   **Action:** Host the contents of `PRIVACY_POLICY.md` (found in the root of this project) on a public URL. GitHub Pages is a great free option.
*   **Update:** Once hosted, replace the placeholder URL in `fastlane/metadata/android/en-US/privacy_url.txt` with your actual link.

## 2. Sensitive Permissions Declaration (`QUERY_ALL_PACKAGES`)
You will be asked to declare why the app uses this permission.

*   **Question:** "Does your app's core feature require broad access to installed apps?"
*   **Answer:** **Yes**
*   **Core Purpose:** Search and Discovery (or Launcher/Utility).
*   **Video Instructions:** You may be asked to provide a video demonstrating the feature. Record a short clip showing:
    1.  Opening the widget configuration.
    2.  Entering a pattern (e.g., `com.google.*`).
    3.  The widget populating with matching apps.
    4.  Launching an app from the widget.
    
    *This proves the permission is essential for the user-facing functionality.*

## 3. Data Safety Section
In the "App Content" -> "Data Safety" section:
*   **Does your app collect or share any of the required user data types?** -> **No**.
*   (The app accesses data locally, but does not *collect* it off-device).

## 4. Target Audience
*   Select **18+**. This is a developer tool, not designed for children. This avoids strict family policy requirements.
