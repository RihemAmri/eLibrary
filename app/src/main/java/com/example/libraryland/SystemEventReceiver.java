package com.example.libraryland;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import com.google.android.material.snackbar.Snackbar;
public class SystemEventReceiver extends BroadcastReceiver {
    private static final String CHANNEL_ID = "time_set_channel";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d("SystemEventReceiver", "Received action: " + action);

        if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {
            // Redémarrage du système
            /*new android.os.Handler(context.getMainLooper()).postDelayed(() -> {
                Toast.makeText(context.getApplicationContext(), "Boot completed detected!", Toast.LENGTH_LONG).show();
            }, 1000); // Affichage d'un message simple
            */
            handleBootCompleted(context);
        } else if ("android.intent.action.TIME_SET".equals(action)) { // Changez ici
            Log.d("SystemEventReceiver", "Time set action received");
            //showSnackbar(context, "Time set action detected!");
            // Créer le canal de notification si nécessaire
            createNotificationChannel(context);

            // Afficher la notification
            showNotification(context);

            handleTimeChanged(context);

        }
    }

    private void handleBootCompleted(Context context) {
        new android.os.Handler(context.getMainLooper()).postDelayed(() -> {
            // Votre logique Firebase ici
            DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference("RecentDates");
            datesRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        long lastCreateDate = snapshot.child("lastCreateDate").getValue(Long.class);
                        long lastModifyDate = snapshot.child("lastModifyDate").getValue(Long.class);
                        long lastDeleteDate = snapshot.child("lastDeleteDate").getValue(Long.class);
                        long lastConsultDate = snapshot.child("lastConsultDate").getValue(Long.class);

                        // Affichage du message initial
                        //Toast.makeText(context, "Recent Dates", Toast.LENGTH_LONG).show();

                        // Afficher chaque date dans un Toast séparé
                        if (lastCreateDate != 0) {
                            Toast.makeText(context, "Recent Dates : Creation: " + formatDate(lastCreateDate), Toast.LENGTH_LONG).show();
                        }

                        if (lastModifyDate != 0) {
                            Toast.makeText(context, "Modification: " + formatDate(lastModifyDate), Toast.LENGTH_LONG).show();
                        }

                        if (lastDeleteDate != 0) {
                            Toast.makeText(context, "Deletion: " + formatDate(lastDeleteDate), Toast.LENGTH_LONG).show();
                        }

                        if (lastConsultDate != 0) {
                            Toast.makeText(context, "Consultation: " + formatDate(lastConsultDate), Toast.LENGTH_LONG).show();
                        }

                    } else {
                        Toast.makeText(context, "No Recent date found", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("SystemEventReceiver", "Failed to load dates: " + error.getMessage());
                }
            });
        }, 3000); // Délai de 3 secondes
    }



    private String formatDate(long timestamp) {
        if (timestamp == 0) return "N/A";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date(timestamp));
    }



    private void handleTimeChanged(Context context) {
        // Récupérer la date actuelle du système
        long currentDate = System.currentTimeMillis();
        Log.d("SystemEventReceiver", "Current date: " + currentDate);

        // Accéder à Firebase pour obtenir la date la plus récente
        DatabaseReference datesRef = FirebaseDatabase.getInstance().getReference("RecentDates");
        datesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    long lastCreateDate = snapshot.child("lastCreateDate").getValue(Long.class);
                    long lastModifyDate = snapshot.child("lastModifyDate").getValue(Long.class);
                    long lastDeleteDate = snapshot.child("lastDeleteDate").getValue(Long.class);
                    long lastConsultDate = snapshot.child("lastConsultDate").getValue(Long.class);

                    // Trouver la date la plus récente
                    long mostRecentDate = Math.max(Math.max(lastCreateDate, lastModifyDate), Math.max(lastDeleteDate, lastConsultDate));
                    Log.d("SystemEventReceiver", "Most recent date from Firebase: " + mostRecentDate);

                    // Comparer la date actuelle avec la plus récente date
                    if (currentDate < mostRecentDate) {
                        Log.d("SystemEventReceiver", "Warning: System date is earlier than the most recent date in Firebase.");

                        // Afficher le Toast sur le thread principal
                        new android.os.Handler(Looper.getMainLooper()).post(() ->
                                Toast.makeText(context,
                                        "Warning: System date is earlier than the most recent date in Firebase.",
                                        Toast.LENGTH_LONG).show()
                        );
                    } else {
                        Log.d("SystemEventReceiver", "System date is valid.");
                    }
                } else {
                    Log.e("SystemEventReceiver", "No dates found in Firebase.");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("SystemEventReceiver", "Failed to load dates: " + error.getMessage());
            }
        });
    }
    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Time Set Channel";
            String description = "Channel for Time Set notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    private void showNotification(Context context) {
        // Vérifier la permission avant d'envoyer une notification
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                return; // Permission manquante, ne pas envoyer la notification
            }
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.baseline_email_24)
                .setContentTitle("Time Set Detected")
                .setContentText("The system time has been changed.")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(1, builder.build());
    }




}
