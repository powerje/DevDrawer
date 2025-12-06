# Keystore Credentials

**IMPORTANT: Keep these credentials safe!**

If you lose this keystore or password, you will permanently lose the ability to update your application on the Google Play Store.

*   **Keystore File:** `release.keystore` (Located in the project root)
*   **Key Alias:** `devdrawer`
*   **Password:** `oQ8rLnUFt4RMtjpwjeNlfA` (Used for both Store and Key password)

## Setup Details
1.  **Keystore File:** Generated at `release.keystore`.
2.  **Configuration:** A `keystore.properties` file has been created to provide these credentials to the build system.
3.  **Git Safety:** Both `release.keystore` and `keystore.properties` have been added to `.gitignore` to prevent accidental commits.

## Google Play Upload
To upload your app via Fastlane, you will need a Service Account Key from the Google Play Console.
1.  Go to **Google Play Console** -> **Setup** -> **API Access**.
2.  Create a Service Account and download the JSON key.
3.  Save it as `fastlane/api-key.json` (This file is also gitignored).
