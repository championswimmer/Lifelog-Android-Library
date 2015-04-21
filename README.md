#LifeLog Android Library
This is an Android (not pure java) wrapper for the [LifeLog Web API](https://developer.sony.com/develop/services/lifelog-api).

##QuickStart
 <b> I will upload this maven & jcenter soon,
 bear with the cumbersome process of manually downloading source
 and adding it till then </b>

###Initialise
In your Application class, inside the _onCreate_ body, add this

```java
LifeLog.initialise("clientid", "secret", "https://callbackurl.com");
```

Replace _clientid_, _secret_ and _callbackurl_ with your respective values of the app that
you have registered on LifeLog.

###Login
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
            Toast.makeText(this, "User authenticated", Toast.LENGTH_SHORT).show();
        }
    }
```
