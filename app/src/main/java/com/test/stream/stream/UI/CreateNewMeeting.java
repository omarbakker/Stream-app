package com.test.stream.stream.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;


import com.test.stream.stream.Controllers.CalendarManager;
import com.test.stream.stream.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class CreateNewMeeting extends AppCompatActivity {

    //Views for datepicker
    TextView datePicker;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;

    //Views for timepicker
    static final int TIME_DIALOG_ID = 1;
    int hour_x, minute_x;
    TextView timePicker;

    //For meeting creation
    private String dayOfWeek;
    private String MonthOfYear;

    //Create new meeting
    EditText meeting_name, meeting_location, meeting_description;
    TextView createNewMeetingTitle;
    Button createNewMeeting;
    android.support.v4.app.FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_new_meeting);

        Typeface RalewayBold = Typeface.createFromAsset(this.getAssets(), "Raleway-ExtraBold.ttf");
        Typeface Raleway = Typeface.createFromAsset(this.getAssets(), "Raleway-Regular.ttf");

        meeting_name = (EditText) findViewById(R.id.new_meeting_name);
        meeting_description = (EditText) findViewById(R.id.new_meeting_description);
        meeting_location = (EditText) findViewById(R.id.new_meeting_location);
        createNewMeetingTitle = (TextView) findViewById(R.id.textView);

        meeting_name.setTypeface(Raleway);
        meeting_description.setTypeface(Raleway);
        meeting_location.setTypeface(Raleway);
        createNewMeetingTitle.setTypeface(RalewayBold);

        meeting_name.requestFocus();

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);
        hour_x = cal.get(Calendar.HOUR);
        minute_x = cal.get(Calendar.MINUTE);
        dayOfWeek = getDayOfWeekInitial(cal.get(Calendar.DAY_OF_WEEK));


        datePicker = (TextView) findViewById(R.id.new_meeting_date);
        datePicker.setText(getDayOfWeekInitial(cal.get(Calendar.DAY_OF_WEEK)) + ", " + cal.get(Calendar.DAY_OF_MONTH) + " " + getMonthString(cal.get(Calendar.MONTH)) + " " + cal.get(Calendar.YEAR));
        datePicker.setTypeface(RalewayBold);
        timePicker = (TextView) findViewById(R.id.new_meeting_time);
        if(cal.get(Calendar.MINUTE)<10)
            timePicker.setText(adjustHour(cal.get(Calendar.HOUR)) + ":" + "0" + cal.get(Calendar.MINUTE) + " " + getAmPmInitial(cal.get(Calendar.AM_PM)));
        else
            timePicker.setText(adjustHour(cal.get(Calendar.HOUR)) + ":" + cal.get(Calendar.MINUTE) + " " + getAmPmInitial(cal.get(Calendar.AM_PM)));
        timePicker.setTypeface(RalewayBold);

        showDialogOnButtonClick();
        showTimeDialogOnButtonClick();
        this.manager = getSupportFragmentManager();

        createNewMeeting = (Button) findViewById(R.id.create_new_meeting);
        createNewMeeting.setTypeface(Raleway);
        createNewMeeting.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkValidInputs();
                    }
                });
    }

    public void showDialogOnButtonClick(){
        // btn = (Button) findViewById(R.id.button2);
        //edittext = (EditText) findViewById(R.id.editText);
        datePicker.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(DIALOG_ID);
                    }
                }
        );
    }

    public void showTimeDialogOnButtonClick() {
        timePicker.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showDialog(TIME_DIALOG_ID);
                    }
                }
        );
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        else if (id == TIME_DIALOG_ID)
            return new TimePickerDialog(this, TimeSetListener, hour_x, minute_x, false);
        return null;
    }

    private TimePickerDialog.OnTimeSetListener TimeSetListener
            = new TimePickerDialog.OnTimeSetListener(){
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour_x = hourOfDay;
            minute_x = minute;
            if(minute_x < 10)
                timePicker.setText(adjustHour(hour_x) + ":" + "0" + minute_x + " " + getAmPm(hour_x));
            else
                timePicker.setText(adjustHour(hour_x) + ":" + minute_x + " " + getAmPm(hour_x));
        }
    };


    private DatePickerDialog.OnDateSetListener dpickerListener
            = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            year_x = year;
            month_x = monthOfYear + 1;
            day_x = dayOfMonth;
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");
            Date date = new Date(year, monthOfYear, dayOfMonth-1);
            dayOfWeek = simpleDateFormat.format(date);
            MonthOfYear = getMonthString(month_x-1);
            datePicker.setText(dayOfWeek + ", " + day_x + " " + MonthOfYear + " " + year_x);
            //textView.setText(MonthOfYear);
        }
    };

    public String getMonthString(int month) {
        String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return MONTHS[month];
    }

    public String getAmPm(int hour) {
        if (hour < 12)
            return "AM";
        else
            return "PM";
    }

    public int adjustHour(int hour) {
        if (hour > 12)
            return hour - 12;
        else if (hour == 0)
            return 12;
        else
            return hour;
    }

    public String getDayOfWeekInitial(int day) {
        switch (day) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
            default:
                return "";
        }
    }

    public String getAmPmInitial(int hour) {
        if (hour == 1)
            return "PM";
        else
            return "AM";
    }

    public boolean isNameValid() {
        if (meeting_name.getText().toString().isEmpty())
            return false;
        else
            return true;
    }

    public boolean isLocationValid() {
        if (meeting_location.getText().toString().isEmpty())
            return false;
        else
            return true;
    }

    public boolean isDescriptionValid() {
        if (meeting_description.getText().toString().isEmpty())
            return false;
        else
            return true;
    }

    public void checkValidInputs() {
        //Toast toast;
        if (!isNameValid()) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast,
                    (ViewGroup) findViewById(R.id.toast_layout_root));
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Enter in a meeting name");

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
        else if (!isDescriptionValid()) {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast,
                    (ViewGroup) findViewById(R.id.toast_layout_root));
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Enter in a meeting description");

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
        else if (!isLocationValid())  {
            LayoutInflater inflater = getLayoutInflater();
            View layout = inflater.inflate(R.layout.toast,
                    (ViewGroup) findViewById(R.id.toast_layout_root));
            TextView text = (TextView) layout.findViewById(R.id.text);
            text.setText("Enter in a meeting location");

            Toast toast = new Toast(getApplicationContext());
            toast.setDuration(Toast.LENGTH_LONG);
            toast.setView(layout);
            toast.show();
        }
        else {
            CalendarManager.sharedInstance().CreateMeeting(meeting_name.getText().toString(), meeting_description.getText().toString(),
                    meeting_location.getText().toString(), adjustHour(hour_x), minute_x,
                    day_x,  month_x, year_x, getMonthString(month_x-1),
                    dayOfWeek, getAmPm(hour_x));
            finish();
        }
    }
}
