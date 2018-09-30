package runapp.test.no1.myapplication.library;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import runapp.test.no1.myapplication.R;

public class CalendarAdapter extends BaseAdapter {
    private ArrayList<DayInfo> arrayListDayInfo;
    public Date selectedDate;
    //public ArrayList<Date> selectedDateList;

    public CalendarAdapter(ArrayList<DayInfo> arrayLIstDayInfo, Date date) {
        this.arrayListDayInfo = arrayLIstDayInfo;
        this.selectedDate = date;
        //this.selectedDateList = selectedDateList;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return arrayListDayInfo.size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return arrayListDayInfo.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        DayInfo day = arrayListDayInfo.get(position);

        if(convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.day, parent, false);
        }

        if(day != null){
            TextView tvDay = convertView.findViewById(R.id.day_cell_tv_day);
            tvDay.setText(day.getDay());

            ImageView ivSelected = convertView.findViewById(R.id.iv_selected);
            //ImageView ivPlan = convertView.findViewById(R.id.iv_plan);

          /*  for (Date selDate : selectedDateList) {
                if(day.isSameDay(selDate)){
                    Log.d("position",position+"");
                    Log.d("selDate",selDate.toString());
                    ivPlan.setVisibility(View.VISIBLE);
                }else{
                    //ivSelected_plan.setVisibility(View.INVISIBLE);
                }
            }*/

            if(day.isSameDay(selectedDate)){
                ivSelected.setVisibility(View.VISIBLE);
                //ivPlan.setVisibility(View.INVISIBLE);
            }else{
                ivSelected.setVisibility(View.INVISIBLE);
            }

            if(day.isInMonth()){
                if((position%7 + 1) == Calendar.SUNDAY){
                    tvDay.setTextColor(Color.RED);
                }else if((position%7 + 1) == Calendar.SATURDAY){
                    tvDay.setTextColor(Color.BLUE);
                }else{
                    tvDay.setTextColor(Color.BLACK);
                }
            }else{
                tvDay.setTextColor(Color.GRAY);
            }
        }
        convertView.setTag(day);

        return convertView;
    }




}