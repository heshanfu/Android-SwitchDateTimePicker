package com.kunzisoft.switchdatetime;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ViewAnimator;

import com.kunzisoft.switchdatetime.date.SwitchDatePicker;
import com.kunzisoft.switchdatetime.time.RadialPickerLayout;
import com.kunzisoft.switchdatetime.time.SwitchTimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * A fragment that displays a dialog window with Date and Time who can be selected by switch button
 * @author J-Jamet
 * @version 1.0
 */
public class SwitchDateTimeDialogFragment extends DialogFragment {

    private static final String STATE_DATETIME = "STATE_DATETIME";
    private Calendar dateTime = Calendar.getInstance();

    private static final String TAG_LABEL = "LABEL";
    private static final String TAG_POSITIVE_BUTTON = "POSITIVE_BUTTON";
    private static final String TAG_NEGATIVE_BUTTON = "NEGATIVE_BUTTON";

    private String mLabel;
    private String mPositiveButton;
    private String mNegativeButton;
    private OnButtonClickListener mListener;

    private View dateTimeLayout;
    private ViewGroup viewGroup;
    private ViewAnimator switcher;

    private int year = dateTime.get(Calendar.YEAR);
    private int month = dateTime.get(Calendar.MONTH);
    private int day = dateTime.get(Calendar.DAY_OF_MONTH);
    private int hour = dateTime.get(Calendar.HOUR_OF_DAY);
    private int minute = dateTime.get(Calendar.MINUTE);

    private SimpleDateFormat dayAndMonthSimpleDate;

    /**
     * Create a new instance of SwitchDateTimeDialogFragment
     */
    public static SwitchDateTimeDialogFragment newInstance(String label, String positiveButton, String negativeButton) {
        SwitchDateTimeDialogFragment switchDateTimeDialogFragment = new SwitchDateTimeDialogFragment();
        // Add arguments
        Bundle args = new Bundle();
        args.putString(TAG_LABEL, label);
        args.putString(TAG_POSITIVE_BUTTON, positiveButton);
        args.putString(TAG_NEGATIVE_BUTTON, negativeButton);
        switchDateTimeDialogFragment.setArguments(args);

        return switchDateTimeDialogFragment;
    }

    /**
     * Set listener for actions
     * @param onButtonClickListener Listener for click
     */
    public void setOnButtonClickListener(OnButtonClickListener onButtonClickListener) {
        this.mListener = onButtonClickListener;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        // Save the current datetime
        savedInstanceState.putLong(STATE_DATETIME, dateTime.getTimeInMillis());

        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        if(getArguments() != null) {
            mLabel = getArguments().getString(TAG_LABEL);
            mPositiveButton = getArguments().getString(TAG_POSITIVE_BUTTON);
            mNegativeButton = getArguments().getString(TAG_NEGATIVE_BUTTON);
        }

        if (savedInstanceState != null) {
            // Restore value from saved state
            dateTime.setTime(new Date(savedInstanceState.getLong(STATE_DATETIME)));
        } else {
            // Init with values set
            dateTime.set(year, month, day, hour, minute);
        }

        LayoutInflater inflater = LayoutInflater.from(getActivity());
        dateTimeLayout = inflater.inflate(R.layout.dialog_switch_datetime_picker,
                (ViewGroup) getActivity().findViewById(R.id.datetime_picker));

        // ViewGroup add
        viewGroup = (ViewGroup) dateTimeLayout.findViewById(R.id.section_add);

        // Set label
        TextView labelView = (TextView) dateTimeLayout.findViewById(R.id.label);
        if(mLabel != null)
            labelView.setText(mLabel);
        else
            labelView.setText(getString(R.string.label_datetime_dialog));

        //TODO block listener for bug button (fast)
        // Switch date to time and reverse
        switcher = (ViewAnimator) dateTimeLayout.findViewById(R.id.dateSwitcher);
        ImageButton buttonSwitch = (ImageButton) dateTimeLayout.findViewById(R.id.button_switch);
        buttonSwitch.setBackgroundColor(Color.TRANSPARENT);
        buttonSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switcher.showNext();
            }
        });

        // Construct TimePicker
        // Values header time
        View timeHeaderValues = dateTimeLayout.findViewById(R.id.time_header_values);
        View.OnClickListener onTimeClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positionView = ViewDatePosition.VIEW_HOURS_AND_MINUTES.getPosition();
                if(switcher.getDisplayedChild() != positionView)
                    switcher.setDisplayedChild(positionView);
            }
        };
        timeHeaderValues.setOnClickListener(onTimeClickListener);

        // Init simple date format if null
        if(dayAndMonthSimpleDate == null)
            dayAndMonthSimpleDate = new SimpleDateFormat("MMMM dd", Locale.getDefault());
        SwitchTimePicker timePicker = new SwitchTimePicker(getContext(), new SwitchTimePicker.OnTimeSetListener() {
            @Override
            public void onTimeSet(RadialPickerLayout view, int hourOfDay, int minute) {

            }
        }, 1, 1, false, false);
        timePicker.onCreateView(dateTimeLayout, savedInstanceState);
        timePicker.setOnClickTimeListener(onTimeClickListener);

        // Construct DatePicker
        // Values header month day
        View monthAndDayHeaderValues = dateTimeLayout.findViewById(R.id.date_picker_month_and_day);
        View.OnClickListener onMonthAndDayClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positionView = ViewDatePosition.VIEW_MONTH_AND_DAY.getPosition();
                if(switcher.getDisplayedChild() != positionView)
                    switcher.setDisplayedChild(positionView);
            }
        };
        monthAndDayHeaderValues.setOnClickListener(onMonthAndDayClickListener);

        // Values header year
        View yearHeaderValues = dateTimeLayout.findViewById(R.id.date_picker_year);
        View.OnClickListener onYearClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int positionView = ViewDatePosition.VIEW_YEAR.getPosition();
                if(switcher.getDisplayedChild() != positionView)
                    switcher.setDisplayedChild(positionView);
            }
        };
        yearHeaderValues.setOnClickListener(onYearClickListener);

        SwitchDatePicker datePicker = new SwitchDatePicker(getContext(), new SwitchDatePicker.OnDateSetListener() {
            @Override
            public void onDateSet(int year, int month, int day) {
                dateTime.set(Calendar.YEAR, year);
                dateTime.set(Calendar.MONTH, month);
                dateTime.set(Calendar.DAY_OF_MONTH, day);
            }
        }, 1980, 1, 1, dayAndMonthSimpleDate, false);
        datePicker.onCreateView(dateTimeLayout, savedInstanceState);
        datePicker.setOnMonthAndDayClickListener(onMonthAndDayClickListener);
        datePicker.setOnYearlickListener(onYearClickListener);

        // Assign buttons
        AlertDialog.Builder db = new AlertDialog.Builder(getActivity());
        db.setView(dateTimeLayout);
        if(mPositiveButton == null)
            mPositiveButton = getString(R.string.positive_button_datetime_picker);
        db.setPositiveButton(mPositiveButton, new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if(mListener !=null)
                            mListener.onPositiveButtonClick(dateTime.getTime());
                    }
                });
        if(mNegativeButton == null)
            mNegativeButton = getString(R.string.negative_button_datetime_picker);
        db.setNegativeButton(mNegativeButton, new
                DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Close dialog
                        if(mListener !=null)
                            mListener.onNegativeButtonClick(dateTime.getTime());
                    }
                });

        AlertDialog alertDialog = db.create();
        //*/
        alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                dateTimeLayout.setMinimumWidth(viewGroup.getWidth() + switcher.getWidth());
            }
        });
        //*/

        return alertDialog;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    /**
     * TODO
     * @return
     */
    public SimpleDateFormat getSimpleDateMonthAndDayFormat() {
        return dayAndMonthSimpleDate;
    }

    /**
     * Assign a SimpleDateFormat like "d MMM" to show formatted DateTime
     * @param simpleDateFormat
     */
    public void setSimpleDateMonthAndDayFormat(SimpleDateFormat simpleDateFormat) {
        // TODO REGEX for dd MM
        this.dayAndMonthSimpleDate = simpleDateFormat;
    }


    /**
     * Callback class for assign action on positive and negative button
     */
    public interface OnButtonClickListener {
        void onPositiveButtonClick(Date date);
        void onNegativeButtonClick(Date date);
    }

    public enum ViewDatePosition {
        VIEW_HOURS_AND_MINUTES(0), VIEW_MONTH_AND_DAY(1), VIEW_YEAR(2);

        private int positionSwitch;

        ViewDatePosition(int position) {
            this.positionSwitch = position;
        }

        public int getPosition() {
            return positionSwitch;
        }
    }
}

