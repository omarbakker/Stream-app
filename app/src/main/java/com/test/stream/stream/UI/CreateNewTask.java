package com.test.stream.stream.UI;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import com.test.stream.stream.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.test.stream.stream.R.id.newTaskDialog;

/**
 * Created by janemacgillivray on 2016-11-27.
 */

public class CreateNewTask extends AppCompatActivity {

    //Views for datepicker
    TextView datePicker;
    int year_x, month_x, day_x;
    static final int DIALOG_ID = 0;

    int[] DueDate = {0,0,0};
    //For meeting creation
    private String dayOfWeek;
    private String MonthOfYear;

    //Create new meeting
    android.support.v4.app.FragmentManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = this.getLayoutInflater();
        final View v = inflater.inflate(R.layout.dialog_newtask, null);

        DueDate = getIntent().getIntArrayExtra("DueDate");

        final Calendar cal = Calendar.getInstance();
        year_x = cal.get(Calendar.YEAR);
        month_x = cal.get(Calendar.MONTH);
        day_x = cal.get(Calendar.DAY_OF_MONTH);

        Typeface RalewayBold = Typeface.createFromAsset(this.getAssets(), "Raleway-Bold.ttf");

        datePicker = (TextView) findViewById(R.id.newTaskNameField);
        datePicker.setText(getDayOfWeekInitial(cal.get(Calendar.DAY_OF_WEEK)) + ", " + cal.get(Calendar.DAY_OF_MONTH) + " " + getMonthString(cal.get(Calendar.MONTH)) + " " + cal.get(Calendar.YEAR));
        datePicker.setTypeface(RalewayBold);


        showDialogOnButtonClick();

        DueDate[0] = day_x;
        DueDate[1] = month_x;
        DueDate[2] = year_x;
        finish();
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


    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_ID)
            return new DatePickerDialog(this, dpickerListener, year_x, month_x, day_x);
        return null;
    }




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
        }
    };

    public String getMonthString(int month) {
        String[] MONTHS = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return MONTHS[month];
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

}
