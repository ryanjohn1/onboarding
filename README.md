Onboarding
===========

Onboarding is an Open Source Android library that allows developers to easily create a non-intrusive
first-use experience to teach users how to use your app without overloading and frustrating them.

Setup
-----
*Note: Onboarding uses the gradle build structure and can be easily imported from Maven Central
with the following dependency.

Add the following to your `build.gradle`:


```groovy
dependencies {
    implementation 'com.github.ryanjohn1:onboarding:1.1.0'
}

```
Usage
-----

There is an example app in the library project showing some simple use cases over 3 different activities.
* StandardToolTipActivity shows different types of ToolTips and how they appear relative to views.
* TrackerActivity shows different uses for the OnboardingTracker object, making a ToolTip only appear
after a certain number of views and never show again once dismissed. It also shows a sequence of three
ToolTips that appear once the previous one is dismissed. Finally, there is an example of a ToolTip set
to appear after a delay, only if the button to which it points hasn't been clicked by the user yet.
* ListActivity shows a simple case of displaying a ToolTip for a view within a list item.


To add a simple ToolTip relative to a View:
* In your layout xml file, add the `ToolTipRelativeLayout` (`com.appenguin.onboarding.ToolTipRelativeLayout`) with height and width of `match_parent`. Make sure this view is on top!
* Find the `ToolTipRelativeLayout` in your code, and start adding `ToolTips`!

Example:
-----
```java
<RelativeLayout
	xmlns:android="http://schemas.android.com/apk/res/android"
	android:layout_width="match_parent"
	android:layout_height="match_parent">

	<TextView
	    android:id="@+id/activity_main_redtv"
	    android:layout_width="wrap_content"
	    android:layout_height="wrap_content"
	    android:layout_centerInParent="true" />

	<com.appenguin.onboarding.ToolTipRelativeLayout
		android:id="@+id/activity_main_tooltipRelativeLayout"
		android:layout_width="match_parent"
		android:layout_height="match_parent" />
</RelativeLayout>

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.activity_main);
	
	ToolTipRelativeLayout toolTipRelativeLayout = (ToolTipRelativeLayout) findViewById(R.id.activity_main_tooltipRelativeLayout);
		
	ToolTip toolTip = new ToolTip()
	                    .withText("A beautiful View")
	                    .withColor(Color.RED)
	                    .withShadow()
						.withAnimationType(ToolTip.ANIMATIONTYPE_FROMTOP);
	myToolTipView = toolTipRelativeLayout.showToolTipForView(toolTip, findViewById(R.id.activity_main_redtv));
	toolTipRelativeLayout.addView(myToolTipView);
}
```
	


Using an OnboardingTracker
-----

To make the ToolTip first appear the second time the activity is shown and then show the subsequent
two times, unless the ToolTip is dismissed forever by tapping it, use an OnboardingTracker like so:

```java
    OnboardingTracker tracker = new OnboardingTracker(this, getString(R.string.tracker_first_button))
                .withFirstShow(1)
                .withLastShow(3)
                .build();

    final ToolTip toolTip = ToolTip toolTip = new ToolTip()
                .withText("Shows on 2nd to 4th views");

    ToolTipView trackerToolTipView = toolTipRelativeLayout.showToolTipWithTracker(toolTip, findViewById(R.id.activity_main_orangetv), tracker);
    toolTipRelativeLayout.addToolTipView(trackerToolTipView);
```

Creating a ToolTipSequence
-----

A sequence of ToolTips can be created as follows:

```java
    final String sequenceId = "sequence";
    OnboardingTracker sequenceTracker = new OnboardingTracker(this, sequenceId).build();
    toolTipSequence = new ToolTipSequence(sequenceTracker, toolTipRelativeLayout);
    // ...
    // Create simple ToolTipViews as before without Trackers.
    // ...
    toolTipSequence.addToSequence(sequenceOneToolTipView);
    toolTipSequence.addToSequence(sequenceTwoToolTipView);
```

ToolTip customization
-----
You can customize the `ToolTip` in several ways:

* Specify a content text using `ToolTip.setText()`.
* Set a backround color using `ToolTip.setColor()`.
* Set the text color using `ToolTip.withTextColor()`.
* Specify whether to show a shadow or not with `ToolTip.setShadow()`.
* Specify how to animate the ToolTip: from the view itself or from the top, using `ToolTip.setAnimationType()`.
* Set your own custom content View using `ToolTip.setContentView()`.
* Set a delay before the ToolTip is shown with `ToolTip.withDelay()`.

The ToolTip textview can be further customised by creating a tooltip_textview.xml file in your project.
This will override the default one and allow you to customise that textview such as uses styles or set maxWidth.


OnboardingTracker customization
-----
You can customize the `OnboardingTracker` in several ways:

* A unique id should be used for each tracker which will be used automatically in the shared preferences to track
its history.
* To construct a Tracker `build()` must be called following the setting of options.
* Set the number of times the Tracker must be built before it will allow the ToolTip to show using
 `withFirstShow()`. It is zero-indexed and defaults to zero.
* Set the last time the Tracker will allow the ToolTip to show using `withLastShow()`.
It is zero-indexed and defaults to never stop automatically.
* If the ToolTip is clicked and dismissed it will no longer show unless `setDismissedPref(false)`
is called to reactivate it
* Alternatively, `setDismissedPref(true)` can be called to stop the ToolTip appearing at any point in the future.
* If there are ToolTips which shouldn't start counting up when to show until initial onboarding has completed
then set isAfterInitialOnboarding(true).

Developed By
-----
* Ryan John

Forked from the SuperToolTips project by Niek Haarman.
https://github.com/nhaarman/supertooltips

License
-----

	Copyright 2015 Ryan John

	Licensed under the Apache License, Version 2.0 (the "License");
	you may not use this file except in compliance with the License.
	You may obtain a copy of the License at

	http://www.apache.org/licenses/LICENSE-2.0

	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
