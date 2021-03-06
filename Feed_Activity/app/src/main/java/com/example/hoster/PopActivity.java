package com.example.hoster;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.travijuu.numberpicker.library.NumberPicker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * creates a meal and writes it to the server
 */
public class PopActivity extends AppCompatActivity {
    Button createMeal;
    EditText pickDate;
    String date;
    Calendar mCalendar;
    SimpleDateFormat mSimpleDateFormat;
    CheckBox kosher;
    CheckBox Halal;
    CheckBox Vegan;
    CheckBox Veggie;
    AutoCompleteTextView location;
    String loc;
    HashMap<String, Boolean> foodRests;
    int maxGuests;
    private Activity mActivity;

    CheckBox beer;
    CheckBox drinks;
    CheckBox dessert;
    CheckBox flowers;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        

        setContentView(R.layout.add_new_meal_popup);
        foodRests = new HashMap<String, Boolean>();

        mSimpleDateFormat = new SimpleDateFormat("dd/MM/yyyy h:mm a", Locale.getDefault());
        mActivity = this;

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);


        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width), (int) (height ));
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.gravity = Gravity.TOP;


        getWindow().setAttributes(params);




        pickDate = (EditText) findViewById(R.id.mealDateAdd);
        pickDate.setEnabled(true);
        pickDate.setKeyListener(null);
        pickDate.setFocusable(false);
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mCalendar = Calendar.getInstance();
                new DatePickerDialog(mActivity, mDateDataSet, mCalendar.get(Calendar.YEAR),
                        mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });

        /* finds elements within the activity */
        kosher = (CheckBox)findViewById(R.id.Kosher);
        Halal = (CheckBox)findViewById(R.id.Halal);
        Vegan = (CheckBox)findViewById(R.id.vegan);
        Veggie = (CheckBox)findViewById(R.id.Vegetarian);

        beer = (CheckBox) findViewById(R.id.beer_box);
        drinks = (CheckBox) findViewById(R.id.drink_box);
        flowers = (CheckBox) findViewById(R.id.flowers_box);
        dessert = (CheckBox) findViewById(R.id.dessert_box);


        /* set location autocomplete */
        ArrayAdapter<String> adapter = new ArrayAdapter<String>
                (this, android.R.layout.simple_dropdown_item_1line, NEIGHBORHOODS_JLM);

        location = (AutoCompleteTextView) findViewById(R.id.location);
        location.setAdapter(adapter);

        final NumberPicker numberPicker = (NumberPicker) findViewById(R.id.number_picker);

        numberPicker.setValue(5);
        numberPicker.setUnit(1);


        /* set create meal to add meal on server */
        createMeal = findViewById(R.id.addNewMeal);
        createMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText titleAdd = (EditText) findViewById(R.id.mealTitleAdd);
                String title = titleAdd.getText().toString();
                String description = getInfoFromTextbox(R.id.mealDescriptionAdd);
                loc = getInfoFromTextbox(R.id.location);
                setFoodRests();
                maxGuests = numberPicker.getValue();

                HashMap<String, String> neededInput = new HashMap<>();


                if(beer.isChecked()){
                    neededInput.put("beer", Meal.NEEDED);
                }
                else {neededInput.put("beer", null);}
                if(drinks.isChecked()){
                    neededInput.put("drinks", Meal.NEEDED);
                }
                else{
                    neededInput.put("drinks", null);
                }
                if(flowers.isChecked()){
                    neededInput.put("flowers", Meal.NEEDED);
                }
                else{
                    neededInput.put("flowers", null);
                }
                if(dessert.isChecked()){
                    neededInput.put("dessert",Meal.NEEDED);
                }
                else {
                    neededInput.put("dessert",null);
                }


                int id = MainActivity.sev.addMeal(MainActivity.userId, title, new ArrayList<String>(),
                        foodRests, description, maxGuests, loc, date, neededInput, MainActivity.userMail);

                MainActivity.adapter.notifyDataSetChanged();
                finish();
            }

        });


        Button closePopUp;
        closePopUp = findViewById(R.id.closePopUpButton);
        closePopUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });


    }


    private static final String[] NEIGHBORHOODS_JLM = new String[] {
            "Musrara", "Ein Kerem", "Nachlaot", "Jewish Quarter", "Yemin Moshe",
            "Old City", "Machne Yehuda", "Emek Refaim", "Rehavia", "Rasko", "Talpiot",
            "Nayot", "Mamila", "Mea' She'arim", "Arnona", "Holyland", "Mishkanot She'ananim",
            "Motza", "Geula", "Katamon", "Kiryat haYovel", "Givat Ram", "Har Nof", "Migrash haRusim",
            "Bait vaGan", "Givat Shaul", "Malha", "Talbiya", "Makor Baruch", "Nachalat Shiv'a",
            "Geulim Bak'a", "Kerem Avraham", "Kiryat Menachem", "Givat Masuaa", "Givat Mordechai",
            "Kiryat haMemshala", "Hutzot haYotzer", "Ramat Sharet", "Romema", "Pat", "Sanhedriya",
            "Bucharim", "Mahane Israel", "She'arey Hesed", "Ir Ganim Gimel", "Hameshulash", "Ramat Beit Hakerem",
            "Makor Hayim", "Kiryat haLeom", "Ramat Deniya", "Tel Arza"
    };
    /**finish
     *
     */

    private void setFoodRests(){

        if (kosher.isChecked()) {
            foodRests.put("Kosher", true);
        } else {
            foodRests.put("Kosher", false);
        }

        if (Halal.isChecked()) {
            foodRests.put("Halal", true);
        } else {
            foodRests.put("Halal", false);
        }

        if (Vegan.isChecked()) {
            foodRests.put("Vegan", true);
        } else {
            foodRests.put("Vegan", false);
        }

        if (Veggie.isChecked()) {
            foodRests.put("Vegetarian", true);
        } else {
            foodRests.put("Vegetarian", false);
        }
    }

    private final DatePickerDialog.OnDateSetListener mDateDataSet = new DatePickerDialog.OnDateSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            mCalendar.set(Calendar.YEAR, year);
            mCalendar.set(Calendar.MONTH, month);
            mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            new TimePickerDialog(mActivity, mTimeDataSet, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE), false).show();
        }
    };

    private final TimePickerDialog.OnTimeSetListener mTimeDataSet = new TimePickerDialog.OnTimeSetListener() {
        @RequiresApi(api = Build.VERSION_CODES.N)
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            mCalendar.set(Calendar.MINUTE, minute);
            String value = mSimpleDateFormat.format(mCalendar.getTime());
            pickDate.setText(value);
            date = value;
        }
    };

    /*
     * receives data from user by i"d
     *
     * @param id - textbox ID
     * @return String representing that data
     */
    private String getInfoFromTextbox(int id) {
        EditText name = (EditText) findViewById(id);
        return name.getText().toString();
    }


    ;}
