package com.llx278.uimocker2;


import android.app.Activity;
import android.content.Context;
import android.os.SystemClock;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Contains the waitForDialogToClose() method.
 * 
 * @author Renas Reda, renas.reda@robotium.com
 * 
 */

class DialogUtils {

	private final InstrumentationDecorator instrumentation;
	private final ActivityUtils activityUtils;
	private final ViewGetter viewGetter;
	private final Sleeper sleeper;
	private final static int TIMEOUT_DIALOG_TO_CLOSE = 1000;
	private final int MINISLEEP = 200;

	/**
	 * Constructs this object.
	 *
	 * @param activityUtils the {@code ActivityUtils} instance
	 * @param viewGetter the {@code ViewGetter} instance
	 * @param sleeper the {@code Sleeper} instance
	 */

	public DialogUtils(InstrumentationDecorator instrumentation, ActivityUtils activityUtils, ViewGetter viewGetter, Sleeper sleeper) {
		this.instrumentation = instrumentation;
		this.activityUtils = activityUtils;
		this.viewGetter = viewGetter;
		this.sleeper = sleeper;
	}

	/**
	 * Waits for a {@link android.app.Dialog} to close.
	 *
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is closed before the defaultWaitTimeout and {@code false} if it is not closed
	 */

	public boolean waitForDialogToClose(long timeout) {
		waitForDialogToOpen(TIMEOUT_DIALOG_TO_CLOSE, false);
		final long endTime = SystemClock.uptimeMillis() + timeout;

		while (SystemClock.uptimeMillis() < endTime) {

			if(!isDialogOpen()){
				return true;
			}
			sleeper.sleep(MINISLEEP);
		}
		return false;
	}



	/**
	 * Waits for a {@link android.app.Dialog} to open.
	 *
	 * @param timeout the amount of time in milliseconds to wait
	 * @return {@code true} if the {@code Dialog} is opened before the defaultWaitTimeout and {@code false} if it is not opened
	 */

	public boolean waitForDialogToOpen(long timeout, boolean sleepFirst) {
		final long endTime = SystemClock.uptimeMillis() + timeout;
		boolean dialogIsOpen = isDialogOpen();

		if(sleepFirst)
			sleeper.sleep();
		
		if(dialogIsOpen){
			return true;
		}

		while (SystemClock.uptimeMillis() < endTime) {

			if(isDialogOpen()){
				return true;
			}
			sleeper.sleepMini();
		}
		return false;
	}

	/**
	 * Checks if a dialog is open. 
	 * 
	 * @return true if dialog is open
	 */

	private boolean isDialogOpen(){
		final Activity activity = activityUtils.getCurrentActivity();
		final View[] views = viewGetter.getWindowDecorViews();
		View view = viewGetter.getRecentDecorView(views);
		
		if(!isDialog(activity, view)){
			for(View v : views){
				if(isDialog(activity, v)){
					return true;
				}
			}
		}
		else {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks that the specified DecorView and the Activity DecorView are not equal.
	 * 
	 * @param activity the activity which DecorView is to be compared
	 * @param decorView the DecorView to compare
	 * @return true if not equal
	 */
	
	private boolean isDialog(Activity activity, View decorView){
		if(decorView == null || !decorView.isShown() || activity == null){
			return false;
		}
		Context viewContext = null;
		if(decorView != null){
			viewContext = decorView.getContext();
		}
		
		if (viewContext instanceof ContextThemeWrapper) {
			ContextThemeWrapper ctw = (ContextThemeWrapper) viewContext;
			viewContext = ctw.getBaseContext();
		}
		Context activityContext = activity;
		Context activityBaseContext = activity.getBaseContext();
		return (activityContext.equals(viewContext) || activityBaseContext.equals(viewContext))
				&& (decorView != activity.getWindow().getDecorView());
	}

	/**
	 * Hides the soft keyboard
	 * 
	 * @param shouldSleepFirst whether to sleep a default pause first
	 * @param shouldSleepAfter whether to sleep a default pause after
	 */

	public void hideSoftKeyboard(EditText editText, boolean shouldSleepFirst, boolean shouldSleepAfter) {
		InputMethodManager inputMethodManager;

		Activity activity = activityUtils.getCurrentActivity();
		if(activity == null){
			inputMethodManager = (InputMethodManager) instrumentation.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		}
		else {
			inputMethodManager = (InputMethodManager)  activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		}

		if (inputMethodManager == null) {
			return;
		}

		if(editText != null) {
			inputMethodManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
			return;
		}

		if (activity != null) {
			View focusedView = activity.getCurrentFocus();

			if(!(focusedView instanceof EditText)) {
				EditText freshestEditText =
						viewGetter.getFreshestView(viewGetter.getViewsByClass(EditText.class, true,null));
				if(freshestEditText != null){
					focusedView = freshestEditText;
				}
			}
			if(focusedView != null) {
				inputMethodManager.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
			}
		}

		if(shouldSleepAfter){
			sleeper.sleep();
		}
	}
}
