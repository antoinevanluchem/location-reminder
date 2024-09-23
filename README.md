# Code Review
## Reviewer Note 
> Congrats! Your hard work has paid off!
> 
> You made all the required changes pointed out in the previous review(s) and are now ready to move on to the next stage!
> 
> Keep up the good work as you continue your Nanodegree journey! Safe journey and bon voyage! 😄

## User Authentication 
✅ Enable user accounts using Firebase Authentication and Firebase UI
> While customization in other screens of FirebaseUI is limited to themes, we have the full power to customize Login UI with our own XML layout. You can learn more [here](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md#custom-layout).

✅ Create Login and Registration screens
> Your login/signin screen meets this spec! 👍
> 
> A successful login/signin screen is all about making a good first impression. As app developers, we all want our users to fall in love at first sight. In the first few minutes of using our app, our users must feel welcome. These screens must be simple and clear.

## Map View 
✅ Style the map 
> The user's current location is properly shown! Nice work! 👍 This app is a location finder app after all so being able to properly retrieve the user's current location is a top priority!

✅ Create a Map view that shows the user's current location 
✅ Add functionaility to allow the user to select POIs to set reminders 
✅ Display a notification when a selected POI is reached

## Reminders 
✅ Add a list view that displays the reminders
> [RecyclerView: "Introduction" (Developing Android Apps with Kotlin -Part 2 -> 3. Introduction)](https://learn.udacity.com/nanodegrees/nd940/parts/cd0636/lessons/c1fc6e4c-905a-4afc-a6d1-d9b05b5d4a35/concepts/a2c72d20-277a-481c-9fee-692fb785a9af) is correctly implemented to display all reminders in the location DB! 👍 It has the [ViewHolder](https://developer.android.com/develop/ui/views/layout/recyclerview#implement-adapter) pattern built right in! You can also check [this](https://www.youtube.com/watch?v=LqBlYJTfLP4) out.
>

✅ Add a screen to create reminders
> Nice work here! It shows that you have mastered two-way data binding! Two-way data binding lets you consolidate setting attributes by allowing you to set values and react to changes at the same time. [Here](https://www.youtube.com/watch?v=u8d_zXukB2w) is a very interesting tutorial about advanced data binding.
> 
> And you have a centralized [repository ("Exercise: Build a Repository")](https://learn.udacity.com/nanodegrees/nd940) to get information from your backend, which is your local DB! 👍 Even though the repository module looks unnecessary, it serves an important purpose: it abstracts the data sources from the rest of the app!
> 
✅ Display details about a reminder when a selected POI is reached and the user clicked on the notification 

## Testing 
✅ Test the ViewModels, Coroutines and LiveData
✅ Create a FakeDataSource to replace the Data Layer and test the app in isolation 
✅ Test DAO and Repository classes 
> An in-memory database is used for tests! 👍 One of the many benefits of testing using an in-memory database is that the test doesn't need to worry about rolling back the test when done.
✅ Use MVVM and Dependency injection to architect your app 
> The MVVM design pattern is properly implemented for your app! 👍
✅ Use Espresso and Mockito to test the app UI and Fragments Navigation

## Code Quality 
✅ Write code using best practices for Android development with Kotlin
> Good naming has the following benefits:
> 
> Improved Communication
> Improved Code integration
> Improved Consistency
> Improved clarity

# Location Reminder

A Todo list app with location reminders that remind the user to do something when he reaches a specific location. The app will require the user to create an account and login to set and access reminders.

## Getting Started

1. Android Studio (Jellyfish or above)
2. JDK 21 with `JAVA_HOME` environment variable set. If you don't have JDK 21 installed or `JAVA_HOME` is not set, consider using a tool like `sdkman` to simplify the process. Refer to the sdkman documentation for installation instructions: [sdkman installation](https://sdkman.io/install)
3. Clone the project to your local machine. 
4. Open the project using Android Studio.

### Dependencies

```
1. A created project on Firebase console.
2. A create a project on Google console.
```

### Installation

Step by step explanation of how to get a dev environment running.

```
1. To enable Firebase Authentication:
        a. Go to the authentication tab at the Firebase console and enable Email/Password and Google Sign-in methods.
        b. download `google-services.json` and add it to the app.
2. To enable Google Maps:
    a. Go to APIs & Services at the Google console.
    b. Select your project and go to APIs & Credentials.
    c. Create a new api key and restrict it for android apps.
    d. Add your package name and SHA-1 signing-certificate fingerprint.
    c. Enable Maps SDK for Android from API restrictions and Save.
    d. Copy the api key to the `google_maps_api.xml`
3. Run the app on your mobile phone or emulator with Google Play Services in it.
```

## Testing

Right click on the `test` or `androidTest` packages and select Run Tests

### Break Down Tests

Explain what each test does and why

```
1.androidTest
        //TODO: Students explain their testing here.
2. test
        //TODO: Students explain their testing here.
```

## Project Instructions
    1. Create a Login screen to ask users to login using an email address or a Google account.  Upon successful login, navigate the user to the Reminders screen.   If there is no account, the app should navigate to a Register screen.
    2. Create a Register screen to allow a user to register using an email address or a Google account.
    3. Create a screen that displays the reminders retrieved from local storage. If there are no reminders, display a   "No Data"  indicator.  If there are any errors, display an error message.
    4. Create a screen that shows a map with the user's current location and asks the user to select a point of interest to create a reminder.
    5. Create a screen to add a reminder when a user reaches the selected location.  Each reminder should include
        a. title
        b. description
        c. selected location
    6. Reminder data should be saved to local storage.
    7. For each reminder, create a geofencing request in the background that fires up a notification when the user enters the geofencing area.
    8. Provide testing for the ViewModels, Coroutines and LiveData objects.
    9. Create a FakeDataSource to replace the Data Layer and test the app in isolation.
    10. Use Espresso and Mockito to test each screen of the app:
        a. Test DAO (Data Access Object) and Repository classes.
        b. Add testing for the error messages.
        c. Add End-To-End testing for the Fragments navigation.


## Student Deliverables:

1. APK file of the final project.
2. Git Repository or zip file with the code.

## Built With

* [Koin](https://github.com/InsertKoinIO/koin) - A pragmatic lightweight dependency injection framework for Kotlin.
* [FirebaseUI Authentication](https://github.com/firebase/FirebaseUI-Android/blob/master/auth/README.md) - FirebaseUI provides a drop-in auth solution that handles the UI flows for signing
* [JobIntentService](https://developer.android.com/reference/androidx/core/app/JobIntentService) - Run background service from the background application, Compatible with >= Android O.

## License
