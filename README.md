#LifeLog Android Library
This is an Android (not pure java) wrapper for the [LifeLog Web API](https://developer.sony.com/develop/services/lifelog-api).

[![Book session on Codementor](https://cdn.codementor.io/badges/book_session_github.svg)](https://www.codementor.io/championswimmer?utm_source=github&utm_medium=button&utm_term=championswimmer&utm_campaign=github)

[![Release](https://img.shields.io/github/release/championswimmer/Lifelog-Android-Library.svg?label=maven)](https://jitpack.io/#championswimmer/Lifelog-Android-Library/1.0)

### How to add to your project

Add it using jitpack maven distribution.  
Add the jitpack maven repository

```groovy
    repositories {
        jcenter()
        maven {
            url "https://jitpack.io"
        }
    }
```

Add the dependency

```groovy
    dependencies {
            compile 'com.github.championswimmer:Lifelog-Android-Library:1.0'
    }
```


###Initialise
In your Application class, inside the _onCreate_ body, add this

```java
LifeLog.initialise("clientid", "secret", "https://callbackurl.com");
```

Replace _clientid_, _secret_ and _callbackurl_ with your respective values of the app that
you have registered on LifeLog.

###Login and authentication
####Login
To log the user in for the first time, use this simple static method

```java
LifeLog.doLogin(MainActivity.this)
```

Note that, after login is completed, we will return back to same activity.
Internally, `doLogin` makes a `startActivityForResult` call. Handle the returning user like this

```java
@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LifeLog.LOGINACTIVITY_REQUEST_CODE) {
            //we are logged in, do what you please now
            Toast.makeText(this, "User authenticated", Toast.LENGTH_SHORT).show();
        }
    }
```

####Checking Authentication state
You would want to check if the user is authenticated or not. Even if he is, we may need to refresh
the auth token. Again, simple code for that.
```java
LifeLog.checkAuthentication(this, new LifeLog.OnAuthenticationChecked() {
    @Override
    public void onAuthChecked(boolean authenticated) {
        if (authenticated) {
            //User is authenticated, we can do API requests now
        } else {
            //User is not authenticated. Make him login (or whatever suits your app's flow)
            //LifeLog.doLogin(MainActivity.this);
        }
    }
});
```

###API Requests
####Location endpoint
Create an object of `LifeLogLocationAPI` using one of the two _prepareRequest_ methods.
One allows you to specify start and end time. Other doesn't. You can set either of start or end
time to be `null` as well.

The data fetching actually starts when _get_ is called. After the data is fetched, the `onLocationFetched`
callback is hit.
A List<> of LifeLogLocation objects are at your disposal.

```java
LifeLogLocationAPI llLocation = LifeLogLocationAPI.prepareRequest(500);
    llLocation.get(MainActivity.this, new LifeLogLocationAPI.OnLocationFetched() {
        @Override
        public void onLocationFetched(ArrayList<LifeLogLocationAPI.LifeLogLocation> locations) {
            for (LifeLogLocationAPI.LifeLogLocation loc : locations) {
                String id = loc.getId();
                Calendar startTime = loc.getStartTime();
                //continue likewise
            }
        }
    });
```
