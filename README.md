# HSFaces, the Android Port

This is a port of [HSFaces](https://github.com/adamfraser/HSFaces) project.  It's a memory game and Hacker School directory
for former and current students.

## Game Play

Users are shown a randomly selected subset of all Hacker Schoolers.  After a designated number of guesses, the game ends.

## Browsing for a Hacker Schooler

This app also provides a directory of Hacker Schoolers.  It's sorted (at the moment) by batch, then name.  If they've provided it online,
this allows you to visit their github, twitter, or email from within the app.  You can drill down to see more details for each 
Hacker Schooler.

### Bug reports

Should be filed as Issues on the Github page for this project.

# Onward and upward
## Current work in progress
- [x] Massive refactoring and look and feel project to make app easier to use and more delightful
- [x] Moving over from a local database model populated via a scrape to a local database model populated via the API.


## Proposed future work
- [ ] Keep list of common nicknames
- [ ] Integration with phone contacts
- [ ] Ability to message a Hacker Schooler from the app -- Requires exposing phone numbers via API


# Getting the app up and running

## How to build
1. Install [Android Studio](https://developer.android.com/sdk/installing/studio.html). Currently builds and runs on Android Studio version 0.8.2.
2. In Android Studio use the SDK Manager (button in toolbar or in menu under Tools -> Android) to install Android Support Repository, Google Repository (in Extras) and sources for API 18 (or one supported by your device)
3. Open (not import) the project in Android Studio
4. Copy over local.properties.sample to local.properties, edit if you installed Android Studio in a different location
5. "Sync Project with Gradle Files" (button in toolbar or in menu under Tools -> Android)
6. Run project (button in toolbar or in menu under Run)

__ See note about API keys before running __

## Using API Keys

The app uses Gradle properties to keep Hacker School API secrets a secret.  You'll need to supply your own API key in order for the app to funciton properly. (All Hacker Schoolers can get one from the Hacker School login portal).

Once you've obtained a super secret Key and Client ID, you'll need to replace the ones that are in this build.

To add your Secret key to the project, create a `gradle.properties` file in the project's root directory and add the following:

```
    api_secret=<your_secret_key>
    client_id=<your_client_id>
```

This keys will automatically be picked up and injected into the app as a Build config as part of the build process.  (Gradle is magic).


## A Note on IDEs

This project is not intended to be run on Eclipse.  It is not designed to run on Eclipse.  If you want to port it to Eclipse to be able to use it, by all means please do but I don't want to hear about it.


## Contributions

I love that you're contributing!  Thanks for contributing!

Pull requests are welcome, however as we're in the middle of moving over to use the API and a massive redesign, I probably won't be accepting much until most of this work is finished.
Most of this is undocumented, send me a note if anything's super unclear.

If there's a feature that you'd like to add, please open up an Issue on this Github project before you begin so that I know what you're planning to work on.

Bug fixes always welcome.


# Due Diligence

This app owes much to:
Apache's Oltu project, a Java OAuth library
Square's Retrofit, a network / object marshalling library
Jake Wharton's Nine Old Androids, an Animation framework for Gingerbread

