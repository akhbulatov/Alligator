# Alligator
[![Release](https://jitpack.io/v/aartikov/Alligator.svg)](https://jitpack.io/#aartikov/Alligator) [![license](https://img.shields.io/badge/license-MIT-blue.svg)](https://opensource.org/licenses/MIT)

Alligator is a modern Android navigation library that will help to organize your navigation code in clean and testable way.

## Features
- Screens based on activities, fragments and dialog fragments.
- Simple yet powerful navigation methods.
- Independence from activity lifecycle (navigation is available even when an application is in background).
- Passing screen arguments without boilerplate code.
- Handling screen result in object oriented style.
- Screen switching with nested navigation.
- Flexible animation configuring.

## Gradle Setup
Add jitpack.io repository in project level build.gradle:

```gradle
repositories {
    ...
    maven { url 'https://jitpack.io' }
}
```

Add the dependencies in module level build.gradle:

```gradle
dependencies {
    compile 'com.github.aartikov.Alligator:alligator:2.0.0'
    annotationProcessor 'com.github.aartikov.Alligator:alligator-compiler:2.0.0'
}
```

## Components to know
[AndroidNavigator](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/AndroidNavigator.html) - the main library object. It implements `Navigator` and `NavigationContextBinder` interfaces and uses a command queue internally to execute navigation commands.

[Navigator](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/Navigator.html) - has navigation methods such as `goForward`, `goBack`, `replace` and so on. It does not depend on Android SDK, so code that uses it can be tested easily. `Navigator` operates with `Screen`s.

[Screen](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/Screen.html) - a logical representation of an application screen. It is used to indicate a screen type and pass screen arguments.

[NavigationContextBinder](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/NavigationContextBinder.html) - binds and unbinds `NavigationContext` to `AndroidNavigator`.

[NavigationContext](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/NavigationContext.html) -  is used to configure `AndroidNavigator`. It contains a reference to the current activity and all the other things needed for command execution.

[Command](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/commands/Command.html) - a command executed by `AndroidNavigator`. The library has a bunch of implemented commands corresponding to navigation methods. You don’t need to create a command manually, `AndroidNavigator` creates it when a navigation method is called.

[NavigationFactory](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/navigationfactories/NavigationFactory.html) - associates `Screen`s with theirs Android implementation. Alligator generates a navigation factory for you with annotation processor, but you can extend it if needed.

[ScreenSwitcher](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/screenswitchers/ScreenSwitcher.html) - an object for switching between several screens without theirs recreation. There are ready to use implementations of `ScreenSwitcher` - [FragmentScreenSwitcher](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/screenswitchers/FragmentScreenSwitcher.html).

[TransitionAnimation](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/animations/TransitionAnimation.html), [TransitionAnimationProvider](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/animations/providers/TransitionAnimationProvider.html) - are used to configure animations.

## Quick start
### 1. Declare screens
Screens with arguments should be `Serializable` or `Parcelable`.

```java
// Screen without arguments
public class ScreenA implements Screen {}

// Screen with an argument
public class ScreenD implements Screen, Serializable {
  private String mMessage;

  public ScreenD(String message) {
     mMessage = message;
  }

  public String getMessage() {
     return mMessage;
  }
}
```

### 2. Register screens
Mark your activities and fragments with `@RegisterScreen` annotation. Alligator looks for this annotations to create `GeneratedNavigationFactory`.

```java
@RegisterScreen(ScreenA.class)
public class ActivityA extends AppCompatActivity 
```
```java
@RegisterScreen(ScreenD.class)
public class FragmentD extends Fragment
```

### 3. Create AndroidNavigator
It should be a single instance in your application.
```java
sAndroidNavigator = new AndroidNavigator(new GeneratedNavigationFactory());
```

### 4. Set NavigationContext
Use `NavigationContext.Builder` to create `NavigationContext`. In the simplest case just pass a current activity to it. You can also set a fragment container, a fragment manager (which by default is taken from an activity), a [screen switcher](https://github.com/aartikov/Alligator#switch-screens), [animation providers](https://github.com/aartikov/Alligator#configure-animations) and [listeners](https://github.com/aartikov/Alligator#listen-navigation).

Activities are responsible to bind and unbind `NavigationContext`. Bind it in `onResumeFragments` (when state of an activity and its fragments is restored) and unbind in `onPause` (when an activity becomes inactive).

```java
@Override
protected void onResumeFragments() {
    super.onResumeFragments();
    NavigationContext navigationContext = new NavigationContext.Builder(this)
            .containerId(R.id.fragment_container)
            .build();
    mNavigationContextBinder.bind(navigationContext);
}

@Override
protected void onPause() {
    mNavigationContextBinder.unbind();
    super.onPause();
}
```


### 5. Call navigation methods
```java
mNavigator.goForward(new ScreenD("Message for D"));
// or
mNavigator.goBack();
```

`Navigator` provides these navigation methods:
1. `goForward(screen)` - adds a new screen and goes to it.
2. `goBack()` - removes the current screen and goes back to the previous screen.
3. `goBackWithResult(screenResult)` - goes back with [ScreenResult](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/ScreenResult.html).
4. `goBackTo(screenClass)` - goes back to a screen with the given class.
5. `replace(screen)` - replaces the last screen with a new screen.
6. `reset(screen)` - removes all other screens and adds a new screen.
7. `finish()` - finishes the last screen or the group of screens executing some common task (implemented as finishing of the current activity).
8. `finishWithResult(screenResult)` - finishes with [ScreenResult](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/ScreenResult.html).
8. `switchTo(screen)` - switches a screen using a [ScreenSwitcher](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/screenswitchers/ScreenSwitcher.html).

Navigation methods can be called at any moment, even when a `NavigationContext` is not bound. When a navigation method is called an appropriate `Command` is created and placed to a command queue. `AndroidNavigator` can execute commands only when a `NavigationContext` is bound to it, in other case a command will be postponed. You can combine navigation methods arbitrarily (for example call two `goBack()` one by one). This works for activities too because `AndroidNavigator` unbinds a `NavigationContext` by itself after activity finishing or starting.

See how navigation methods work in [simple navigation sample](https://github.com/aartikov/Alligator/tree/master/simplenavigationsample) an [navigation methods sample](https://github.com/aartikov/Alligator/tree/master/navigationmethodssample).

### 6. Get screen arguments
To get screen arguments from an activity or a fragment use [ScreenResolver](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/ScreenResolver.html). 
```java
mScreenResolver = SampleApplication.sAndroidNavigator.getScreenResolver();
ScreenD screen = mScreenResolver.getScreen(this); // 'this' is Activity or Fragment
String message = screen.getMessage();
```

## Advanced topics
### Configure animations
Create `TransitionAnimationProvider` and set it to `NavigationContext`.
```java
public class SampleTransitionAnimationProvider implements TransitionAnimationProvider {
	@Override
	public TransitionAnimation getAnimation(TransitionType transitionType, Class<? extends Screen> screenClassFrom,
    					Class<? extends Screen> screenClassTo, boolean isActivity, @Nullable AnimationData animationData) {
		switch (transitionType) {
			case FORWARD:
				return new SimpleTransitionAnimation(R.anim.slide_in_right, R.anim.slide_out_left);
			case BACK:
				return new SimpleTransitionAnimation(R.anim.slide_in_left, R.anim.slide_out_right);
			default:
				return TransitionAnimation.DEFAULT;
		}
	}
}
```
```java
    NavigationContext navigationContext = new NavigationContext.Builder(this)
            .transitionAnimationProvider(new SampleTransitionAnimationProvider())
            .build();
```

Lollipop transition animations are also supported, see [shared element animation sample](https://github.com/aartikov/Alligator/tree/master/sharedelementanimationsample).

### Switch screens
A navigation method `switchTo` is similar to `replace`. The difference is that during screen switching screens can be reused. For example if there are three tabs in your application and each tab screen is represented by a fragment, there are no reason to create more than three fragments. Screen switching is especially useful if you want to create a nested navigation where each tab has its own backstack.

To make screen switching posible a special object `ScreenSwitcher` should be created and set to `NavigationContext`. The library provides a `ScreenSwitcher` implementation called `FragmentScreenSwitcher`. Screens passed to  `FragmentScreenSwitcher` are used as keys to identify fragments so they must have `equals` and `hashCode` methods correctly overridden.

 See how screen switching works in [simple screen switcher sample](https://github.com/aartikov/Alligator/tree/master/simplescreenswitchersample) an [advanced screen switcher sample](https://github.com/aartikov/Alligator/tree/master/advancedscreenswitchersample).

### Open dialogs
To open a dialog register screen implemented by a dialog fragment and start it with `goForward` method.

### Listen navigation
These types of listeners can be set to `NavigationContext`
- [TransitionListener](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/listeners/TransitionListener.html) - is called when usual screen transition (not screen switching and not dialog showing) has been executed.
- [DialogShowingListener](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/listeners/DialogShowingListener.html) - is called when a dialog fragment has been shown.
- [ScreenSwitchingListener](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/listeners/ScreenSwitchingListener.html) - is called when a screen has been switched with a screen switcher.
- [ScreenResultListener](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/listeners/ScreenResultListener.html) - is called when a screen that can return a result has finished.
- [NavigationErrorListener](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/listeners/NavigationErrorListener.html) is called when a navigation error has occurred.

### Start external activity
To use an external activity (for example a phone dialer) extend `GeneratedNavigationFactory` and register a screen with a custom activity converter.

```java
public class SampleNavigationFactory extends GeneratedNavigationFactory {
	public SampleNavigationFactory() {
		ActivityConverter<PhoneDialerScreen> phoneDialerConverter = new ActivityConverter<>(
				(context, screen) -> new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + screen.getPhoneNumber()))
		);
		registerActivity(PhoneDialerScreen.class, null, phoneDialerConverter);
   }
```

Start it with `goForward` method. Use [NavigationErrorListener](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/listeners/NavigationErrorListener.html) to check that an activity has been succesfully resolved.

### Handle screen result
A screen can return [ScreenResult](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/ScreenResult.html) to a previous screen. It is like `startActivityForResult`, but with Alligator there are no needs to declare request codes and handle `onActivityResult` manually. Alligator defines unique request codes for screens implemented by activities that can return results. For screens implemented by fragments Alligator uses usual listeners.

Declare and register screen result classes. Return a result with `goBackWithResult` or `finishWithResult` methods of `Navigator`. Use [ActivityResultHandler](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/ActivityResultHandler.html) and [ScreenResultListener](https://jitpack.io/com/github/aartikov/Alligator/alligator/2.0.0/javadoc/me/aartikov/alligator/listeners/ScreenResultListener.html) to handle screen result.

See how to do it in [screen result sample](https://github.com/aartikov/Alligator/tree/master/screenresultsample).

## Developed by
Artur Artikov <a href="mailto:a.artikov@gmail.com">a.artikov@gmail.com</a></br>Mikhail Savin <a href="mailto:savinmike.u@gmail.com">savinmike.u@gmail.com</a>
## License
```
The MIT License (MIT)

Copyright (c) 2017 Artur Artikov

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```