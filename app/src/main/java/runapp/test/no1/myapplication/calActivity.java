package runapp.test.no1.myapplication;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import runapp.test.no1.myapplication.library.CalendarAdapter;
import runapp.test.no1.myapplication.library.DayInfo;
import runapp.test.no1.myapplication.library.OnSwipeTouchListener;

public class calActivity extends AppCompatActivity {

    private TextView tvCalendarTitle;
    private TextView tvSelectedDate;
    private GridView gvCalendar;

    private ArrayList<DayInfo> arrayListDayInfo;
    public ArrayList<Date> selectedDateList;

    Calendar mThisMonthCalendar;
    CalendarAdapter mCalendarAdapter;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd(EEE)", Locale.getDefault());
    Date selectedDate = new Date();

    private ImageButton leftBtn;
    private ImageButton rightBtn;

    public Integer id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cal);

        //Button button = (Button)findViewById(R.id.planSetBtn);
        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogSelectOption();
            }
        });*/

        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);

        checkFunction();

        tvCalendarTitle = findViewById(R.id.tv_calendar_title);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        gvCalendar = findViewById(R.id.gv_calendar);

        leftBtn = findViewById(R.id.leftBtn);
        rightBtn = findViewById(R.id.rightBtn);

        leftBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mThisMonthCalendar.add(Calendar.MONTH, -1);
                getCalendar(mThisMonthCalendar.getTime());
            }
        });

        rightBtn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                mThisMonthCalendar.add(Calendar.MONTH, +1);
                getCalendar(mThisMonthCalendar.getTime());
            }
        });

        gvCalendar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d("selectedDate",((DayInfo)view.getTag()).getDate().toString());

                setSelectedDate(((DayInfo)view.getTag()).getDate());

                tvSelectedDate.setText(sdf.format(mCalendarAdapter.selectedDate));

                mCalendarAdapter.notifyDataSetChanged();
            }
        });

        arrayListDayInfo = new ArrayList<>();
        selectedDateList = new ArrayList<>();

    }

    @Override
    public void onResume() {
        super.onResume();

        mThisMonthCalendar = Calendar.getInstance();
        getCalendar(mThisMonthCalendar.getTime());
    }

    public void setSelectedDate(Date date){
        selectedDate = date;
        //selectedDateList.add(date);

        if(mCalendarAdapter != null) {
            mCalendarAdapter.selectedDate = date;
            //mCalendarAdapter.selectedDateList = selectedDateList;
        }
    }

    private void getCalendar(Date dateForCurrentMonth){
        int dayOfWeek;
        int thisMonthLastDay;

        arrayListDayInfo.clear();
        selectedDateList.clear();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateForCurrentMonth);

        calendar.set(Calendar.DATE, 1);//1일로 변경
        dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);//1일의 요일 구하기
        Log.d("CalendarTest", "dayOfWeek = " + dayOfWeek+"");

        if(dayOfWeek == Calendar.SUNDAY){ //현재 달의 1일이 무슨 요일인지 검사
            dayOfWeek += 7;
        }

        thisMonthLastDay = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        setCalendarTitle();

        DayInfo day;

        calendar.add(Calendar.DATE, -1*(dayOfWeek-1)); //현재 달력화면에서 보이는 지난달의 시작일
        for(int i=0; i<dayOfWeek-1; i++){
            day = new DayInfo();
            day.setDate(calendar.getTime());
            day.setInMonth(false);
            arrayListDayInfo.add(day);

            calendar.add(Calendar.DATE, +1);
        }

        for(int i=1; i <= thisMonthLastDay; i++){
            day = new DayInfo();
            day.setDate(calendar.getTime());
            day.setInMonth(true);
            arrayListDayInfo.add(day);

            calendar.add(Calendar.DATE, +1);
        }

        for(int i=1; i<42-(thisMonthLastDay+dayOfWeek-1)+1; i++) {
            day = new DayInfo();
            day.setDate(calendar.getTime());
            day.setInMonth(false);
            arrayListDayInfo.add(day);

            calendar.add(Calendar.DATE, +1);
        }

        mCalendarAdapter = new CalendarAdapter(arrayListDayInfo, selectedDate);
        gvCalendar.setAdapter(mCalendarAdapter);

        tvSelectedDate.setText(sdf.format(selectedDate));
    }

    private void setCalendarTitle(){
        StringBuilder sb = new StringBuilder();
        String monthStr;
        Integer monthInt = mThisMonthCalendar.get(Calendar.MONTH) + 1;
        if(monthInt < 10){
            monthStr = "0"+ monthInt;
        }else{
            monthStr = monthInt.toString();
        }
        sb.append(mThisMonthCalendar.get(Calendar.YEAR))
                .append("/ ")
                .append(monthStr);
        tvCalendarTitle.setText(sb.toString());
    }

    public void checkFunction(){
        int permissioninfo = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if(permissioninfo == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,"SDCard(Write is available.)",Toast.LENGTH_SHORT).show();
        }else{
            if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.WRITE_EXTERNAL_STORAGE)){
                Toast.makeText(this, "Permission Discription",Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);

            }else{
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},100);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        String str = null;
        if(requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                str = "SD Card Write Permission is approved.";
            else str = "SD Card Write Permission is denyed.";
            Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
        }
    }


    public ArrayList<Date> loadFile(String title){
        ArrayList<Date> saveDateList = new ArrayList<>();
        Date saveDate = new Date();
        try{
            String path = getExternalPath();
            BufferedReader br = new BufferedReader(new FileReader(path+"mydiary/"+title + ".txt"));
            String readStr = "";
            String str = null;
            while(((str = br.readLine()) != null)){
                //readStr += str +"\n";
                saveDate = new SimpleDateFormat("yyyy-MM-dd").parse(str);
                saveDateList.add(saveDate);
            }
            br.close();

            return saveDateList;

        }catch (FileNotFoundException e){
            e.printStackTrace();
            Toast.makeText(this, "File not Found", Toast.LENGTH_SHORT).show();
        }catch (IOException e) {
            e.printStackTrace();
        }catch (ParseException e) {
            e.printStackTrace();
        }

        return saveDateList;
    }

    public void writeFile(String title, String body){
        try{
            String path = getExternalPath();
            String filename = title;

            BufferedWriter bw = new BufferedWriter(new FileWriter(path + "mydiary/" + filename + ".txt", false));
            bw.write(body);
            bw.close();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String getExternalPath(){
        String sdPath ="";
        String ext = Environment.getExternalStorageState();
        if(ext.equals(Environment.MEDIA_MOUNTED)){
            sdPath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/";
        }else{
            sdPath  = getFilesDir() +"";

        }
        return sdPath;
    }

    public void deletefile(String title){
        String path = getExternalPath();

        File file = new File(path + "mydiary");

        File[] files = new File(path + "mydiary").listFiles();

        for(File f : files){
            if(title.equals(f.getName())){
                f.delete();
            }
        }
    }

    private void DialogSelectOption() {
        final List<String> ListItems = new ArrayList<>();
        ListItems.add("Full Plan");
        ListItems.add("15days Plan");
        ListItems.add("10days Plan");
        ListItems.add("5days Plan");
        final CharSequence[] items =  ListItems.toArray(new String[ ListItems.size()]);

        final List SelectedItems  = new ArrayList();
        int defaultItem = 0;
        SelectedItems.add(defaultItem);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose A Plan");
        builder.setSingleChoiceItems(items, defaultItem,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SelectedItems.clear();
                        SelectedItems.add(which);
                    }
                });
        builder.setPositiveButton("Ok",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String msg="";

                        if (!SelectedItems.isEmpty()) {
                            int index = (int) SelectedItems.get(0);
                            msg = ListItems.get(index);
                            if(msg.equals("Full Plan") ){
                                //writeFile(id.toString(), "2018-08-07");
                                String str = "2018-08-07";
                                try {
                                    Date saveDate = new SimpleDateFormat("yyyy-MM-dd").parse(str);
                                    selectedDateList.add(saveDate);
                                    //mCalendarAdapter.selectedDateList = selectedDateList;
                                    mCalendarAdapter.notifyDataSetChanged();
                                }catch (ParseException e){

                                }

                            }else if(msg.equals("15days Plan")){
                                String str = "2018-08-06";
                                try {
                                    Date saveDate = new SimpleDateFormat("yyyy-MM-dd").parse(str);
                                    selectedDateList.add(saveDate);
                                    //mCalendarAdapter.selectedDateList = selectedDateList;
                                    mCalendarAdapter.notifyDataSetChanged();
                                }catch (ParseException e){

                                }
                                //writeFile(id.toString(), "2018-08-06");
                            }else if(msg.equals("10days Plan")){
                                String str = "2018-08-05";
                                try {
                                    Date saveDate = new SimpleDateFormat("yyyy-MM-dd").parse(str);
                                    selectedDateList.add(saveDate);
                                    //mCalendarAdapter.selectedDateList = selectedDateList;
                                    mCalendarAdapter.notifyDataSetChanged();
                                }catch (ParseException e){

                                }
                                //writeFile(id.toString(), "2018-08-05");
                            }else if(msg.equals("5days Plan")){
                                String str = "2018-08-04";
                                try {
                                    Date saveDate = new SimpleDateFormat("yyyy-MM-dd").parse(str);
                                    selectedDateList.add(saveDate);
                                    //mCalendarAdapter.selectedDateList = selectedDateList;
                                    mCalendarAdapter.notifyDataSetChanged();
                                }catch (ParseException e){

                                }
                                //writeFile(id.toString(), "2018-08-04");
                            }
                        }
                        Toast.makeText(getApplicationContext(),
                                "Items Selected.\n"+ msg , Toast.LENGTH_LONG)
                                .show();
                    }
                });
        builder.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        builder.show();
    }

}
