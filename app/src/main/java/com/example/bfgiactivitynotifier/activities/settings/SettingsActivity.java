package com.example.bfgiactivitynotifier.activities.settings;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;

import com.example.bfgiactivitynotifier.R;
import com.example.bfgiactivitynotifier.activities.profile.ProfileActivity;
import com.example.bfgiactivitynotifier.common.CommonClass;
import com.example.bfgiactivitynotifier.databinding.ActivitySettingsBinding;
import com.example.bfgiactivitynotifier.faculty.tasks_activity.TasksActivity;
import com.example.bfgiactivitynotifier.models.UserTasks;
import com.example.bfgiactivitynotifier.signin.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySettingsBinding activitySettingsBinding = DataBindingUtil.setContentView(this, R.layout.activity_settings);

        activitySettingsBinding.backButton.setOnClickListener(view-> finish());

        activitySettingsBinding.openProfile.setOnClickListener(view-> startActivity(new Intent(this, ProfileActivity.class)));

        activitySettingsBinding.downloadData.setOnClickListener(view -> downloadDataInExcel());

        activitySettingsBinding.showTasksAddedByMe.setOnClickListener(view -> {
            Intent intent = new Intent(this, TasksActivity.class);
            intent.putExtra("textOnAction", "Tasks Added By Me");
            startActivity(intent);
        });

        activitySettingsBinding.logOut.setOnClickListener(view -> new AlertDialog.Builder(this)
                .setTitle("Log Out")
                .setMessage("Do you really want to logout?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> {
                    SharedPreferences sharedPreferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();
                    sharedPreferences = getSharedPreferences("notifications_sp", Context.MODE_PRIVATE);
                    sharedPreferences.edit().clear().apply();
                    finishAffinity();
                    Intent intent = new Intent(this, SignInActivity.class);
                    startActivity(intent);
                })
                .setNegativeButton(android.R.string.no, null)
                .show());
    }

    private void downloadDataInExcel() {
        new AlertDialog.Builder(this)
                .setTitle("Download Tasks Data")
                .setMessage("Import this week's data in excel format?")
                .setPositiveButton(android.R.string.yes, (dialogInterface, i) -> download())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    List<UserTasks> userTasksList = new ArrayList<>();
    private void download() {
        Toast.makeText(this, "Fetching Data", Toast.LENGTH_SHORT).show();
        FirebaseFirestore.getInstance().collection("activities_data").orderBy("added_on", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        List<DocumentSnapshot> documentSnapshotList = task.getResult().getDocuments();
                        for (DocumentSnapshot documentSnapshot: documentSnapshotList){
                            UserTasks userTasks = getData(documentSnapshot);
                            if(userTasks != null){
                                userTasksList.add(userTasks);
                            }
                        }
                        createExcelWorkbook();
                    }
                });
    }

    // New Workbook
    private final Workbook workbook = new HSSFWorkbook();


    /**
     * Method: Generate Excel Workbook
     */
    public void createExcelWorkbook() {
        // Global Variables
        Cell cell;

        // Cell style for header row
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.AQUA.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(CellStyle.ALIGN_CENTER);

        // New Sheet
        Sheet sheet;
        String EXCEL_SHEET_NAME = "Sheet1";
        try{
            sheet = workbook.createSheet(EXCEL_SHEET_NAME);
            // Generate column headings
            Row row = sheet.createRow(0);

            cell = row.createCell(0);
            cell.setCellValue("Task Plan Authority");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(1);
            cell.setCellValue("Task Name");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(2);
            cell.setCellValue("Task Type");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(3);
            cell.setCellValue("Follow Up Taken By");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(4);
            cell.setCellValue("Action Taker");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(5);
            cell.setCellValue("Department");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(6);
            cell.setCellValue("Added By");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(7);
            cell.setCellValue("Start Date");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(8);
            cell.setCellValue("End Date");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(9);
            cell.setCellValue("Status");
            cell.setCellStyle(cellStyle);

            cell = row.createCell(10);
            cell.setCellValue("Delay Reason");
            cell.setCellStyle(cellStyle);

            for (int i = 0; i < userTasksList.size(); i++) {
                // Create a New Row for every new entry in list
                Row rowData = sheet.createRow(i + 1);

                // Create Cells for each row
                cell = rowData.createCell(0);
                cell.setCellValue(userTasksList.get(i).getTask_plan_authority());

                cell = rowData.createCell(1);
                cell.setCellValue(userTasksList.get(i).getTask_name());

                cell = rowData.createCell(2);
                cell.setCellValue(userTasksList.get(i).getTask_type());

                cell = rowData.createCell(3);
                cell.setCellValue(userTasksList.get(i).getFollow_up_taken_by());

                cell = rowData.createCell(4);
                cell.setCellValue(userTasksList.get(i).getAction_taker());

                cell = rowData.createCell(5);
                cell.setCellValue(userTasksList.get(i).getDepartment());

                cell = rowData.createCell(6);
                cell.setCellValue(userTasksList.get(i).getAdded_by_name());

                cell = rowData.createCell(7);
                cell.setCellValue(userTasksList.get(i).getStart_date());

                cell = rowData.createCell(8);
                cell.setCellValue(userTasksList.get(i).getEnd_date());

                cell = rowData.createCell(9);
                cell.setCellValue(userTasksList.get(i).getStatus());

                cell = rowData.createCell(10);
                cell.setCellValue((RichTextString) userTasksList.get(i).getDelay_reason());
            }

            storeFile();

        }catch (Exception e){
            Toast.makeText(this, "Error: "+e, Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void storeFile() {
        if(!CommonClass.isExternalStorageAvailable() || CommonClass.isExternalStorageReadOnly()){
            Toast.makeText(this, "Can't read storage!", Toast.LENGTH_SHORT).show();
        }else{
            File file = new File(this.getExternalFilesDir(null), "TaskSheet.xlsx");
            FileOutputStream fileOutputStream = null;

            try {
                fileOutputStream = new FileOutputStream(file);
                workbook.write(fileOutputStream);
                Log.e(TAG, "Writing file" + file);
                Toast.makeText(this, "File Saved in "+file, Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(this, "Error writing Exception: "+e, Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Failed to save file due to Exception: "+e, Toast.LENGTH_SHORT).show();
            } finally {
                try {
                    if (null != fileOutputStream) {
                        fileOutputStream.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private UserTasks getData(DocumentSnapshot document) {
        UserTasks userTasks = null;
        String auth = CommonClass.modelUserData.getDesignation();
        if(CommonClass.modelUserData.getDepartment().equals(document.get("department"))
                && CommonClass.checkDateRange(
                Objects.requireNonNull(document.get("start_date")).toString(),
                Objects.requireNonNull(document.get("end_date")).toString())
                &&
                (Objects.equals(document.get("task_plan_authority"), CommonClass.modelUserData.getFull_name())
                        || Objects.equals(document.get("action_taker"), CommonClass.modelUserData.getFull_name())
                        || Objects.equals(document.get("action_taker"), "All Faculty")
                        || Objects.equals(document.get("added_by"), Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                        || auth.equals("Principal")
                        || (auth.equals("Faculty") && Objects.equals(document.get("added_by_designation"), auth))
                        || (auth.equals("HOD") && (Objects.equals(document.get("added_by_designation"), auth) || Objects.equals(document.get("added_by_designation"), "Faculty")))
                        || (auth.equals("Dean") && (!Objects.equals(document.get("added_by_designation"), "Principal")))
                )
        ){
            userTasks = document.toObject(UserTasks.class);
            Objects.requireNonNull(userTasks).setDocument_id(document.getId());
            int colorTask = R.color.completed;
            if(!userTasks.isCompleted()){
                try {
                    String start = userTasks.getStart_date();
                    String end = userTasks.getEnd_date();
                    String status = new CommonClass().getTasksStatus(start, end);
                    switch (status){
                        case "Upcoming Tasks":
                            colorTask = R.color.upcoming;
                            break;
                        case "In Progress":
                            colorTask = R.color.inProgress;
                            break;
                        case "Not Complete":
                            colorTask = R.color.notComplete;
                    }
                    userTasks.setColor(ContextCompat.getColor(this, colorTask));
                    userTasks.setStatus(status);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }else{
                Objects.requireNonNull(userTasks).setStatus("Completed Tasks");
                userTasks.setColor(ContextCompat.getColor(this, colorTask));
            }

        }
        return userTasks;
    }
}