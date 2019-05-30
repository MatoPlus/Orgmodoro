package com.example.rixin.orgmodoro;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;

/*
 * Author: Ri Xin Yang
 * Date: May 24, 2019
 * Desc: This is the settings activity for the Pomodoro timer app. This activity handles the how
 * the user interact with the settings to customize the app to their preference. This includes
 * Initializing break interval, the work interval, and the app's global colour scheme.
 * Note that isLightTheme is not stored in onPause since the data should be received from main.
 * saving it within onPause will automatically default 'isLightTheme' to false during the first run.
 * Note that this is inconsistent.
 */
public class SettingsActivity extends Activity {

    // Declare instance variables for the settings activity.
    private final static long DEFAULT_WORK_DURATION = 1500000;
    private final static long DEFAULT_BREAK_DURATION = 300000;
    private ImageButton timerButton;
    private SeekBar breakSeekBar;
    private SeekBar workSeekBar;
    private TextView breakStatusView;
    private TextView workStatusView;
    private TextView breakLabel;
    private TextView workLabel;
    private TextView themeLabel;
    private TextView workDescriptionLabel;
    private TextView breakDescriptionLabel;
    private TextView themeDescriptionLabel;
    private ConstraintLayout settingsLayout;
    private RadioGroup themeRadioGroup;
    private RadioButton lightThemeRadioButton;
    private RadioButton darkThemeRadioButton;
    private Drawable lightTimerButtonImage;
    private Drawable darkTimerButtonImage;
    private int breakStatus;
    private int workStatus;
    private boolean isLightTheme;
    private int colourPrimary;
    private int colourText;
    private int colourBackground;
    private long newBreakDurationInMillis;
    private long newWorkDurationInMillis;
    private final static int minTimeInMinutes = 1;

    // Declare reference for a SharedPreferences object
    private SharedPreferences savedPrefs;

    /*
     * This method is called on the creation of the settings activity. Specifically, this method
     * will handle the instantiation of the basic references, objects, and data variables required for
     * the settings activity to method.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Set up reference for main images and layout.
        settingsLayout = findViewById(R.id.settingsLayout);
        lightTimerButtonImage = getResources().getDrawable(R.drawable.light_timer);
        darkTimerButtonImage = getResources().getDrawable(R.drawable.dark_timer);

        // Set up reference instance variables with resource.
        timerButton = findViewById(R.id.timerButton);
        breakSeekBar = findViewById(R.id.breakSeekBar);
        workSeekBar = findViewById(R.id.workSeekBar);
        breakLabel = findViewById(R.id.breakLabel);
        breakStatusView = findViewById(R.id.breakStatusView);
        breakDescriptionLabel = findViewById(R.id.breakDescriptionLabel);
        workLabel = findViewById(R.id.workLabel);
        workStatusView = findViewById(R.id.workStatusView);
        workDescriptionLabel = findViewById(R.id.workDescriptionLabel);
        themeLabel = findViewById(R.id.themeLabel);
        themeDescriptionLabel = findViewById(R.id.themeDescriptionLabel);
        lightThemeRadioButton = findViewById(R.id.lightThemeRadioButton);
        darkThemeRadioButton = findViewById(R.id.darkThemeRadioButton);
        themeRadioGroup = findViewById(R.id.themeRadioGroup);

        // Set instance variables with corresponding object listeners.
        timerButton.setOnClickListener(new ButtonListener());
        breakSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
        workSeekBar.setOnSeekBarChangeListener(new SeekBarListener());
        themeRadioGroup.setOnCheckedChangeListener(new RadioGroupListener());

        // Get variables from main activity, where this activity is called.
        Intent intent = getIntent();

        // Save appropriate variables retrieved from main to display current user settings.
        isLightTheme = intent.getBooleanExtra("isLightTheme", true);
        newBreakDurationInMillis = intent.getLongExtra("setBreakDurationInMillis",
                DEFAULT_BREAK_DURATION);
        newWorkDurationInMillis = intent.getLongExtra("setWorkDurationInMillis",
                DEFAULT_WORK_DURATION);
        breakStatus = convertMillisToMin(newBreakDurationInMillis);
        workStatus = convertMillisToMin(newWorkDurationInMillis);

        // get SharedPreferences object for saving variables onPause.
        savedPrefs = getSharedPreferences( "SettingsPrefs", MODE_PRIVATE );

        // update activity theme.
        updateActivityColourScheme();

        // update all the current widget with the settings retrieved from main.
        updateCurrentWidgetWithSettings();

    }

    /*
     * This inner class for the radio button groups, implementing the
     * RadioGroup.OnCheckedChangeListener
     */
    class RadioGroupListener implements RadioGroup.OnCheckedChangeListener {

        /*
         * This method is automatically called when there is a change in the RadioGroup.
         */
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            // find which radio button is selected, change theme flag accordingly.
            if (checkedId == R.id.lightThemeRadioButton) {
                isLightTheme = true;
            } else {
                isLightTheme = false;
            }

            // Update colour scheme after changing theme.
            updateActivityColourScheme();
        }

    }

    /*
     * This inner class acts as the seek bar listener. implementing
     * SeekBar.OnSeekBarChangeListener
     */
    class SeekBarListener implements SeekBar.OnSeekBarChangeListener {

        /*
         * This method is automatically called when there is a change in the seek bar. This method
         * will change the corresponding status view as the seek bar changes. Additionally, the
         * new changed duration will be changed for returning back to the main activity.
         */
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            // Declare local variables.
            String statusView;

            // If the break seek bar is changed, set text view for break status.
            if (seekBar.getId() == R.id.breakSeekBar) {
                // Get status, add minTime to accommodated for min value of 1 in seek bar.
                breakStatus = progress + minTimeInMinutes;

                // Decide to use singular version of minute if appropriate.
                if (breakStatus == 1) {
                    statusView = breakStatus + " Minute";
                }
                else {
                    statusView = breakStatus + " Minutes";
                }

                // Set break status and save new break duration in millis.
                breakStatusView.setText(statusView);
                newBreakDurationInMillis = convertMinToMillis(breakStatus);
            }

            // If the work seek bar is changed, set text view for work status.
            else if (seekBar.getId() == R.id.workSeekBar) {
                // Get status, add minTime to accommodated for min value of 1 in seek bar.
                workStatus = progress+minTimeInMinutes;

                // Decide to use singular version of minute if appropriate.
                if (workStatus == 1) {
                    statusView = workStatus + " Minute";
                }
                else {
                    statusView = workStatus + " Minutes";
                }

                // Set work status and save new work duration in millis.
                workStatusView.setText(statusView);
                newWorkDurationInMillis = convertMinToMillis(workStatus);
            }
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // Called when the user starts changing the SeekBar
            // Not Used / Implemented
        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // Called when the user finishes changing the SeekBar
            // Not Used / Implemented
        }

    }

    /*
     * Inner class for Button listeners that implements View.OnClickListener.
     */
    class ButtonListener implements View.OnClickListener {

        /*
         * This method is automatically called when a button is pressed, if the appropriate button
         * is pressed, perform a certain call.
         */
        @Override
        public void onClick(View v) {

            // If the timer button is pressed, return to main activity.
            if (v.getId() == R.id.timerButton) {
                exitToTimerActivity();
            }
        }
    }

    /*
     * This method executes a sequence of steps which saves the appropriate variables in this class
     * to be used back in main results.
     */
    public void exitToTimerActivity() {

        // Create the intent for saving variables back to main.
        Intent intent = new Intent();

        // Save variables as key value pairs.
        intent.putExtra("isLightTheme", isLightTheme);
        intent.putExtra("newBreakDurationInMillis", newBreakDurationInMillis);
        intent.putExtra("newWorkDurationInMillis", newWorkDurationInMillis);

        // Set result to ok code to declare that app has return normally.
        setResult(RESULT_OK, intent);

        // Finish settings activity, return to main activity.
        finish();
   }

   /*
    * This method accepts a parameter of minutes in int to be converted into a long value of
    * milliseconds.
    */
   private long convertMinToMillis(int minutes) {

       // Return operation, converting minutes to milliseconds.
        return (minutes * 60 * 1000);

   }

    /*
     * This method accepts a parameter of milliseconds in long to be converted into an int value of
     * minutes.
     */
    private int convertMillisToMin(long millis) {

        // Return operation from millis to minutes.
        return ((int)(millis / 60 / 1000));

    }

    /*
     * This method updates the colours used currently in the activity depending on the isLightTheme
     * flag. This will update the colour palette to dark or light when called. Note that the
     * secondary colour is not used in this activity.
     */
    private void updateColourSchemeColour() {


        // Set light theme colours if appropriate.
        if (isLightTheme) {
            colourPrimary = getResources().getColor(R.color.lightPrimary);
            colourText = getResources().getColor(R.color.lightText);
            colourBackground = getResources().getColor(R.color.lightBackground);
        }
        // Set dark theme colours if appropriate.
        else {
            colourPrimary = getResources().getColor(R.color.darkPrimary);
            colourText = getResources().getColor(R.color.darkText);
            colourBackground = getResources().getColor(R.color.darkBackground);
        }

    }

    /*
     * This method will update the colour of the widgets that has to be manually updated.
     * Specifically, this method will use the current colour platte and the isLightTheme flag to
     * determine which image for the buttons to use. Additionally, this method will also use the
     * current colour platte th set the colours of the background, labels, and other widgets.
     */
    private void updateWidgetColourScheme() {

        // Determine which image to use for the timer button depending on the flag.
        if (isLightTheme) {
            timerButton.setImageDrawable(lightTimerButtonImage);
        }
        else {
            timerButton.setImageDrawable(darkTimerButtonImage);
        }

        // Background colour change.
        settingsLayout.setBackgroundColor(colourBackground);

        // Text colour change
        breakLabel.setTextColor(colourText);
        breakDescriptionLabel.setTextColor(colourText);
        breakStatusView.setTextColor(colourText);
        workLabel.setTextColor(colourText);
        workStatusView.setTextColor(colourText);
        workDescriptionLabel.setTextColor(colourText);
        themeLabel.setTextColor(colourText);
        themeDescriptionLabel.setTextColor(colourText);
        lightThemeRadioButton.setTextColor(colourText);
        darkThemeRadioButton.setTextColor(colourText);

        // Change tint colours of widgets with primary colour.
        breakSeekBar.setProgressTintList(ColorStateList.valueOf(colourPrimary));
        breakSeekBar.setThumbTintList(ColorStateList.valueOf(colourPrimary));
        workSeekBar.setProgressTintList(ColorStateList.valueOf(colourPrimary));
        workSeekBar.setThumbTintList(ColorStateList.valueOf(colourPrimary));
        lightThemeRadioButton.setButtonTintList(ColorStateList.valueOf(colourPrimary));
        darkThemeRadioButton.setButtonTintList(ColorStateList.valueOf(colourPrimary));

    }

    /*
     * This method updates the display of current widget with any new variables. This method is
     * meant to be executed after retrieving data from main, updating the settings activity to
     * display the current user settings.
     */
    private void updateCurrentWidgetWithSettings() {

        // Update seek bar progress.
        breakSeekBar.setProgress(breakStatus - minTimeInMinutes);
        workSeekBar.setProgress(workStatus - minTimeInMinutes);

        // Update radio button checks, depending on current theme flag.
        if (isLightTheme) {
            themeRadioGroup.check(R.id.lightThemeRadioButton);
        }
        else {
            themeRadioGroup.check(R.id.darkThemeRadioButton);
        }
    }

    /*
     * This method will update the colour scheme and then update the widgets when called.
     */
    private void updateActivityColourScheme() {

        // Update the colour scheme colours followed by the update of all widgets.
        updateColourSchemeColour();
        updateWidgetColourScheme();
    }

    /*
     * This method is automatically called when the app pauses. When the app pause, some variables
     * that are considered important will be saved for restoring on resume.
     */
    @Override
    public void onPause() {
        // Save the billAmountString and tipPercentage instance variables
        SharedPreferences.Editor prefsEditor = savedPrefs.edit();
        prefsEditor.putInt("breakStatus", breakStatus);
        prefsEditor.putInt("workStatus", workStatus);
        prefsEditor.commit();

        // Calling the parent onPause() must be done LAST
        super.onPause();
    }

    /*
     * This method is automatically called when the app resumes from the paused state. The variables
     * that are save will be restored to be used within the current settings activity again.
     */
    @Override
    public void onResume() {

        // Call progress required for on resume call.
        super.onResume();

        // Load the instance variables back (or default values)
        breakStatus = savedPrefs.getInt("breakStatus", 5);
        workStatus = savedPrefs.getInt("workStatus", 25);
    }

    /*
     * This method will be invoked when user click android device Back menu at bottom. When back is
     * pressed, it will exit the current activity and go back to the previous activity.
     */
    @Override
    public void onBackPressed() {
        // Uses standard exit method when back is pressed.
        exitToTimerActivity();
    }

}

