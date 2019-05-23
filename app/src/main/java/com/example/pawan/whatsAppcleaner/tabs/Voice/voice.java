package com.example.pawan.whatsAppcleaner.tabs.Voice;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.pawan.whatsAppcleaner.DataHolder;
import com.example.pawan.whatsAppcleaner.adapters.innerAdapeters.InnerDetailsAdapter;
import com.example.pawan.whatsAppcleaner.adapters.innerAdapeters.InnerDetailsAdapter_audio;
import com.example.pawan.whatsAppcleaner.adapters.innerAdapeters.InnerDetailsAdapter_image;
import com.example.pawan.whatsAppcleaner.datas.FileDetails;
import com.example.pawan.whatsAppcleaner.R;
import com.example.pawan.whatsAppcleaner.tabs.FilesFragment;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;


public class voice extends AppCompatActivity implements  InnerDetailsAdapter_audio.OnCheckboxListener {


    RecyclerView recyclerView;
    Button button;
    private ImageView no_files;
    private InnerDetailsAdapter_audio innerDetailsAdapterAudio;
    private ArrayList<FileDetails> innerdatalist = new ArrayList<>();


    private double len;
    private String byteMake;
    private static final long GiB = 1024 * 1024 * 1024;
    private static final long MiB = 1024 * 1024;
    private static final long KiB = 1024;
    private String path = Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WallPaper";

    private ProgressDialog progressDialog;
    private ArrayList<FileDetails> filesToDelete = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doc_activity);

        recyclerView = findViewById(R.id.recycler_view);
        button = findViewById(R.id.delete);
        no_files = findViewById(R.id.nofiles);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(voice.this)
                        .setMessage("Are you sure you want to delete selected files?")
                        .setCancelable(true)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int success = -1;
                                ArrayList<FileDetails> deletedFiles = new ArrayList<>();

                                for (FileDetails details : filesToDelete) {
                                    File file = new File(details.getPath());
                                    if (file.exists()) {
                                        if (file.delete()) {
                                            deletedFiles.add(details);
                                            if (success == 0) {
                                                return;
                                            }
                                            success = 1;
                                        } else {
                                            Log.e("TEST", "" + file.getName() + " delete failed");
                                            success = 0;
                                        }
                                    } else {
                                        Log.e("TEST", "" + file.getName() + " doesn't exists");
                                        success = 0;
                                    }
                                }

                                filesToDelete.clear();

                                for (FileDetails deletedFile : deletedFiles) {
                                    innerdatalist.remove(deletedFile);
                                }
                                innerDetailsAdapterAudio.notifyDataSetChanged();
                                if (success == 0) {
                                    Toast.makeText(voice.this, "Couldn't delete some files", Toast.LENGTH_SHORT).show();
                                } else if (success == 1) {
                                    Toast.makeText(voice.this, "Deleted successfully", Toast.LENGTH_SHORT).show();
                                }
                                button.setText("Delete Selected Items (0B)");
                                button.setTextColor(Color.parseColor("#A9A9A9"));
                            }
                        })
                        .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        }).create().show();
            }
        });

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //Need to ask permission again or close the app
        } else {
            String path = Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WhatsApp Voice Notes";


            File directory = new File(path);

            ArrayList<FileDetails> fileList1 = new ArrayList<>();

            File[] results = directory.listFiles();
            if (results != null) {
                for (File file : results) {
                    if (file.isDirectory()) {
                        File[] res = file.listFiles();
                        Log.e("Files", String.valueOf(res.length));

                        for (int j = 0; j < res.length; j++) {

                            FileDetails fileDetails = new FileDetails();
                              fileDetails.setName(res[j].getName());
                              fileDetails.setPath(res[j].getPath());
                              fileDetails.setImage(R.drawable.voice);
                              fileDetails.setColor(R.color.orange);
                              fileDetails.setSize("" + getFileSize(res[j]));
                              innerdatalist.add(fileDetails);
                        }
                    }
                }
               /// innerdatalist = fileList1;
                Log.e("Files", "files found: " + fileList1.toString());
            } else { Log.e("Files", "No files found in " + directory.getName());
            }
            if (innerdatalist.isEmpty()){
                no_files.setVisibility(View.VISIBLE);
                no_files.setImageResource(R.drawable.file);
            }
        }
        innerDetailsAdapterAudio = new InnerDetailsAdapter_audio(this, innerdatalist, this);
        recyclerView.setAdapter(innerDetailsAdapterAudio);
    }

    @Override
    public void onCheckboxClicked(View view, ArrayList<FileDetails> pos) {
        filesToDelete.clear();

        for (FileDetails details : pos) {
            if (details.isSelected()) {
                filesToDelete.add(details);
            }
        }

        if (filesToDelete.size() > 0) {

            long totalFileSize = 0;

            for (FileDetails details : filesToDelete) {
                File file = new File(details.getPath());
                totalFileSize += file.length();
            }

            String size = Formatter.formatShortFileSize(voice.this, totalFileSize);
            button.setText("Delete Selected Items (" + size + ")");
            button.setTextColor(Color.parseColor("#C103A9F4"));
        } else {
            button.setText("Delete Selected Items (0B)");
            button.setTextColor(Color.parseColor("#A9A9A9"));
        }
    }

//    private static class FetchFiles extends AsyncTask<String, Void, Object> {
//
//        private voice.FetchFiles.OnFilesFetched onFilesFetched;
//        private WeakReference<voice> wallpaperWeakReference;
//
//        FetchFiles(voice wallpaper, voice.FetchFiles.OnFilesFetched onFilesFetched) {
//            wallpaperWeakReference = new WeakReference<>(wallpaper);
//            this.onFilesFetched = onFilesFetched;
//        }
//
//
//        @Override
//        protected void onPreExecute() {
//            // display a progress dialog for good user experiance
//            wallpaperWeakReference.get().progressDialog = new ProgressDialog(wallpaperWeakReference.get().getApplicationContext());
//            wallpaperWeakReference.get().progressDialog.setMessage("Please Wait");
//            wallpaperWeakReference.get().progressDialog.setCancelable(false);
//            if (!wallpaperWeakReference.get().progressDialog.isShowing()) {
//                wallpaperWeakReference.get().progressDialog.show();
//            }
//        }
//
//        @Override
//        protected Object doInBackground(String... strings) {
//            String path = Environment.getExternalStorageDirectory().toString() + "/WhatsApp/Media/WallPaper";
//
//
//            File directory = new File(path);
//
//            ArrayList<FileDetails> fileList1 = new ArrayList<>();
//
//            File[] results = directory.listFiles();
//            if (results != null) {
//                for (int i = 0; i < results.length; i++) {
//                    if (results[i].isDirectory()) {
//                    } else {
//                        FileDetails fileDetails = new FileDetails();
//                        fileDetails.setName(results[i].getName());
//                        fileDetails.setPath(results[i].getPath());
//                        fileList1.add(fileDetails);
//                    }
//                }
//                Log.e("Files", "files found: " + fileList1.toString());
//            } else {
//                Log.e("Files", "No files found in " + directory.getName());
//            }
//            return fileList1;
//        }
//        @Override
//        protected void onPostExecute(Object o) {
//            List<FileDetails> files = (List<FileDetails>) o;
//            if (onFilesFetched != null) {
//                onFilesFetched.onFilesFetched(files);
//            }
//        }
//
//        public interface OnFilesFetched {
//            void onFilesFetched(List<FileDetails> fileDetails);
//        }
//    }
    private String getFileSize(File file) {
        NumberFormat format = new DecimalFormat("#.##");
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);
        final double length = file.length();

        if (file.isFile()) {
            if (length > GiB) {
                len = length / GiB;
                byteMake = "GB";
                return format.format(length / GiB) + " GB";
            } else if (length > MiB) {
                len = length / MiB;
                byteMake = "MB";
                return format.format(length / MiB) + " MB";
            } else if (length > KiB) {
                len = length / KiB;
                byteMake = "KB";
                return format.format(length / KiB) + " KB";
            }else
                return format.format(length) + " B";
        } else {
            len = 0;
        }
        return "";
    }

}